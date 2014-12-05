/*******************************************************************************
 * Copyright (c) 2012 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.php.zendserver.monitor.internal.ui;

import java.io.File;
import java.net.URL;
import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.PlatformUI;
import org.zend.core.notifications.ui.ActionType;
import org.zend.core.notifications.ui.IActionListener;
import org.zend.core.notifications.ui.IBody;
import org.zend.core.notifications.ui.NotificationSettings;
import org.zend.core.notifications.ui.dialogs.ReadMoreDialog;
import org.zend.core.notifications.util.Fonts;
import org.zend.php.zendserver.monitor.core.EventType;
import org.zend.php.zendserver.monitor.core.IEventDetails;
import org.zend.php.zendserver.monitor.core.MonitorManager;
import org.zend.php.zendserver.monitor.ui.ICodeTraceEditorProvider;
import org.zend.sdklib.application.ZendCodeTracing;
import org.zend.sdklib.monitor.IZendIssue;
import org.zend.webapi.core.WebApiException;
import org.zend.webapi.core.connection.data.EventsGroupDetails;
import org.zend.webapi.internal.core.connection.exception.InvalidResponseException;

/**
 * Implementation of {@link IBody} for Zend Server event notification.
 * 
 * @author Wojciech Galanciak, 2012
 * 
 */
public class EventBody implements IBody {

	private static final String PROVIDER_EXTENSION = "org.zend.php.zendserver.monitor.ui.codeTracingEditor"; //$NON-NLS-1$

	private IZendIssue zendIssue;
	private String targetId;
	private IEventDetails eventDetails;
	private int actionAvailable;

	private IActionListener listener;

	private static ICodeTraceEditorProvider editorProvider;

	public EventBody(String targetId, IEventDetails eventSource,
			IZendIssue zendIssue, int actionsAvailable) {
		this.zendIssue = zendIssue;
		this.targetId = targetId;
		this.eventDetails = eventSource;
		this.actionAvailable = actionsAvailable;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.zend.core.notifications.ui.IBody#createContent(org.eclipse.swt.widgets
	 * .Composite, org.zend.core.notifications.ui.NotificationSettings)
	 */
	public Composite createContent(Composite container,
			NotificationSettings settings) {
		Composite composite = createEntryComposite(container);
		createDescription(composite, settings);
		boolean isRepeat = (actionAvailable & MonitorManager.REPEAT) == MonitorManager.REPEAT;
		boolean isCodetrace = (actionAvailable & MonitorManager.CODE_TRACE) == MonitorManager.CODE_TRACE;
		boolean isSource = eventDetails.isAvailable();
		int repeatAlign = SWT.LEFT;
		int codetraceAlign = SWT.CENTER;
		if (!isCodetrace) {
			new Label(composite, SWT.NONE);
			repeatAlign = SWT.CENTER;
		}
		if (!isSource) {
			new Label(composite, SWT.NONE);
			repeatAlign = repeatAlign == SWT.CENTER ? SWT.RIGHT : SWT.CENTER;
			codetraceAlign = SWT.RIGHT;
		}
		if (isRepeat) {
			createRepeatLink(composite, repeatAlign);
		} else {
			new Label(composite, SWT.NONE);
		}
		if (isCodetrace) {
			createTraceLink(composite, codetraceAlign);
		}
		if (isSource) {
			createSourceLink(composite);
		}
		return composite;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.zend.core.notifications.ui.IBody#addActionListener(org.zend.core.
	 * notifications.ui.IActionListener)
	 */
	public void addActionListener(IActionListener listener) {
		this.listener = listener;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.zend.core.notifications.ui.IBody#getMenuItems(org.eclipse.swt.widgets
	 * .Menu)
	 */
	public void addMenuItems(Menu menu) {
	}

	private void createTraceLink(Composite composite, int align) {
		if (getProvider() != null) {
			Link traceLink = createLink(composite,
					getLinkText(Messages.EventBody_CodetraceLink), align);
			traceLink.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent event) {
					Job showCodeTraceJob = new Job(
							Messages.EventBody_CodetraceJobTitle) {

						@Override
						protected IStatus run(IProgressMonitor monitor) {
							monitor.beginTask(
									Messages.EventBody_CodetraceJobTitle,
									IProgressMonitor.UNKNOWN);
							List<EventsGroupDetails> groups;
							try {
								groups = zendIssue.getGroupDetails();
							} catch (InvalidResponseException e) {
								showMessage(
										Messages.EventBody_CodeTraceTitle,
										Messages.EventBody_CodeTraceNotSupportedMessage);
								return Status.OK_STATUS;
							} catch (WebApiException e) {
								showMessage(
										Messages.EventBody_CodeTraceTitle,
										Messages.EventBody_CodeTraceFailedMessage);
								return Status.OK_STATUS;
							}
							if (groups != null && groups.size() == 1) {
								EventsGroupDetails group = groups.get(0);
								String traceId = group.getCodeTracing();
								if (traceId == null || traceId.isEmpty()) {
									traceId = group.getEvent().getCodeTracing();
								}
								if (traceId != null) {
									ZendCodeTracing tracing = new ZendCodeTracing(
											targetId);
									File codeTrace = tracing.get(traceId);
									if (codeTrace != null) {
										getProvider().openInEditor(
												codeTrace.getAbsolutePath());
										return Status.OK_STATUS;
									}
								}
								showMessage(
										Messages.EventBody_CodeTraceTitle,
										Messages.EventBody_CodetraceJobErrorMessage);
							}
							return Status.OK_STATUS;
						}
					};
					showCodeTraceJob.setUser(true);
					showCodeTraceJob.schedule();
				}
			});
		}
	}

	private void createSourceLink(Composite composite) {
		Link sourceLink = createLink(composite,
				getLinkText(Messages.EventBody_SourceLink), SWT.RIGHT);
		sourceLink.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent selectionEvent) {
				OpenInEditorJob job = new OpenInEditorJob(eventDetails);
				job.setUser(true);
				job.schedule();
			}
		});
	}

	private void createRepeatLink(Composite composite, int align) {
		Link repeatLink = createLink(composite,
				getLinkText(Messages.EventBody_2), align);
		repeatLink.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent selectionEvent) {
				Job job = new RequestGeneratorJob(zendIssue);
				job.setUser(true);
				job.schedule();
				if (listener != null) {
					listener.performAction(ActionType.HIDE);
				}
			}
		});
	}

	private String getLinkText(String text) {
		return "<a>" + text + "</a>"; //$NON-NLS-1$ //$NON-NLS-2$
	}

	private Link createLink(Composite parent, String text, int align) {
		Link link = new Link(parent, SWT.NO_FOCUS | align);
		link.setText(text);
		GridData gd = new GridData(align, SWT.FILL, true, true);
		link.setLayoutData(gd);
		return link;
	}

	private void createDescription(Composite composite,
			NotificationSettings settings) {
		Link label = new Link(composite, SWT.WRAP);
		label.setFont(Fonts.DEFAULT.getFont());
		label.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, true, true, 3, 1));
		label.setForeground(Display.getDefault()
				.getSystemColor(SWT.COLOR_BLACK));
		String text = zendIssue.getIssue().getGeneralDetails().getErrorString();
		if (text == null || text.isEmpty()) {
			text = zendIssue.getIssue().getRule();
		}
		initializeDescription(settings, label, text);
	}

	private void initializeDescription(NotificationSettings settings,
			Link label, String text) {
		final EventType type = eventDetails.getType();
		if (text != null) {
			text = StringEscapeUtils.unescapeHtml(text);
			label.setText(text);
		}
		int width = Math.max(settings.getWidth(),
				NotificationSettings.DEFAULT_WIDTH);
		Point size = label.computeSize(width, SWT.DEFAULT);
		int height = Fonts.DEFAULT.getFont().getFontData()[0].getHeight();
		if (text != null && size.y > 5 * height) {
			int cut = (int) (text.length() * ((double) (5 * height) / (double) size.y));
			String shortText = text.substring(0, cut);
			int index = shortText.lastIndexOf('.');
			if (index == -1) {
				index = shortText.lastIndexOf(' ');
			}
			shortText = shortText.substring(0, index + 1);
			shortText += " ... " + getLinkText(Messages.EventBody_ReadMore); //$NON-NLS-1$
			label.setText(shortText);
			final String finalText = text;
			label.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					new ReadMoreDialog(org.zend.core.notifications.Activator
							.getDefault().getParent(), type.getRule(),
							finalText, type.getLink()).open();
				}
			});
		} else {
			if (type != null && type != EventType.UNKNOWN) {
				if (text != null && !text.isEmpty()) {
					if (!text.endsWith("\\.")) { //$NON-NLS-1$
						text += '.';
					}
					label.setText(text + ' '
							+ getLinkText(Messages.EventBody_ReadMore));
				} else {
					label.setText(getLinkText(Messages.EventBody_ReadMore));
				}
				label.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent event) {
						try {
							PlatformUI.getWorkbench().getBrowserSupport()
									.getExternalBrowser()
									.openURL(new URL(type.getLink()));
						} catch (Exception e) {
							Activator.log(e);
						}
					}
				});
			}
		}
	}

	private Composite createEntryComposite(Composite container) {
		Composite composite = new Composite(container, SWT.NONE);
		GridLayout layout = new GridLayout(3, false);
		layout.horizontalSpacing = layout.verticalSpacing = 2;
		composite.setLayout(layout);
		return composite;
	}

	private static ICodeTraceEditorProvider getProvider() {
		if (editorProvider == null) {
			IConfigurationElement[] elements = Platform.getExtensionRegistry()
					.getConfigurationElementsFor(PROVIDER_EXTENSION);
			for (IConfigurationElement element : elements) {
				if ("codeTracingEditor".equals(element.getName())) { //$NON-NLS-1$
					try {
						Object listener = element
								.createExecutableExtension("class"); //$NON-NLS-1$
						if (listener instanceof ICodeTraceEditorProvider) {
							editorProvider = (ICodeTraceEditorProvider) listener;
							break;
						}
					} catch (CoreException e) {
						Activator.log(e);
					}
				}
			}
		}
		return editorProvider;
	}

	private void showMessage(final String title, final String message) {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				MessageDialog.openInformation(
						org.zend.core.notifications.Activator.getDefault()
								.getParent(), title, message);
			}
		});
	}

}
