/*******************************************************************************
 * Copyright (c) 2013 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.php.library.internal.ui.wizards;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.zend.php.library.core.deploy.LibraryDeployData;
import org.zend.php.library.core.deploy.LibraryDeploymentAttributes;
import org.zend.php.library.internal.ui.LibraryUI;
import org.zend.php.library.internal.ui.Messages;
import org.zend.php.zendserver.deployment.core.debugger.DeploymentAttributes;
import org.zend.php.zendserver.deployment.debug.ui.listeners.IStatusChangeListener;
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

	private LibraryDeployData data;

	public LibraryConfigurationBlock(IStatusChangeListener listener,
			LibraryDeployData data) {
		super(listener);
		this.data = data;
		this.description = Messages.LibraryDeploymentWizard_Description;
	}

	public Composite createContents(final Composite parent,
			final boolean resizeShell) {
		Composite container = super.createContents(parent, resizeShell);
		createDeployCombo(container);
		libraryName = createLabelWithLabel(
				Messages.LibraryConfigurationBlock_Name, null, container);
		libraryVersion = createLabelWithLabel(
				Messages.LibraryConfigurationBlock_Version, null, container);
		warnUpdate = createLabelWithCheckbox(
				Messages.LibraryConfigurationBlock_WarnRedeploy, null,
				container);
		addPHPLibrary = createLabelWithCheckbox(
				Messages.LibraryConfigurationBlock_AddPHPLibrary, null,
				container);
		return container;
	}

	public void initializeFields() {
		libraryName.setText(data.getName());
		libraryVersion.setText(data.getVersion());
		String targetId = data.getTargetId();
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
				warnUpdate.setSelection(data.isWarnSynchronize());
			}

			String addLib = settings
					.get(LibraryDeploymentAttributes.ADD_LIBRARY.getName());
			if (addLib != null) {
				addPHPLibrary.setSelection(Boolean.valueOf(addLib));
			} else {
				addPHPLibrary.setSelection(true);
			}
		} else {
			warnUpdate.setSelection(data.isWarnSynchronize());
			addPHPLibrary.setSelection(true);
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
			return new Status(IStatus.ERROR, LibraryUI.PLUGIN_ID,
					Messages.LibraryConfigurationBlock_NoTargetMessage);
		}
		return new Status(IStatus.OK, LibraryUI.PLUGIN_ID, description);
	}

	public LibraryDeployData getData() {
		if (getTarget() != null) {
			data.setTargetId(getTarget().getId());
		}
		if (warnUpdate.isEnabled()) {
			data.setWarnSynchronize(warnUpdate.getSelection());
		} else {
			data.setWarnSynchronize(false);
		}
		if (addPHPLibrary.isEnabled()) {
			data.setAddPHPLibrary(addPHPLibrary.getSelection());
		}
		return data;
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
