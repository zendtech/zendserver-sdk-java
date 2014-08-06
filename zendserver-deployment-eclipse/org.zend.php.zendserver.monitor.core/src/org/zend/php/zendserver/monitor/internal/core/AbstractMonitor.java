/*******************************************************************************
 * Copyright (c) 2012 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.php.zendserver.monitor.internal.core;

import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.php.internal.server.core.Server;
import org.eclipse.swt.widgets.Display;
import org.zend.php.server.core.utils.ServerUtils;
import org.zend.php.zendserver.deployment.core.targets.TargetsManagerService;
import org.zend.php.zendserver.monitor.core.Activator;
import org.zend.php.zendserver.monitor.core.IEventDetails;
import org.zend.php.zendserver.monitor.core.INotificationProvider;
import org.zend.php.zendserver.monitor.core.MonitorManager;
import org.zend.sdklib.application.ZendCodeTracing;
import org.zend.sdklib.manager.TargetsManager;
import org.zend.sdklib.monitor.IZendIssue;
import org.zend.sdklib.monitor.ZendMonitor;
import org.zend.sdklib.monitor.ZendMonitor.Filter;
import org.zend.sdklib.target.IZendTarget;
import org.zend.webapi.core.WebApiException;
import org.zend.webapi.core.connection.data.CodeTracingStatus;
import org.zend.webapi.core.connection.data.EventsGroupDetails;
import org.zend.webapi.core.connection.data.values.ZendServerVersion;
import org.zend.webapi.core.connection.response.ResponseCode;
import org.zend.webapi.internal.core.connection.exception.UnexpectedResponseCode;
import org.zend.webapi.internal.core.connection.exception.WebApiCommunicationError;

/**
 * Represents abstract monitor job. It contains monitor internal implementation
 * shared between different monitor types.
 * 
 * @author Wojciech Galanciak, 2012
 * 
 */
@SuppressWarnings("restriction")
public abstract class AbstractMonitor extends Job {

	private static final String PROVIDER_EXTENSION = "org.zend.php.zendserver.monitor.core.notificationProvider"; //$NON-NLS-1$

	private static final int JOB_DELAY_MIN = 2000;
	private static final int JOB_DELAY_MAX = 4 * JOB_DELAY_MIN;
	private static final int STEP = 30;

	private static INotificationProvider provider;

	protected String targetId;
	protected ZendMonitor monitor;
	protected long lastTime;

	private int jobDelay = JOB_DELAY_MIN;
	private int counter;
	private int offset;
	private ZendCodeTracing codeTracing;

	private boolean codeTracingEnabled;

	public AbstractMonitor(String targetId, String jobTitle) {
		super(jobTitle);
		this.targetId = targetId;
		this.codeTracingEnabled = true;
	}

	/**
	 * Start monitor job.
	 */
	public void start() {
		getProvider().showProgress(
				getName(),
				MessageFormat.format(Messages.ZendServerMonitor_TaskTitle,
						getServerName()), new IRunnableWithProgress() {

					public void run(IProgressMonitor monitor)
							throws InvocationTargetException,
							InterruptedException {
						if (!doStart(monitor)) {
							MonitorManager.removeMonitor(AbstractMonitor.this);
						}
					}
				});
	}

	public void stop() {
		boolean disableCodeTracing = false;
		if (getState() != Job.NONE) {
			disableCodeTracing = true;
		}
		cancel();
		if (disableCodeTracing) {
			disableCodeTacing();
		}
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		monitor.beginTask(MessageFormat.format(
				Messages.ZendServerMonitor_TaskTitle, getServerName()),
				IProgressMonitor.UNKNOWN);
		IZendTarget target = getTarget();
		if (target != null) {
			if (isZS6(target)) {
				doRunZS6(target);
			} else {
				doRunOld(target);
			}
			if (!monitor.isCanceled()) {
				monitor.done();
				if (counter > STEP && jobDelay < JOB_DELAY_MAX) {
					jobDelay *= 2;
					counter = 0;
				}
				this.schedule(jobDelay);
				return Status.OK_STATUS;
			}
		} else {
			getProvider().showErrorMessage(
					Messages.AbstractMonitor_NotificationTitle,
					Messages.AbstractMonitor_NoTargetError);
		}
		monitor.done();
		return Status.CANCEL_STATUS;
	}

	protected abstract void handleIssues(List<IZendIssue> issues,
			IZendTarget target);

	protected void showNonification(final IZendIssue issue,
			final String projectName, final String basePath, final int delay,
			final int actionsAvailable) {
		Display.getDefault().asyncExec(new Runnable() {

			public void run() {
				IEventDetails eventSource = EventDetails.create(issue
						.getIssue().getGeneralDetails().getUrl(), projectName,
						basePath, issue.getIssue());
				getProvider().showNonification(issue, targetId, eventSource,
						delay, actionsAvailable);
			}
		});
	}

	protected int checkActions(IZendIssue issue) {
		IZendTarget target = getTarget();
		if (TargetsManager.checkMinVersion(target, ZendServerVersion.v5_6_0)) {
			return codeTracingEnabled ? MonitorManager.REPEAT
					+ MonitorManager.CODE_TRACE : MonitorManager.REPEAT;
		}
		int result = 0;
		try {
			List<EventsGroupDetails> groups = issue.getGroupDetails();
			result += MonitorManager.REPEAT;
			if (codeTracingEnabled && groups != null && groups.size() == 1) {
				EventsGroupDetails group = groups.get(0);
				String traceId = group.getCodeTracing();
				if (traceId == null) {
					traceId = group.getEvent().getCodeTracing();
				}
				if (traceId != null && !traceId.isEmpty()) {
					result += MonitorManager.CODE_TRACE;
				}
			}
		} catch (Exception e) {
			Activator.log(e);
			return 0;
		}
		return result;
	}

	protected abstract IProject getProject(String urlString);

	protected static INotificationProvider getProvider() {
		if (provider == null) {
			IConfigurationElement[] elements = Platform.getExtensionRegistry()
					.getConfigurationElementsFor(PROVIDER_EXTENSION);
			for (IConfigurationElement element : elements) {
				if ("notificationProvider".equals(element.getName())) { //$NON-NLS-1$
					try {
						Object listener = element
								.createExecutableExtension("class"); //$NON-NLS-1$
						if (listener instanceof INotificationProvider) {
							provider = (INotificationProvider) listener;
							break;
						}
					} catch (CoreException e) {
						Activator.log(e);
					}
				}
			}
		}
		return provider;
	}

	protected void disableCodeTacing() {
		final String name = getServerName();
		getProvider().showProgress(
				getName(),
				MessageFormat.format(Messages.AbstractMonitor_DisablingJobName,
						name), new IRunnableWithProgress() {

					public void run(IProgressMonitor monitor)
							throws InvocationTargetException,
							InterruptedException {
						monitor.beginTask(
								MessageFormat
										.format(Messages.AbstractMonitor_DisablingJobName,
												name), IProgressMonitor.UNKNOWN);
						ZendCodeTracing codeTracing = new ZendCodeTracing(
								targetId);
						try {
							if (getTarget() != null) {
								CodeTracingStatus status = codeTracing
										.disable(true);
								if (status == null) {
									String m = MessageFormat
											.format(Messages.AbstractMonitor_InitializationJobConnectionError,
													name);
									handleError(monitor, m);
									return;
								}
							}
						} catch (WebApiCommunicationError e) {
							String m = MessageFormat
									.format(Messages.AbstractMonitor_InitializationJobConnectionError,
											name);
							handleError(monitor, m);
							return;
						} catch (UnexpectedResponseCode e) {
							ResponseCode code = e.getResponseCode();
							switch (code) {
							case UNSUPPORTED_API_VERSION:
								String m = MessageFormat
										.format(Messages.AbstractMonitor_InitializationJobUnsupportedVersion,
												name);
								handleError(monitor, m);
								return;
							default:
								break;
							}
						} catch (WebApiException e) {
							handleError(monitor, e.getMessage());
						}
					}
				});
	}

	protected boolean isZS6(IZendTarget target) {
		ZendServerVersion version = ZendServerVersion.byName(target
				.getProperty(IZendTarget.SERVER_VERSION));
		return version.getName().startsWith("6"); //$NON-NLS-1$
	}

	protected String getServerName() {
		Server server = ServerUtils.getServer(getTarget());
		return server != null ? server.getName() : targetId;
	}

	protected IZendTarget getTarget() {
		return TargetsManagerService.INSTANCE.getTargetManager().getTargetById(
				targetId);
	}

	private void doRunZS6(IZendTarget target) {
		List<IZendIssue> issues = null;
		if (monitor == null) {
			monitor = new ZendMonitor(targetId);
			lastTime = System.currentTimeMillis();
		} else {
			issues = monitor
					.getIssues(Filter.ALL_OPEN_EVENTS, lastTime, target);
			if (issues != null && issues.size() > 0) {
				counter = 0;
				if (jobDelay > JOB_DELAY_MIN) {
					jobDelay /= 2;
				}
				issues = removeDuplicates(issues);
				handleIssues(issues, target);
				lastTime = monitor.getLastEventTime(issues.get(0), target);
			} else {
				counter++;
			}
		}
	}

	private List<IZendIssue> removeDuplicates(List<IZendIssue> issues) {
		List<IZendIssue> result = new ArrayList<IZendIssue>();
		for (IZendIssue issue : issues) {
			if (!result.contains(issue)) {
				result.add(issue);
			}
		}
		return result;
	}

	private void doRunOld(IZendTarget target) {
		List<IZendIssue> issues = null;
		if (monitor == null) {
			monitor = new ZendMonitor(targetId);
			issues = monitor.getOpenIssues();
			if (issues != null && issues.size() > 0) {
				offset += issues.size();
				lastTime = monitor.getLastEventTime(
						issues.get(issues.size() - 1), target);
			}
		} else {
			issues = monitor.getIssues(Filter.ALL_OPEN_EVENTS, offset);
			if (issues != null && issues.size() > 0) {
				counter = 0;
				if (jobDelay > JOB_DELAY_MIN) {
					jobDelay /= 2;
				}
				issues = removeDuplicates(issues);
				handleIssues(issues, target);
				offset += issues.size();
				lastTime = monitor.getLastEventTime(
						issues.get(issues.size() - 1), target);
			} else {
				counter++;
			}
		}
	}

	private boolean doStart(IProgressMonitor monitor) {
		String name = getServerName();
		monitor.beginTask(MessageFormat.format(
				Messages.AbstractMonitor_EnablingJobName, name),
				IProgressMonitor.UNKNOWN);
		if (codeTracing == null) {
			codeTracing = new ZendCodeTracing(targetId);
			try {
				CodeTracingStatus status = codeTracing.enable(true);
				if (status == null) {
					String m = MessageFormat
							.format(Messages.AbstractMonitor_InitializationJobConnectionError,
									name);
					handleError(monitor, m);
					return false;
				}
			} catch (WebApiException e) {
				Activator.log(e);
				if (e instanceof WebApiCommunicationError) {
					String m = MessageFormat
							.format(Messages.AbstractMonitor_InitializationJobConnectionError,
									name);
					handleError(monitor, m);
					return false;
				} else {
					if (e instanceof UnexpectedResponseCode) {
						UnexpectedResponseCode codeException = (UnexpectedResponseCode) e;
						ResponseCode code = codeException.getResponseCode();
						switch (code) {
						case UNSUPPORTED_API_VERSION:
							String m = MessageFormat
									.format(Messages.AbstractMonitor_InitializationJobUnsupportedVersion,
											name);
							handleError(monitor, m);
							return false;
						default:
							break;
						}
					}
					codeTracingEnabled = false;
				}
			}
		}
		lastTime = Long.MAX_VALUE;
		if (getState() == Job.NONE) {
			setSystem(true);
		}
		AbstractMonitor.this.run(monitor);
		return true;
	}

	private void handleError(IProgressMonitor monitor, String m) {
		getProvider().showErrorMessage(getName(), m);
		stop();
	}
}
