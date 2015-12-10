/*******************************************************************************
 * Copyright (c) 2014 Zend Technologies.
 * All rights reserved. This program and the accompanying materials
 * are the copyright of Zend Technologies and is protected under
 * copyright laws of the United States.
 * You must not copy, adapt or redistribute this document for 
 * any use.
 *******************************************************************************/
package org.zend.php.zendserver.deployment.ui.servers;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.zend.php.zendserver.deployment.ui.Activator;
import org.zend.php.zendserver.deployment.ui.wizards.OpenShiftInitializationWizard;
import org.zend.sdklib.internal.target.OpenShiftTarget;
import org.zend.sdklib.internal.target.ZendTarget;
import org.zend.sdklib.manager.TargetsManager;
import org.zend.sdklib.target.IZendTarget;
import org.zend.sdklib.target.LicenseExpiredException;
import org.zend.webapi.core.WebApiException;
import org.zend.webapi.core.connection.data.values.ServerType;
import org.zend.webapi.core.connection.data.values.WebApiVersion;
import org.zend.webapi.core.connection.response.ResponseCode;
import org.zend.webapi.internal.core.connection.exception.UnexpectedResponseCode;
import org.zend.webapi.internal.core.connection.exception.WebApiCommunicationError;

/**
 * Class responsible for testing connection with specified target(s). It can
 * handle different type of servers, including OpenShift and
 * local/remote Zend Server.
 * 
 * 
 * @author Wojciech Galanciak, 2014
 * 
 */
public class TargetConnectionTester {

	private class OpenShiftInitializer {

		private IZendTarget target;
		private IProgressMonitor monitor;
		private IStatus status;

		public OpenShiftInitializer(IZendTarget target, IProgressMonitor monitor) {
			super();
			this.target = target;
		}

		public IZendTarget getTarget() {
			return target;
		}

		public IStatus getStatus() {
			return status;
		}

		public void init() {
			Display.getDefault().syncExec(new Runnable() {

				public void run() {
					Shell shell = PlatformUI.getWorkbench()
							.getActiveWorkbenchWindow().getShell();
					WizardDialog dialog = new WizardDialog(shell,
							new OpenShiftInitializationWizard(target));
					dialog.open();
				}
			});
			try {
				target = testConnectAndDetectPort(target, monitor);
			} catch (WebApiException e) {
				status = getError(e.getMessage(), e);
			} catch (RuntimeException e) {
				status = getError(e.getMessage(), e);
			} catch (LicenseExpiredException e) {
				status = getError(e.getMessage(), e);
			}
		}

	}

	private static final int[] possiblePorts = new int[] { 10081, 10082, 10088 };

	private IZendTarget[] finalTargets;

	public List<IZendTarget> getFinalTargets() {
		return Arrays.asList(finalTargets);
	}

	/**
	 * Test connection with specified target. As a status it will return the
	 * result of connection testing. In the case of {@link IStatus#OK}
	 * connection was established successfully.
	 * 
	 * @param target
	 *            target to be tested
	 * @param monitor
	 *            progress monitor
	 * @return {@link IStatus#OK} if connection was established successfully;
	 *         otherwise return {@link IStatus#ERROR}
	 */
	public IStatus testConnection(IZendTarget target, IProgressMonitor monitor) {
		return testConnection(new IZendTarget[] { target }, monitor)[0];
	}

	/**
	 * Test connection with specified one or more targets. As a status it will
	 * return the result of connection testing. In the case of
	 * {@link IStatus#OK} connection was established successfully. If connection
	 * was established successfully not with all specified targets then return
	 * {@link IStatus#WARNING} with appropriate message.
	 * 
	 * @param targets
	 *            targets to be tested
	 * @param monitor
	 *            progress monitor
	 * @return array of statuses for each target separately: {@link IStatus#OK}
	 *         if connection was established successfully; otherwise return
	 *         {@link IStatus#ERROR}
	 */
	public IStatus[] testConnection(IZendTarget[] targets,
			IProgressMonitor monitor) {
		finalTargets = new IZendTarget[targets.length];
		IStatus[] results = new IStatus[targets.length];
		for (int i = 0; i < targets.length; i++) {
			IZendTarget target = targets[i];
			if (target == null) {
				results[i] = getError(Messages.WebApiTester_NullTarget);
				continue;
			}

			String message = ((ZendTarget) target).validateTarget();
			if (message != null) {
				results[i] = getError(message);
				continue;
			}

			// if (target.isTemporary()) {
			if (TargetsManager.isOpenShift(target)
					&& Boolean.valueOf(target
							.getProperty(OpenShiftTarget.BOOTSTRAP))) {
				OpenShiftInitializer initializer = new OpenShiftInitializer(
						target, monitor);
				initializer.init();
				if (results[i] == null) {
					target = initializer.getTarget();
				} else {
					results[i] = initializer.getStatus();
					continue;
				}
			}
			try {
				target = testConnectAndDetectPort(target, monitor);
			} catch (UnexpectedResponseCode e) {
				if (TargetsManager.isOpenShift(target)) {
					if (e.getResponseCode() == ResponseCode.SERVER_NOT_CONFIGURED) {
						OpenShiftInitializer initializer = new OpenShiftInitializer(
								target, monitor);
						initializer.init();
						if (results[i] == null) {
							target = initializer.getTarget();
						} else {
							results[i] = initializer.getStatus();
							continue;
						}
					}
				}
				if (target == null) {
					results[i] = getError(MessageFormat.format(
							Messages.WebApiTester_UnexpectedError,
							e.getMessage()), e);
					continue;
				} else {
					results[i] = getError(e.getMessage());
					continue;
				}
			} catch (WebApiException e) {
				results[i] = getError(MessageFormat.format(
						Messages.WebApiTester_UnexpectedError,
						e.getMessage()), e);
				continue;
			} catch (RuntimeException e) {
				results[i] = getError(MessageFormat.format(
						Messages.WebApiTester_UnexpectedError,
						e.getMessage()), e);
				continue;
			} catch (LicenseExpiredException e) {
				results[i] = getError(MessageFormat.format(
						Messages.WebApiTester_UnexpectedError,
						e.getMessage()), e);
				continue;
			}
			// }
			if (target != null) {
				results[i] = Status.OK_STATUS;
				finalTargets[i] = copy((ZendTarget) target);
			}
		}
		return results;
	}

	private IZendTarget testConnectAndDetectPort(IZendTarget target,
			IProgressMonitor monitor) throws WebApiException,
			LicenseExpiredException {
		WebApiException catchedException = null;
		int[] portToTest = possiblePorts;
		if (target.getHost().getPort() == -1) {
			for (int port : portToTest) {
				URL old = target.getHost();
				URL host;
				try {
					host = new URL(old.getProtocol(), old.getHost(), port,
							old.getFile());
					((ZendTarget) target).setHost(host);
				} catch (MalformedURLException e) {
					// should never happen, just replacing a port
				}
				monitor.subTask(MessageFormat.format(
						Messages.WebApiTester_TestingPortSubTask, target
								.getHost().getHost(), String.valueOf(port)));
				try {
					return testTargetConnection(target);
				} catch (WebApiException e) {
					catchedException = e;
				}
			}
		} else {
			try {
				return testTargetConnection(target);
			} catch (WebApiException e) {
				catchedException = e;
			}
		}
		if (catchedException != null) {
			throw catchedException;
		}
		return null;
	}

	private IZendTarget testTargetConnection(IZendTarget target)
			throws WebApiException, LicenseExpiredException {
		try {
			if (target.connect(WebApiVersion.V1_3, ServerType.ZEND_SERVER)) {
				return target;
			}
		} catch (WebApiCommunicationError e) {
			throw e;
		} catch (UnexpectedResponseCode e) {
			ResponseCode code = e.getResponseCode();
			switch (code) {
			case INTERNAL_SERVER_ERROR:
			case AUTH_ERROR:
			case INSUFFICIENT_ACCESS_LEVEL:
				throw e;
			default:
				break;
			}
		}
		try {
			if (target.connect(WebApiVersion.UNKNOWN, ServerType.ZEND_SERVER)) {
				return target;
			}
		} catch (WebApiException ex) {
			if (target.connect()) {
				return target;
			}
		}
		return null;
	}

	private IZendTarget copy(ZendTarget t) {
		ZendTarget target = new ZendTarget(t.getId(), t.getHost(),
				t.getDefaultServerURL(), t.getKey(), t.getSecretKey());
		String[] keys = t.getPropertiesKeys();
		for (String key : keys) {
			target.addProperty(key, t.getProperty(key));
		}
		return target;
	}

	private IStatus getError(String message) {
		return new Status(IStatus.ERROR, Activator.PLUGIN_ID, message);
	}

	private IStatus getError(String message, Exception e) {
		return new Status(IStatus.ERROR, Activator.PLUGIN_ID, message, e);
	}

	private IStatus getWarning(String message) {
		return new Status(IStatus.WARNING, Activator.PLUGIN_ID, message);
	}

}
