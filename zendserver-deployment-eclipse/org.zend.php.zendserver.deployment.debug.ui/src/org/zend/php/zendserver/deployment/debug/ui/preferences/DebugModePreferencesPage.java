/*******************************************************************************
 * Copyright (c) 2012 Zend Technologies Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Zend Technologies Ltd. - initial API and implementation
 *******************************************************************************/
package org.zend.php.zendserver.deployment.debug.ui.preferences;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.osgi.service.prefs.BackingStoreException;
import org.zend.core.notifications.NotificationManager;
import org.zend.php.zendserver.deployment.core.targets.TargetsManagerService;
import org.zend.php.zendserver.deployment.debug.core.DebugModeManager;
import org.zend.php.zendserver.deployment.debug.ui.Messages;
import org.zend.php.zendserver.deployment.ui.targets.TargetsCombo;
import org.zend.php.zendserver.deployment.ui.targets.TargetsCombo.Type;
import org.zend.sdklib.target.IZendTarget;

/**
 * Debug Mode preferences.
 * 
 * @author Wojciech Galanciak, 2012
 * 
 */
public class DebugModePreferencesPage extends PreferencePage implements
		IWorkbenchPreferencePage {

	private class URLInputValidator implements IInputValidator {

		public String isValid(String newText) {
			try {
				URL url = new URL(newText);
				IZendTarget target = targetsCombo.getSelected();
				int port = target.getHost().getPort();
				if (port != -1 && port == url.getPort()) {
					return MessageFormat
							.format(Messages.DebugModePreferencesPage_URLValidationPortError,
									String.valueOf(port));
				}
			} catch (MalformedURLException e) {
				return Messages.DebugModePreferencesPage_URLValidationError;
			}
			return null;
		}

	}

	public static final String ID = "org.zend.php.zendserver.deployment.ui.DebugModePreferencePage"; //$NON-NLS-1$

	private static final String EMPTY_STRING = ""; //$NON-NLS-1$

	private TargetsCombo targetsCombo;
	private TableViewer viewer;
	private Button removeButton;
	private Button modifyButton;
	private Button addButton;

	private IEclipsePreferences prefs;
	private IEclipsePreferences defaultPrefs;

	private Map<String, List<String>> input;

	public DebugModePreferencesPage() {
		this.prefs = InstanceScope.INSTANCE
				.getNode(DebugModeManager.DEBUG_MODE_NODE);
		this.defaultPrefs = DefaultScope.INSTANCE
				.getNode(DebugModeManager.DEBUG_MODE_NODE);
		this.input = new HashMap<String, List<String>>();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.preference.PreferencePage#applyData(java.lang.Object)
	 */
	public void applyData(Object data) {
		if (data instanceof IZendTarget) {
			IZendTarget target = (IZendTarget) data;
			if (targetsCombo != null) {
				targetsCombo.select(target.getId());
				viewer.setInput(input.get(target.getId()));
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.preference.PreferencePage#performOk()
	 */
	public boolean performOk() {
		boolean dirty = false;
		IZendTarget[] targets = TargetsManagerService.INSTANCE
				.getTargetManager().getTargets();
		final List<IZendTarget> toRestart = new ArrayList<IZendTarget>();
		for (IZendTarget target : targets) {
			boolean dirtyTarget = false;
			String id = target.getId();
			String oldValue = prefs
					.get(id, defaultPrefs.get(id, (String) null));
			String newValue = getValue(input.get(id));
			if (newValue == null && oldValue != null) {
				prefs.remove(id);
				dirty = true;
				dirtyTarget = true;
			}
			if (newValue != null
					&& (oldValue == null || !oldValue.equals(newValue))) {
				prefs.put(id, newValue);
				dirty = true;
				dirtyTarget = true;
			}
			if (dirtyTarget && askForRestart(target)) {
				toRestart.add(target);
			}
		}
		if (dirty) {
			try {
				prefs.flush();
			} catch (BackingStoreException e) {
				org.zend.php.zendserver.deployment.debug.ui.Activator.log(e);
			}
		}
		if (!toRestart.isEmpty()) {
			Job restartJob = new Job(Messages.DebugModePreferencesPage_JobTitle) {

				protected IStatus run(IProgressMonitor monitor) {
					monitor.beginTask(
							Messages.DebugModePreferencesPage_JobDescription,
							IProgressMonitor.UNKNOWN);
					for (IZendTarget target : toRestart) {
						IStatus status = DebugModeManager.getManager()
								.restartDebugMode(target);
						switch (status.getSeverity()) {
						case IStatus.OK:
							NotificationManager.registerInfo(
									Messages.DebugModeHandler_DebugModeLabel,
									status.getMessage(), 4000);
							break;
						case IStatus.WARNING:
							NotificationManager.registerWarning(
									Messages.DebugModeHandler_DebugModeLabel,
									status.getMessage(), 4000);
							break;
						case IStatus.ERROR:
							NotificationManager.registerError(
									Messages.DebugModeHandler_DebugModeLabel,
									status.getMessage(), 4000);
							break;
						default:
							break;
						}
					}
					return Status.OK_STATUS;
				}
			};
			restartJob.setUser(true);
			restartJob.schedule();
		}
		return super.performOk();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.preference.PreferencePage#performDefaults()
	 */
	protected void performDefaults() {
		IZendTarget[] targets = TargetsManagerService.INSTANCE
				.getTargetManager().getTargets();
		for (IZendTarget target : targets) {
			String id = target.getId();
			prefs.remove(id);
		}
		initializeValues();
		super.performDefaults();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.preference.PreferencePage#createContents(org.eclipse
	 * .swt.widgets.Composite)
	 */
	protected Control createContents(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		composite.setLayout(new GridLayout(2, false));

		createTargetSelection(composite);
		createFiltersSection(composite);
		initializeValues();

		return composite;
	}

	private void createFiltersSection(Composite parent) {
		Composite filtersSection = new Composite(parent, SWT.NONE);
		filtersSection.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,
				true, 2, 1));
		GridLayout gl = new GridLayout(2, false);
		gl.marginWidth = 0;
		gl.marginHeight = 0;
		filtersSection.setLayout(gl);
		Label filtersLabel = new Label(filtersSection, SWT.NONE);
		filtersLabel.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false,
				2, 1));
		filtersLabel
				.setText(Messages.DebugModePreferencesPage_FilterSectionLabel);
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
		GridData gd = new GridData(SWT.FILL, SWT.FILL, false, true);
		gd.widthHint = 90;
		buttonsSection.setLayoutData(gd);
		GridLayout layout = new GridLayout(1, true);
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		buttonsSection.setLayout(layout);
		addButton = new Button(buttonsSection, SWT.PUSH);
		addButton.setText(Messages.DebugModePreferencesPage_AddButton);
		addButton.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		addButton.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent e) {
				InputDialog dlg = new InputDialog(getShell(), EMPTY_STRING,
						Messages.DebugModePreferencesPage_AddDesc,
						EMPTY_STRING, new URLInputValidator());
				if (dlg.open() == Window.OK) {
					String id = targetsCombo.getSelected().getId();
					List<String> values = input.get(id);
					values.add(dlg.getValue());
					viewer.refresh();
				}
			}
		});
		modifyButton = new Button(buttonsSection, SWT.PUSH);
		modifyButton.setText(Messages.DebugModePreferencesPage_EdiotButton);
		modifyButton
				.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		modifyButton.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent e) {
				IStructuredSelection sel = (IStructuredSelection) viewer
						.getSelection();
				Object[] selected = sel.toArray();
				if (selected.length > 0) {
					InputDialog dlg = new InputDialog(getShell(), EMPTY_STRING,
							Messages.DebugModePreferencesPage_ModifyDesc,
							(String) selected[0], new URLInputValidator());
					if (dlg.open() == Window.OK) {
						String id = targetsCombo.getSelected().getId();
						List<String> values = input.get(id);
						int index = values.indexOf(selected[0]);
						values.remove(selected[0]);
						values.add(index, dlg.getValue());
						viewer.refresh();
					}
				}
			}
		});
		removeButton = new Button(buttonsSection, SWT.PUSH);
		removeButton.setText(Messages.DebugModePreferencesPage_RemoveButton);
		removeButton
				.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		removeButton.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent e) {
				removeElement(viewer.getSelection());
			}
		});
		if (targetsCombo.getSelected() == null) {
			addButton.setEnabled(false);
		}
		removeButton.setEnabled(false);
		modifyButton.setEnabled(false);
	}

	private void createTargetSelection(Composite container) {
		targetsCombo = new TargetsCombo(Type.ZEND_SERVER_6, false);
		targetsCombo.setLabel(Messages.DebugModePreferencesPage_TargetLabel);
		targetsCombo.createControl(container);
		targetsCombo.getCombo().addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent e) {
				String id = targetsCombo.getSelected().getId();
				viewer.setInput(input.get(id));
				addButton.setEnabled(true);
			}

		});
	}

	private void initializeValues() {
		IZendTarget[] targets = TargetsManagerService.INSTANCE
				.getTargetManager().getTargets();
		for (IZendTarget target : targets) {
			String id = target.getId();
			String value = prefs.get(id, defaultPrefs.get(id, (String) null));
			if (value != null && !value.trim().isEmpty()) {
				String[] segments = value.split(","); //$NON-NLS-1$
				input.put(id, new ArrayList<String>(Arrays.asList(segments)));
			} else {
				input.put(id, new ArrayList<String>());
			}
		}
		String id = targetsCombo.getSelected().getId();
		viewer.setInput(input.get(id));
		viewer.refresh();
	}

	private void removeElement(ISelection selection) {
		IStructuredSelection sel = (IStructuredSelection) selection;
		Object[] toRemove = sel.toArray();
		for (Object elem : toRemove) {
			if (elem == null) {
				return;
			}
			String id = targetsCombo.getSelected().getId();
			List<String> values = input.get(id);
			values.remove((String) elem);
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
			return MessageDialog.openQuestion(getShell(),
					Messages.DebugModeHandler_DebugModeLabel,
					MessageFormat.format(
							Messages.DebugModePreferencesPage_RestartMessage,
							target.getHost().getHost()));
		}
		return false;
	}

}
