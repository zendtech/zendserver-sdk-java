/*******************************************************************************
 * Copyright (c) 2012 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.php.zendserver.monitor.ui.preferences;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbenchPropertyPage;
import org.eclipse.ui.dialogs.PropertyPage;
import org.osgi.service.prefs.BackingStoreException;
import org.zend.php.zendserver.monitor.core.Activator;
import org.zend.php.zendserver.monitor.core.MonitorManager;
import org.zend.php.zendserver.monitor.internal.ui.Messages;
import org.zend.sdklib.manager.TargetsManager;
import org.zend.sdklib.target.IZendTarget;
import org.zend.webapi.core.connection.data.values.IssueSeverity;

/**
 * Property page for Zend Server monitoring customization.
 * 
 * @author Wojciech Galanciak, 2012
 * 
 */
public class ServerMonitoringPropertyPage extends PropertyPage implements
		IWorkbenchPropertyPage {

	private Button[] severityButtons;
	private IEclipsePreferences prefs;

	public ServerMonitoringPropertyPage() {
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
		init();
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.preference.PreferencePage#performOk()
	 */
	public boolean performOk() {
		boolean isDirty = false;
		try {
			for (Button button : severityButtons) {
				boolean selection = button.getSelection();
				String nodeName = getNodeName(button);
				Boolean current = getCurrentValue(nodeName);
				if ((current != null && current != selection)
						|| (current == null && selection)) {
					prefs.putBoolean(getNodeName(button), selection);
					isDirty = true;
				}
			}
			if (isDirty) {
				prefs.flush();
			}
		} catch (BackingStoreException e) {
			Activator.log(e);
			return false;
		}
		return true;
	}

	private Boolean getCurrentValue(String nodeName) {
		String val = prefs.get(nodeName, (String) null);
		return val != null ? Boolean.valueOf(val) : null;
	}

	private void init() {
		IProject project = getProject();
		if (project != null) {
			prefs = new ProjectScope(project).getNode(Activator.PLUGIN_ID);
		}
		for (Button button : severityButtons) {
			String nodeName = getNodeName(button);
			button.setEnabled(isEnabled());
			button.setSelection(prefs.getBoolean(nodeName, false));
		}
	}

	private String getNodeName(Button button) {
		return button.getText().toLowerCase();
	}

	private void createSeveritySection(Composite composite) {
		Label label = new Label(composite, SWT.WRAP);
		label.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		label.setText(Messages.ServerMonitoringPropertyPage_Description);
		Group group = new Group(composite, SWT.NONE);
		group.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, true));
		group.setLayout(new GridLayout(1, false));
		group.setText(Messages.ServerMonitoringPropertyPage_SeverityGroupLabel);
		IssueSeverity[] severityValues = IssueSeverity.values();
		severityButtons = new Button[severityValues.length];
		for (int i = 0; i < severityValues.length; i++) {
			severityButtons[i] = createCheckBox(group, severityValues[i]);
		}
	}

	private boolean isEnabled() {
		TargetsManager manager = new TargetsManager();
		IZendTarget[] targets = manager.getTargets();
		for (IZendTarget target : targets) {
			if (prefs
					.getBoolean(MonitorManager.ENABLED + target.getId(), false)) {
				return true;
			}
		}
		return false;
	}

	private Button createCheckBox(Composite parent, IssueSeverity severity) {
		Button button = new Button(parent, SWT.CHECK | SWT.LEFT);
		button.setText(severity.getName());
		button.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		return button;
	}

	private IProject getProject() {
		final IAdaptable adaptable = getElement();
		Object project = Platform.getAdapterManager().getAdapter(adaptable,
				IProject.class);
		if (project instanceof IProject) {
			return (IProject) project;
		}
		return null;
	}

}
