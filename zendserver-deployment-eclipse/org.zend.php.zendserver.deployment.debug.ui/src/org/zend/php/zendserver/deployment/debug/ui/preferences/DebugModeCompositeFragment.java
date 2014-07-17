/*******************************************************************************
 * Copyright (c) 2014 Zend Technologies.
 * All rights reserved. This program and the accompanying materials
 * are the copyright of Zend Technologies and is protected under
 * copyright laws of the United States.
 * You must not copy, adapt or redistribute this document for 
 * any use.
 *******************************************************************************/
package org.zend.php.zendserver.deployment.debug.ui.preferences;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.php.internal.server.core.Server;
import org.eclipse.php.internal.ui.wizards.IControlHandler;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.osgi.service.prefs.BackingStoreException;
import org.zend.core.notifications.NotificationManager;
import org.zend.php.server.ui.fragments.AbstractCompositeFragment;
import org.zend.php.zendserver.deployment.core.targets.TargetsManagerService;
import org.zend.php.zendserver.deployment.debug.core.DebugModeManager;
import org.zend.php.zendserver.deployment.debug.ui.Messages;
import org.zend.php.zendserver.deployment.ui.Activator;
import org.zend.sdklib.manager.TargetsManager;
import org.zend.sdklib.target.IZendTarget;

/**
 * @author Wojciech Galanciak, 2014
 * 
 */
@SuppressWarnings("restriction")
public class DebugModeCompositeFragment extends AbstractCompositeFragment {

	private class URLInputValidator implements IInputValidator {

		public String isValid(String newText) {
			try {
				URL url = new URL(newText);
				int port = target.getHost().getPort();
				if (port != -1 && port == url.getPort()) {
					return MessageFormat.format(
							Messages.DebugModeCompositeFragment_InvalidPort,
							String.valueOf(port));
				}
			} catch (MalformedURLException e) {
				return Messages.DebugModeCompositeFragment_InvalidUrl;
			}
			return null;
		}
	}

	private class RestartJob extends Job {

		public RestartJob() {
			super(Messages.DebugModeCompositeFragment_RestartJobTitle);
		}

		protected IStatus run(IProgressMonitor monitor) {
			monitor.beginTask(
					Messages.DebugModeCompositeFragment_RestartJobDesc,
					IProgressMonitor.UNKNOWN);
			IStatus status = DebugModeManager.getManager().restartDebugMode(
					target);
			switch (status.getSeverity()) {
			case IStatus.OK:
				NotificationManager.registerInfo(
						Messages.DebugModeCompositeFragment_Name,
						status.getMessage(), 4000);
				break;
			case IStatus.WARNING:
				NotificationManager.registerWarning(
						Messages.DebugModeCompositeFragment_Name,
						status.getMessage(), 4000);
				break;
			case IStatus.ERROR:
				NotificationManager.registerError(
						Messages.DebugModeCompositeFragment_Name,
						status.getMessage(), 4000);
				break;
			default:
				break;
			}
			return Status.OK_STATUS;
		}
	}

	public static String ID = "org.zend.php.zendserver.deployment.debug.ui.preferences.DebugModeCompositeFragment"; //$NON-NLS-1$

	private static final String EMPTY_STRING = ""; //$NON-NLS-1$

	private TableViewer viewer;
	private Button removeButton;
	private Button modifyButton;
	private Button addButton;

	private IEclipsePreferences prefs;
	private IEclipsePreferences defaultPrefs;

	private List<String> input;

	private IZendTarget target;

	private Button breakFirstButton;

	/**
	 * PlatformCompositeFragment constructor
	 * 
	 * @param parent
	 * @param handler
	 * @param isForEditing
	 */
	public DebugModeCompositeFragment(Composite parent,
			IControlHandler handler, boolean isForEditing) {
		super(parent, handler, isForEditing,
				Messages.DebugModeCompositeFragment_Name,
				getTitle(isForEditing),
				Messages.DebugModeCompositeFragment_PageDesc);
		this.prefs = InstanceScope.INSTANCE
				.getNode(DebugModeManager.DEBUG_MODE_NODE);
		this.defaultPrefs = DefaultScope.INSTANCE
				.getNode(DebugModeManager.DEBUG_MODE_NODE);
		createControl(isForEditing);
	}

	@Override
	public boolean performOk() {
		boolean dirty = false;
		IZendTarget target = getTarget();
		if (target != null) {
			String id = target.getId();
			String oldValue = prefs
					.get(id, defaultPrefs.get(id, (String) null));
			String newValue = getValue(input);
			if (newValue == null && oldValue != null) {
				prefs.remove(id);
				dirty = true;
			}
			if (newValue != null
					&& (oldValue == null || !oldValue.equals(newValue))) {
				prefs.put(id, newValue);
				dirty = true;
			}
			if (dirty) {
				try {
					prefs.flush();
				} catch (BackingStoreException e) {
					Activator.log(e);
				}
				if (askForRestart(target)) {
					Job restartJob = new RestartJob();
					restartJob.setUser(true);
					restartJob.schedule();
				}
			}
		}
		Server server = getServer();
		server.setAttribute(DebugModeManager.BREAK_FIRST_LINE,
				String.valueOf(breakFirstButton.getSelection()));
		return true;
	}

	@Override
	public String getId() {
		return ID;
	}

	@Override
	public void validate() {
		setMessage(getDescription(), IMessageProvider.NONE);
	}

	@Override
	public boolean isComplete() {
		return true;
	}

	@Override
	protected void createControl(Composite parent) {
		Composite filtersSection = new Composite(parent, SWT.NONE);
		filtersSection.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,
				true, 3, 1));
		GridLayout gl = new GridLayout(2, false);
		gl.marginWidth = 0;
		gl.marginHeight = 0;
		filtersSection.setLayout(gl);
		Label filtersLabel = new Label(filtersSection, SWT.WRAP);
		GridData gd = new GridData(SWT.FILL, SWT.TOP, true, false);
		gd.widthHint = 400;
		gd.horizontalSpan = 2;
		filtersLabel.setLayoutData(gd);
		filtersLabel
				.setText(Messages.DebugModeCompositeFragment_FilterSectionLabel);
		viewer = new TableViewer(filtersSection, SWT.SINGLE | SWT.BORDER);
		viewer.getTable().setLayoutData(
				new GridData(SWT.FILL, SWT.FILL, true, true));
		viewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				ISelection selection = viewer.getSelection();
				if (selection != null && !selection.isEmpty()) {
					removeButton.setEnabled(true);
					modifyButton.setEnabled(true);
				} else {
					removeButton.setEnabled(false);
					modifyButton.setEnabled(false);
				}
			}
		});
		viewer.setContentProvider(new FiltersContentProvider());
		viewer.setLabelProvider(new LabelProvider() {
			public String getText(Object element) {
				if (element instanceof String) {
					return (String) element;
				}
				return super.getText(element);
			}
		});

		Composite buttonsSection = new Composite(filtersSection, SWT.NONE);
		gd = new GridData(SWT.FILL, SWT.FILL, false, true);
		gd.widthHint = 90;
		buttonsSection.setLayoutData(gd);
		GridLayout layout = new GridLayout(1, true);
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		buttonsSection.setLayout(layout);
		addButton = new Button(buttonsSection, SWT.PUSH);
		addButton.setText(Messages.DebugModeCompositeFragment_AddLabel);
		addButton.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		addButton.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent e) {
				InputDialog dlg = new InputDialog(getShell(), EMPTY_STRING,
						Messages.DebugModeCompositeFragment_EnterFilter,
						EMPTY_STRING, new URLInputValidator());
				if (dlg.open() == Window.OK) {
					input.add(dlg.getValue());
					viewer.refresh();
				}
			}
		});
		modifyButton = new Button(buttonsSection, SWT.PUSH);
		modifyButton.setText(Messages.DebugModeCompositeFragment_EditLabel);
		modifyButton
				.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		modifyButton.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent e) {
				IStructuredSelection sel = (IStructuredSelection) viewer
						.getSelection();
				Object[] selected = sel.toArray();
				if (selected.length > 0) {
					InputDialog dlg = new InputDialog(getShell(), EMPTY_STRING,
							Messages.DebugModeCompositeFragment_ModifyFilter,
							(String) selected[0], new URLInputValidator());
					if (dlg.open() == Window.OK) {
						int index = input.indexOf(selected[0]);
						input.remove(selected[0]);
						input.add(index, dlg.getValue());
						viewer.refresh();
					}
				}
			}
		});
		removeButton = new Button(buttonsSection, SWT.PUSH);
		removeButton.setText(Messages.DebugModeCompositeFragment_RemoveLabel);
		removeButton
				.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		removeButton.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent e) {
				removeElement(viewer.getSelection());
			}
		});
		removeButton.setEnabled(false);
		modifyButton.setEnabled(false);
		breakFirstButton = new Button(parent, SWT.CHECK);
		breakFirstButton.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,
				false, 3, 1));
		breakFirstButton
				.setText(Messages.DebugModeCompositeFragment_BreakFirstLabel);
	}

	@Override
	protected void init() {
		target = getTarget();
		input = new ArrayList<String>();
		if (target != null) {
			String id = target.getId();
			String value = prefs.get(id, defaultPrefs.get(id, (String) null));
			if (value != null && !value.trim().isEmpty()) {
				String[] segments = value.split(","); //$NON-NLS-1$
				input = new ArrayList<String>(Arrays.asList(segments));
			}
		}
		Server server = getServer();
		if (server != null) {
			String breakAtFirst = server.getAttribute(
					DebugModeManager.BREAK_FIRST_LINE, String.valueOf(true));
			breakFirstButton.setSelection(Boolean.valueOf(breakAtFirst));
		}
		viewer.setInput(input);
		viewer.refresh();
		validate();
	}

	private void removeElement(ISelection selection) {
		IStructuredSelection sel = (IStructuredSelection) selection;
		Object[] toRemove = sel.toArray();
		for (Object elem : toRemove) {
			if (elem == null) {
				return;
			}
			input.remove((String) elem);
		}
		viewer.refresh();
	}

	private String getValue(List<String> list) {
		StringBuilder builder = new StringBuilder();
		if (list == null || list.isEmpty()) {
			return null;
		}
		for (String val : list) {
			builder.append(val).append(DebugModeManager.FILTER_SEPARATOR);
		}
		return builder.substring(0, builder.length() - 1);
	}

	private boolean askForRestart(IZendTarget target) {
		if (DebugModeManager.getManager().isInDebugMode(target)) {
			return MessageDialog
					.openQuestion(
							getShell(),
							Messages.DebugModeCompositeFragment_Name,
							MessageFormat
									.format(Messages.DebugModeCompositeFragment_RestartQuestion,
											target.getHost().getHost()));
		}
		return false;
	}

	private IZendTarget getTarget() {
		TargetsManager manager = TargetsManagerService.INSTANCE
				.getTargetManager();
		Server server = getServer();
		if (server != null) {
			String serverName = server.getName();
			IZendTarget[] targets = manager.getTargets();
			for (IZendTarget target : targets) {
				if (serverName.equals(target.getServerName())) {
					return target;
				}
			}
		}
		return null;
	}

	private static String getTitle(boolean isEditing) {
		return isEditing ? Messages.DebugModeCompositeFragment_EditTitle
				: Messages.DebugModeCompositeFragment_CreateTitle;
	}

}
