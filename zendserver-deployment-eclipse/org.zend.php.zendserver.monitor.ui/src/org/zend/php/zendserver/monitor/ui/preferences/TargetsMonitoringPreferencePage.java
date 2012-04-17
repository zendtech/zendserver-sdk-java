/*******************************************************************************
 * Copyright (c) 2012 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.php.zendserver.monitor.ui.preferences;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.resource.ImageDescriptor;
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
import org.zend.php.zendserver.monitor.core.Activator;
import org.zend.php.zendserver.monitor.core.MonitorManager;
import org.zend.php.zendserver.monitor.internal.ui.Messages;
import org.zend.webapi.core.connection.data.values.IssueSeverity;

/**
 * Property page for targets monitoring customization.
 * 
 * @author Wojciech Galanciak, 2012
 * 
 */
public class TargetsMonitoringPreferencePage extends PreferencePage implements
		IWorkbenchPreferencePage {

	private static final int DELAY_DEFAULT = 10;

	private Button[] severityButtons;
	private Button hideButton;
	private Text delayText;

	public TargetsMonitoringPreferencePage() {
		setDescription(Messages.TargetsMonitoringPreferencePage_PreferencePageDescription);
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
	}

	public TargetsMonitoringPreferencePage(String title, ImageDescriptor image) {
		super(title, image);
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
	 * @see org.eclipse.jface.preference.PreferencePage#performOk()
	 */
	@Override
	public boolean performOk() {
		for (Button button : severityButtons) {
			String nodeName = getNodeName(button);
			getPreferenceStore().setValue(nodeName, button.getSelection());
		}
		getPreferenceStore().setValue(MonitorManager.HIDE_KEY,
				hideButton.getSelection());
		getPreferenceStore().setValue(MonitorManager.HIDE_TIME_KEY,
				delayText.getText());
		getPreferenceStore().setValue(MonitorManager.HIDE_KEY,
				hideButton.getSelection());
		getPreferenceStore().setValue(MonitorManager.HIDE_TIME_KEY,
				delayText.getText());
		super.performDefaults();
		return super.performOk();
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
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		composite.setLayout(new GridLayout(1, true));
		createSeveritySection(composite);
		createDelaySection(composite);
		init();
		return composite;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.preference.PreferencePage#performDefaults()
	 */
	@Override
	protected void performDefaults() {
		for (Button button : severityButtons) {
			String nodeName = getNodeName(button);
			button.setSelection(getDefaultValue(nodeName));
		}
		hideButton.setSelection(getDefaultValue(MonitorManager.HIDE_KEY));
		delayText.setText(String.valueOf(DELAY_DEFAULT));
		super.performDefaults();
	}

	private void createSeveritySection(Composite composite) {
		Label label = new Label(composite, SWT.WRAP);
		label.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		label.setText(Messages.ServerMonitoringPropertyPage_Description);
		Group group = new Group(composite, SWT.NONE);
		group.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		group.setLayout(new GridLayout(1, false));
		IssueSeverity[] severityValues = IssueSeverity.values();
		severityButtons = new Button[severityValues.length];
		for (int i = 0; i < severityValues.length; i++) {
			severityButtons[i] = createCheckBox(group, severityValues[i]);
		}
	}

	private void createDelaySection(Composite composite) {
		hideButton = new Button(composite, SWT.CHECK);
		hideButton.setText(Messages.ServerMonitoringPropertyPage_HideLabel);
		hideButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				delayText.setEnabled(hideButton.getSelection());
			}
		});
		hideButton.setLayoutData(new GridData(SWT.TOP, SWT.FILL, false, false));
		Group group = new Group(composite, SWT.NONE);
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
					Integer.valueOf(val);
				} catch (NumberFormatException ex) {
					setErrorMessage(Messages.ServerMonitoringPropertyPage_InvalidDelayMessage);
					setValid(false);
					return;
				}
				setErrorMessage(null);
				setValid(true);
			}
		});
		delayText.setText(String.valueOf(DELAY_DEFAULT));
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

	private void init() {
		for (Button button : severityButtons) {
			String nodeName = getNodeName(button);
			button.setSelection(isEnabled(nodeName));
		}
		boolean hideValue = getPreferenceStore().getBoolean(
				MonitorManager.HIDE_KEY);
		hideButton.setSelection(hideValue);
		delayText.setEnabled(hideValue);
		int delay = getPreferenceStore().getInt(MonitorManager.HIDE_TIME_KEY);
		if (delay == 0) {
			delay = 10;
		}
		delayText.setText(String.valueOf(delay));
	}

	private String getNodeName(Button button) {
		return button.getText().toLowerCase();
	}

	private boolean isEnabled(String nodeName) {
		return getPreferenceStore().getBoolean(nodeName);
	}

	private boolean getDefaultValue(String nodeName) {
		return getPreferenceStore().getDefaultBoolean(nodeName);
	}

}
