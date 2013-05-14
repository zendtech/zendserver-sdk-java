/*******************************************************************************
 * Copyright (c) 2013 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.php.library.internal.ui.wizards;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.zend.php.library.core.deploy.LibraryDeploymentAttributes;
import org.zend.php.library.internal.ui.LibraryUI;
import org.zend.php.library.internal.ui.Messages;
import org.zend.php.zendserver.deployment.core.debugger.DeploymentAttributes;
import org.zend.php.zendserver.deployment.core.debugger.IDeploymentHelper;
import org.zend.php.zendserver.deployment.core.descriptor.DescriptorContainerManager;
import org.zend.php.zendserver.deployment.core.descriptor.IDeploymentDescriptor;
import org.zend.php.zendserver.deployment.core.descriptor.IDescriptorContainer;
import org.zend.php.zendserver.deployment.debug.core.config.DeploymentHelper;
import org.zend.php.zendserver.deployment.debug.ui.listeners.IStatusChangeListener;
import org.zend.php.zendserver.deployment.debug.ui.wizards.AbstractBlock;
import org.zend.php.zendserver.deployment.ui.actions.AddTargetAction;
import org.zend.php.zendserver.deployment.ui.targets.TargetsCombo;
import org.zend.sdklib.target.IZendTarget;

/**
 * @author Wojciech Galanciak, 2013
 * 
 */
public class LibraryConfigurationBlock extends AbstractBlock {

	private TargetsCombo targetsCombo = new TargetsCombo(true);

	private Button warnUpdate;
	private Button addPHPLibrary;

	private String description;

	private Label libraryName;
	private Label libraryVersion;

	public LibraryConfigurationBlock(IStatusChangeListener listener,
			String description) {
		super(listener);
		this.description = description;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.zend.php.zendserver.deployment.debug.ui.wizards.AbstractBlock#
	 * createContents(org.eclipse.swt.widgets.Composite, boolean)
	 */
	public Composite createContents(final Composite parent,
			final boolean resizeShell) {
		super.createContents(parent, resizeShell);
		getContainer().setLayoutData(
				new GridData(SWT.FILL, SWT.FILL, true, false));
		createDeployCombo(getContainer());
		libraryName = createLabelWithLabel(
				Messages.LibraryConfigurationBlock_Name, null, getContainer());
		libraryVersion = createLabelWithLabel(
				Messages.LibraryConfigurationBlock_Version, null,
				getContainer());
		warnUpdate = createLabelWithCheckbox(
				Messages.LibraryConfigurationBlock_WarnRedeploy, null,
				getContainer());
		addPHPLibrary = createLabelWithCheckbox(
				Messages.LibraryConfigurationBlock_AddPHPLibrary, null,
				getContainer());
		return getContainer();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.zend.php.zendserver.deployment.debug.ui.wizards.AbstractBlock#
	 * initializeFields
	 * (org.zend.php.zendserver.deployment.core.debugger.IDeploymentHelper)
	 */
	public void initializeFields(IDeploymentHelper helper) {
		libraryName.setText(helper.getAppName());
		IDescriptorContainer descContainer = DescriptorContainerManager
				.getService().openDescriptorContainer(
						ResourcesPlugin.getWorkspace().getRoot()
								.getProject(helper.getProjectName()));
		IDeploymentDescriptor descModel = descContainer.getDescriptorModel();
		libraryVersion.setText(descModel.getReleaseVersion());
		String targetId = helper.getTargetId();
		if ((targetId == null || targetId.isEmpty())) {
			IDialogSettings settings = getDialogSettings();
			if (settings != null) {
				targetsCombo.select(settings.get(DeploymentAttributes.TARGET_ID
						.getName()));
			}
		} else {
			targetsCombo.select(targetId);
		}
		IDialogSettings settings = getDialogSettings();
		if (settings != null) {
			String warnUpdateVal = settings
					.get(LibraryDeploymentAttributes.WARN_UPDATE.getName());
			if (warnUpdateVal != null) {
				warnUpdate.setSelection(Boolean.valueOf(warnUpdateVal));
			} else {
				warnUpdate.setSelection(helper.isWarnUpdate());
			}

			String addLib = settings
					.get(LibraryDeploymentAttributes.ADD_LIBRARY.getName());
			if (addLib != null) {
				addPHPLibrary.setSelection(Boolean.valueOf(addLib));
			} else {
				Boolean value = Boolean
						.valueOf(helper.getExtraAttributes().get(
								LibraryDeploymentAttributes.ADD_LIBRARY
										.getName()));
				addPHPLibrary.setSelection(value);
			}
		} else {
			warnUpdate.setSelection(helper.isWarnUpdate());
			Boolean value = Boolean.valueOf(helper.getExtraAttributes().get(
					LibraryDeploymentAttributes.ADD_LIBRARY.getName()));
			addPHPLibrary.setSelection(value);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.zend.php.zendserver.deployment.debug.ui.wizards.AbstractBlock#
	 * validatePage()
	 */
	public IStatus validatePage() {
		if (getTarget() == null) {
			return new Status(IStatus.ERROR, LibraryUI.PLUGIN_ID,
					Messages.LibraryConfigurationBlock_NoTargetMessage);
		}
		return new Status(IStatus.OK, LibraryUI.PLUGIN_ID, description);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.zend.php.zendserver.deployment.debug.ui.wizards.AbstractBlock#getHelper
	 * ()
	 */
	public IDeploymentHelper getHelper() {
		DeploymentHelper helper = new DeploymentHelper();
		if (getTarget() != null) {
			helper.setTargetId(getTarget().getId());
			helper.setTargetHost(getTarget().getHost().getHost());
		}
		helper.setOperationType(IDeploymentHelper.DEPLOY);
		if (warnUpdate.isEnabled()) {
			helper.setWarnUpdate(warnUpdate.getSelection());
		} else {
			helper.setWarnUpdate(false);
		}
		Map<String, String> extraAttributes = new HashMap<String, String>();
		if (addPHPLibrary.isEnabled()) {
			extraAttributes.put(
					LibraryDeploymentAttributes.ADD_LIBRARY.getName(),
					String.valueOf(addPHPLibrary.getSelection()));
		} else {
			extraAttributes.put(
					LibraryDeploymentAttributes.ADD_LIBRARY.getName(),
					String.valueOf(false));
		}
		helper.setExtraAtttributes(extraAttributes);
		return helper;
	}

	public void setDeployComboEnabled(boolean value) {
		targetsCombo.setEnabled(value);
	}

	public void setWarnUpdateEnabled(boolean value) {
		warnUpdate.setEnabled(value);
	}

	private IZendTarget getTarget() {
		return targetsCombo.getSelected();
	}

	private void createDeployCombo(Composite container) {
		targetsCombo.setLabel(Messages.LibraryConfigurationBlock_DeployTo);
		targetsCombo.createControl(container);
		targetsCombo.getCombo().addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent e) {
				listener.statusChanged(validatePage());
			}
		});
		targetsCombo.setAddTargetListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent e) {
				AddTargetAction addTarget = new AddTargetAction();
				addTarget.run();
				IZendTarget newTarget = addTarget.getTarget();
				if (newTarget != null) {
					targetsCombo.updateItems();
					targetsCombo.select(newTarget.getId());
					listener.statusChanged(validatePage());
				}
			}
		});
	}

}
