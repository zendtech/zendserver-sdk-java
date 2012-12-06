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
package org.zend.php.zendserver.monitor.ui.preferences;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.zend.php.zendserver.deployment.core.targets.TargetsManagerService;
import org.zend.php.zendserver.deployment.ui.targets.TargetsCombo;
import org.zend.php.zendserver.monitor.core.Activator;
import org.zend.php.zendserver.monitor.core.MonitorManager;
import org.zend.php.zendserver.monitor.internal.ui.Messages;
import org.zend.sdklib.target.IZendTarget;
import org.zend.webapi.core.connection.data.values.IssueSeverity;

/**
 * Preference page for targets monitoring customization.
 * 
 * @author Wojciech Galanciak, 2012
 * 
 */
public class MonitoringPreferencePage extends PreferencePage implements
		IWorkbenchPreferencePage {

	private class URLInputValidator implements IInputValidator {

		public String isValid(String newText) {
			try {
				new URL(newText);
			} catch (MalformedURLException e) {
				return Messages.MonitoringPreferencePage_InvalidUrlMessage;
			}
			return null;
		}

	}

	private class TargetParams {

		private boolean[] severities;
		private boolean hide;
		private int delay;

	}

	public static final String ID = "org.zend.php.zendserver.monitor.ui.targetsMonitoring"; //$NON-NLS-1$

	private static final String EMPTY_STRING = ""; //$NON-NLS-1$

	private TargetsCombo targetsCombo;
	private TableViewer viewer;
	private Button removeButton;
	private Button modifyButton;
	private Button addButton;
	private Button[] severityButtons;
	private Button hideButton;
	private Text delayText;

	private Map<String, List<String>> input;
	private Map<String, TargetParams> params;

	public MonitoringPreferencePage() {
		setDescription(Messages.TargetsMonitoringPreferencePage_PreferencePageDescription);
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		this.input = new HashMap<String, List<String>>();
		this.params = new HashMap<String, TargetParams>();
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
			String targetId = target.getId();
			if (targetsCombo != null) {
				targetsCombo.select(target.getId());
				viewer.setInput(input.get(targetId));
			}
			initializetTargetParams(targetId);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.preference.PreferencePage#performOk()
	 */
	public boolean performOk() {
		IZendTarget[] targets = TargetsManagerService.INSTANCE
				.getTargetManager().getTargets();
		for (IZendTarget target : targets) {
			String id = target.getId();
			String oldValue = getValue(MonitorManager.getFilters(id));
			String newValue = getValue(input.get(id));
			if (oldValue == null || !oldValue.equals(newValue)) {
				getPreferenceStore().setValue(MonitorManager.getFiltersKey(id),
						newValue);
				MonitorManager.updateFilters(id);
			}
			TargetParams p = params.get(id);
			for (int i = 0; i < severityButtons.length; i++) {
				String nodeName = getNodeName(severityButtons[i]);
				getPreferenceStore().setValue(id + '.' + nodeName,
						p.severities[i]);
			}
			getPreferenceStore()
					.setValue(MonitorManager.getHideKey(id), p.hide);
			getPreferenceStore().setValue(MonitorManager.getHideTimeKey(id),
					p.delay);
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
			MonitorManager.removePreferences(target);
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
		new Label(composite, SWT.NONE);
		createFiltersSection(composite);
		createSeveritySection(composite);
		createDelaySection(composite);
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
		filtersLabel.setText(Messages.MonitoringPreferencePage_FiltersLabel);
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
		addButton.setText(Messages.MonitoringPreferencePage_AddLabel);
		addButton.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		addButton.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent e) {
				InputDialog dlg = new InputDialog(getShell(),
						Messages.MonitoringPreferencePage_AddFilterTitle,
						Messages.MonitoringPreferencePage_AddTilterDesc,
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
		modifyButton.setText(Messages.MonitoringPreferencePage_ModifyLabel);
		modifyButton
				.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		modifyButton.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent e) {
				IStructuredSelection sel = (IStructuredSelection) viewer
						.getSelection();
				Object[] selected = sel.toArray();
				if (selected.length > 0) {
					InputDialog dlg = new InputDialog(
							getShell(),
							Messages.MonitoringPreferencePage_ModifyFilterTitle,
							Messages.MonitoringPreferencePage_ModifyFilterDesc,
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
		removeButton.setText(Messages.MonitoringPreferencePage_RemoveLabel);
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
		targetsCombo = new TargetsCombo(false);
		targetsCombo.setLabel(Messages.MonitoringPreferencePage_TargetsLabel);
		targetsCombo.createControl(container);
		targetsCombo.getCombo().addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent e) {
				String id = targetsCombo.getSelected().getId();
				viewer.setInput(input.get(id));
				addButton.setEnabled(true);
				initializetTargetParams(id);
			}

		});
	}

	private void initializeValues() {
		IZendTarget[] targets = TargetsManagerService.INSTANCE
				.getTargetManager().getTargets();
		for (IZendTarget target : targets) {
			String id = target.getId();
			List<String> value = MonitorManager.getFilters(id);
			if (value != null && !value.isEmpty()) {
				input.put(id, value);
			} else {
				input.put(id, new ArrayList<String>());
			}
			TargetParams p = new TargetParams();
			String key = MonitorManager.getHideKey(id);
			if (getPreferenceStore().isDefault(key)) {
				p.hide = getPreferenceStore().getDefaultBoolean(key);
			} else {
				p.hide = getPreferenceStore().getBoolean(key);
			}
			key = MonitorManager.getHideTimeKey(id);
			if (getPreferenceStore().isDefault(key)) {
				p.delay = getPreferenceStore().getDefaultInt(key);
			} else {
				p.delay = getPreferenceStore().getInt(key);
			}
			p.severities = new boolean[3];
			for (int i = 0; i < severityButtons.length; i++) {
				String nodeName = getNodeName(severityButtons[i]);
				key = id + '.' + nodeName;
				if (getPreferenceStore().isDefault(key)) {
					p.severities[i] = getPreferenceStore().getDefaultBoolean(
							key);
				} else {
					p.severities[i] = getPreferenceStore().getBoolean(key);
				}
			}
			params.put(id, p);
		}
		String id = targetsCombo.getSelected().getId();
		viewer.setInput(input.get(id));
		viewer.refresh();
		initializetTargetParams(id);
	}

	private void initializetTargetParams(String id) {
		TargetParams p = params.get(id);
		for (int i = 0; i < severityButtons.length; i++) {
			severityButtons[i].setSelection(p.severities[i]);
		}
		hideButton.setSelection(p.hide);
		delayText.setEnabled(hideButton.getSelection());
		int delay = p.delay;
		if (delay == 0) {
			delay = 10;
		}
		delayText.setText(String.valueOf(delay));
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
		if (list != null && !list.isEmpty()) {
			for (String val : list) {
				builder.append(val).append(MonitorManager.FILTER_SEPARATOR);
			}
			return builder.substring(0, builder.length() - 1);
		}
		return ""; //$NON-NLS-1$
	}

	private void createSeveritySection(Composite composite) {
		Composite container = new Composite(composite, SWT.NONE);
		container.setLayoutData(new GridData(SWT.TOP, SWT.FILL, false, false,
				2, 1));
		container.setLayout(new GridLayout(1, true));
		Label label = new Label(container, SWT.WRAP);
		label.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		label.setText(Messages.ServerMonitoringPropertyPage_Description);
		Group group = new Group(container, SWT.NONE);
		group.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		group.setLayout(new GridLayout(1, false));
		IssueSeverity[] severityValues = IssueSeverity.values();
		severityButtons = new Button[severityValues.length];
		for (int i = 0; i < severityValues.length; i++) {
			severityButtons[i] = createCheckBox(group, severityValues[i]);
			severityButtons[i].addSelectionListener(new SelectionAdapter() {

				public void widgetSelected(SelectionEvent e) {
					String id = targetsCombo.getSelected().getId();
					TargetParams p = params.get(id);
					for (int i = 0; i < severityButtons.length; i++) {
						p.severities[i] = severityButtons[i].getSelection();
					}
				}
			});
		}
	}

	private void createDelaySection(Composite composite) {
		Composite container = new Composite(composite, SWT.NONE);
		container.setLayoutData(new GridData(SWT.TOP, SWT.FILL, false, false,
				2, 1));
		container.setLayout(new GridLayout(1, true));
		hideButton = new Button(container, SWT.CHECK);
		hideButton.setText(Messages.ServerMonitoringPropertyPage_HideLabel);
		hideButton.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent e) {
				String id = targetsCombo.getSelected().getId();
				TargetParams p = params.get(id);
				p.hide = hideButton.getSelection();
				delayText.setEnabled(p.hide);
			}
		});
		hideButton.setLayoutData(new GridData(SWT.TOP, SWT.FILL, false, false));
		Group group = new Group(container, SWT.NONE);
		group.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, true));
		group.setLayout(new GridLayout(1, false));
		Composite labelComposite = new Composite(group, SWT.NONE);
		labelComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false,
				false));
		GridLayout layout = new GridLayout(2, false);
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		labelComposite.setLayout(layout);
		Label label = new Label(labelComposite, SWT.NONE);
		label.setText(Messages.ServerMonitoringPropertyPage_DelayLabel);
		GridData gd = new GridData(SWT.LEFT, SWT.CENTER, true, true);
		gd.minimumWidth = 100;
		label.setLayoutData(gd);
		delayText = new Text(labelComposite, SWT.BORDER | SWT.SINGLE);
		delayText.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent e) {
				try {
					String val = delayText.getText();
					int delay = Integer.valueOf(val);
					String id = targetsCombo.getSelected().getId();
					TargetParams p = params.get(id);
					p.delay = delay;
				} catch (NumberFormatException ex) {
					setErrorMessage(Messages.ServerMonitoringPropertyPage_InvalidDelayMessage);
					setValid(false);
					return;
				}
				setErrorMessage(null);
				setValid(true);
			}
		});
		delayText.setText(String.valueOf(MonitorManager.DELAY_DEFAULT));
		delayText
				.setToolTipText(Messages.ServerMonitoringPropertyPage_DelayTooltip);
		GridData data = new GridData(SWT.LEFT, SWT.CENTER, true, true);
		data.minimumWidth = 100;
		delayText.setLayoutData(data);
	}

	private Button createCheckBox(Composite parent, IssueSeverity severity) {
		Button button = new Button(parent, SWT.CHECK | SWT.LEFT);
		button.setText(severity.getName());
		button.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		return button;
	}

	private String getNodeName(Button button) {
		return button.getText();
	}

}
