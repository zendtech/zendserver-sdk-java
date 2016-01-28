/*******************************************************************************
 * Copyright (c) 2013 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.php.zendserver.deployment.debug.ui.wizards;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.zend.php.server.ui.IAddServerListener;
import org.zend.php.server.ui.ServersCombo;
import org.zend.php.zendserver.deployment.core.debugger.LibraryDeployData;
import org.zend.php.zendserver.deployment.core.debugger.LibraryDeploymentAttributes;
import org.zend.php.zendserver.deployment.core.utils.LibraryVersion;
import org.zend.php.zendserver.deployment.debug.core.config.LaunchUtils;
import org.zend.php.zendserver.deployment.debug.ui.Activator;
import org.zend.php.zendserver.deployment.debug.ui.Messages;
import org.zend.php.zendserver.deployment.debug.ui.listeners.IStatusChangeListener;
import org.zend.sdklib.manager.TargetsManager;
import org.zend.sdklib.target.IZendTarget;
import org.zend.webapi.core.connection.data.values.ZendServerVersion;

/**
 * @author Wojciech Galanciak, 2013
 * 
 */
public class LibraryConfigurationBlock extends AbstractLibraryBlock {

	private ServersCombo serversCombo;
	private Button addLibraryButton;
	private Button setAsDefaultButton;
	private Text nameText;
	private Text versionText;

	private LibraryDeployData data;
	protected IDialogSettings dialogSettings;
	private boolean isUpdating = false;

	public LibraryConfigurationBlock(IStatusChangeListener listener, LibraryDeployData data,
			IDialogSettings dialogSettings) {
		super(listener);
		this.dialogSettings = dialogSettings;
		this.data = data;
	}

	public Composite createContents(final Composite parent, final boolean resizeShell) {
		Composite container = super.createContents(parent, resizeShell);

		serversCombo = new ServersCombo(ServersCombo.DEPLOYMENT_FILTER, true, false);
		serversCombo.createControl(container);
		serversCombo.getCombo().addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				listener.statusChanged(validatePage());
			}
		});
		serversCombo.setListener(new IAddServerListener() {
			public void serverAdded(String name) {
				listener.statusChanged(validatePage());
			}
		});

		Label lblName = new Label(container, SWT.NONE);
		lblName.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
		lblName.setText(Messages.LibraryConfigurationBlock_Name);

		nameText = new Text(container, SWT.BORDER);
		nameText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		nameText.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				if (isUpdating)
					return;

				listener.statusChanged(validatePage());
			}
		});

		Label lblVersion = new Label(container, SWT.NONE);
		lblVersion.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
		lblVersion.setText(Messages.LibraryConfigurationBlock_Version);

		versionText = new Text(container, SWT.BORDER);
		versionText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		versionText.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				if (isUpdating)
					return;

				listener.statusChanged(validatePage());
			}
		});

		new Label(container, SWT.NULL);
		setAsDefaultButton = new Button(container, SWT.CHECK);
		setAsDefaultButton.setText(Messages.LibraryConfigurationBlock_SetAsDefaultVersion);
		setAsDefaultButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		if (data.isEnableAddLibrary()) {
			new Label(container, SWT.NULL);
			addLibraryButton = new Button(container, SWT.CHECK);
			addLibraryButton.setText(Messages.LibraryConfigurationBlock_AddPHPLibrary);
			addLibraryButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		}

		initializeFields();
		updateEnablement();

		return container;
	}

	private void initializeFields() {
		try {
			isUpdating = true;

			String name = data.getName();
			nameText.setText(name != null ? name : ""); //$NON-NLS-1$

			String version = data.getVersion();
			versionText.setText(version != null ? version : ""); //$NON-NLS-1$

			String targetId = data.getTargetId();
			if ((targetId == null || targetId.isEmpty())) {
				IDialogSettings settings = dialogSettings;
				if (settings != null) {
					serversCombo.selectByTarget(settings.get(LibraryDeploymentAttributes.TARGET_ID.getName()));
				}
			} else {
				if (data.getProject() != null) {
					IZendTarget target = LaunchUtils.getTargetFromPreferences(data.getProject().getName());
					if (target != null && targetId.equals(target.getId())) {
						targetId = target.getId();
					}
				}
				serversCombo.selectByTarget(targetId);
			}

			IDialogSettings settings = dialogSettings;
			boolean setAsDefault = false;
			boolean addLibrary = true;
			if (settings != null) {
				String addLib = settings.get(LibraryDeploymentAttributes.ADD_LIBRARY.getName());
				if (addLib != null) {
					addLibrary = Boolean.valueOf(addLib);
				}
				setAsDefault = settings.getBoolean(LibraryDeploymentAttributes.SET_AS_DEFAULT.getName());
			}
			if (addLibraryButton != null) {
				addLibraryButton.setSelection(addLibrary);
			}
			setAsDefaultButton.setSelection(setAsDefault);
		} finally {
			isUpdating = false;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.zend.php.library.internal.ui.wizards.AbstractBlock#validatePage()
	 */
	public IStatus validatePage() {
		if (getTarget() == null) {
			return new Status(IStatus.ERROR, Activator.PLUGIN_ID, Messages.LibraryConfigurationBlock_NoTargetMessage);
		}
		if (!TargetsManager.checkMinVersion(getTarget(), ZendServerVersion.byName("6.1.0"))) { //$NON-NLS-1$
			return new Status(IStatus.ERROR, Activator.PLUGIN_ID,
					Messages.LibraryConfigurationBlock_DeployNotSupportedError);
		}
		if (nameText.getText().isEmpty()) {
			return new Status(IStatus.ERROR, Activator.PLUGIN_ID, Messages.LibraryConfigurationBlock_NameRequiredError);
		}
		if (versionText.getText().isEmpty()) {
			return new Status(IStatus.ERROR, Activator.PLUGIN_ID,
					Messages.LibraryConfigurationBlock_VersionRequiredError);
		}
		LibraryVersion version = LibraryVersion.byName(versionText.getText());
		if (version == null || version.getMajor() == -1 || version.getMinor() == -1 || version.getBuild() == -1) {
			return new Status(IStatus.ERROR, Activator.PLUGIN_ID,
					Messages.LibraryConfigurationBlock_VersionInvalidError);
		}
		return new Status(IStatus.OK, Activator.PLUGIN_ID, Messages.LibraryDeploymentWizard_Description);
	}

	public LibraryDeployData getData() {
		if (getTarget() != null) {
			data.setTargetId(getTarget().getId());
		}
		data.setName(nameText.getText());
		data.setVersion(versionText.getText());
		if (data.isEnableAddLibrary() && addLibraryButton.isEnabled()) {
			data.setAddPHPLibrary(addLibraryButton.getSelection());
		} else {
			data.setAddPHPLibrary(false);
		}
		data.setMakeDefault(setAsDefaultButton.getSelection());
		return data;
	}

	protected void updateEnablement() {
		String name = data.getName();
		nameText.setEnabled(name == null || name.isEmpty());
		String version = data.getVersion();
		versionText.setEnabled(version == null || version.isEmpty());
	}

	private IZendTarget getTarget() {
		return serversCombo.getSelectedTarget();
	}
}
