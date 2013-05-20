/*******************************************************************************
 * Copyright (c) 2013 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.php.library.internal.ui.wizards;

import java.io.File;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.IBuildpathContainer;
import org.eclipse.dltk.core.IBuildpathEntry;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.dltk.core.environment.EnvironmentPathUtils;
import org.eclipse.dltk.internal.core.UserLibraryManager;
import org.eclipse.dltk.ui.preferences.UserLibraryPreferencePage;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.php.internal.core.PHPLanguageToolkit;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.PreferencesUtil;
import org.w3c.dom.Document;
import org.zend.php.library.core.LibraryUtils;
import org.zend.php.library.core.LibraryVersion;
import org.zend.php.library.core.deploy.LibraryDeployData;
import org.zend.php.library.internal.ui.LibraryUI;
import org.zend.php.library.internal.ui.Messages;
import org.zend.php.zendserver.deployment.core.descriptor.ProjectType;
import org.zend.php.zendserver.deployment.debug.ui.listeners.IStatusChangeListener;

/**
 * @author Wojciech Galanciak, 2013
 * 
 */
public class DeployTargetBlock extends AbstractBlock {

	private Text zpkText;
	private Text nameText;
	private Text versionText;
	private Button warnUpdate;
	private Button addPHPLibrary;

	private Button zpkEnableButton;
	private Button zpkBrowseButton;

	private List list;

	private String[] libraryNames;
	private LibraryDeployData data;
	private String description;
	private Button phpLibraryEnableButton;
	private Button manageButton;
	private IScriptProject tempProject;

	protected DeployTargetBlock(IStatusChangeListener listener,
			LibraryDeployData data) {
		super(listener);
		this.data = data;
		this.tempProject = createPlaceholderProject();
		this.description = Messages.DeployTargetBlock_Description;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.zend.php.library.internal.ui.wizards.AbstractBlock#createContents
	 * (org.eclipse.swt.widgets.Composite, boolean)
	 */
	public Composite createContents(Composite parent, boolean resizeShell) {
		Composite container = super.createContents(parent, resizeShell);
		GridLayout layout = new GridLayout(3, false);
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		container.setLayout(layout);
		zpkEnableButton = new Button(container, SWT.RADIO);
		zpkEnableButton.setText(Messages.DeployTargetBlock_EnableZpk);
		zpkEnableButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				data.setEnableAddLibrary(false);
				updateEnablement();
				listener.statusChanged(validatePage());
			}
		});
		zpkText = new Text(container, SWT.BORDER);
		zpkText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false,
				1, 1));
		zpkText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				listener.statusChanged(validatePage());
			}
		});

		zpkBrowseButton = new Button(container, SWT.NONE);
		zpkBrowseButton.setText(Messages.DeployTargetBlock_Browse);
		GridData gd = new GridData(SWT.RIGHT, SWT.TOP, false, false);
		gd.widthHint = 80;
		zpkBrowseButton.setLayoutData(gd);
		zpkBrowseButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				FileDialog dialog = new FileDialog(e.display.getActiveShell(),
						SWT.SINGLE);
				dialog.setText(Messages.DeployTargetBlock_BrowseDialogTitle);
				dialog.setFilterExtensions(new String[] { "*.zpk" }); //$NON-NLS-1$
				final String res = dialog.open();
				if (res == null) {
					return;
				}
				zpkText.setText(res);
				File deploymentFile = LibraryUtils
						.unzipDescriptor(new File(res));
				Document doc = LibraryUtils
						.getDeploymentDescriptor(deploymentFile);
				if (LibraryUtils.getProjectType(doc) == ProjectType.LIBRARY) {
					String name = LibraryUtils.getLibraryName(doc);
					String version = LibraryUtils.getLibraryVersion(doc);
					nameText.setText(name);
					versionText.setText(version);
					listener.statusChanged(validatePage());
				} else {
					listener.statusChanged(new Status(IStatus.ERROR,
							LibraryUI.PLUGIN_ID,
							Messages.ImportZpkBlock_NotLibaryError));
				}
				deploymentFile.deleteOnExit();
				updateEnablement();
			}
		});
		phpLibraryEnableButton = new Button(container, SWT.RADIO);
		phpLibraryEnableButton
				.setText(Messages.DeployTargetBlock_EnablePHPLibrary);
		phpLibraryEnableButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				updateEnablement();
				listener.statusChanged(validatePage());
			}
		});
		final ListViewer listViewer = new ListViewer(container, SWT.BORDER
				| SWT.V_SCROLL);
		listViewer.setLabelProvider(new LabelProvider() {

			public String getText(Object element) {
				return (String) element;
			}
		});
		listViewer.setContentProvider(new ArrayContentProvider());
		list = listViewer.getList();
		libraryNames = DLTKCore.getUserLibraryNames(PHPLanguageToolkit
				.getDefault());
		if (libraryNames.length > 0) {
			listViewer.setInput(libraryNames);
		}
		list.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		list.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent e) {
				String name = libraryNames[list.getSelectionIndex()];
				String version = DLTKCore.getUserLibraryVersion(name,
						PHPLanguageToolkit.getDefault());
				nameText.setText(name);
				if (version != null) {
					versionText.setText(version);
				}
				data.setEnableAddLibrary(false);
				versionText.setEnabled(version == null);
				listener.statusChanged(validatePage());
			}
		});
		manageButton = new Button(container, SWT.PUSH);
		manageButton.setText(Messages.DeployTargetBlock_Manage);
		manageButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				String id = UserLibraryPreferencePage
						.getPreferencePageId(PHPLanguageToolkit.getDefault());
				PreferencesUtil.createPreferenceDialogOn(
						PlatformUI.getWorkbench().getActiveWorkbenchWindow()
								.getShell(), id, new String[] { id }, data)
						.open();
				libraryNames = DLTKCore.getUserLibraryNames(PHPLanguageToolkit
						.getDefault());
				if (libraryNames.length > 0) {
					listViewer.setInput(libraryNames);
				}
			}
		});
		gd = new GridData(SWT.RIGHT, SWT.TOP, false, false);
		gd.widthHint = 80;
		manageButton.setLayoutData(gd);
		Label lblName = new Label(container, SWT.NONE);
		lblName.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
		lblName.setText(Messages.DeployTargetBlock_Name);

		nameText = new Text(container, SWT.BORDER);
		nameText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

		new Label(container, SWT.NONE);
		Label lblVersion = new Label(container, SWT.NONE);
		lblVersion.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false,
				false, 1, 1));
		lblVersion.setText(Messages.DeployTargetBlock_Version);

		versionText = new Text(container, SWT.BORDER);
		versionText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false));

		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		warnUpdate = createLabelWithCheckbox(
				Messages.LibraryConfigurationBlock_WarnRedeploy, null,
				container);

		new Label(container, SWT.NONE);
		addPHPLibrary = createLabelWithCheckbox(
				Messages.LibraryConfigurationBlock_AddPHPLibrary, null,
				container);
		zpkEnableButton.setSelection(true);
		updateEnablement();
		return container;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.zend.php.library.internal.ui.wizards.AbstractBlock#validatePage()
	 */
	public IStatus validatePage() {
		if (zpkEnableButton.getSelection()) {
			if (zpkText != null) {
				if (!new File(zpkText.getText()).exists()) {
					return new Status(IStatus.ERROR, LibraryUI.PLUGIN_ID,
							Messages.DeployTargetBlock_ZpkDoesNotExistError);
				}
				if (!zpkText.getText().endsWith(".zpk")) { //$NON-NLS-1$
					return new Status(IStatus.ERROR, LibraryUI.PLUGIN_ID,
							Messages.DeployTargetBlock_InvalidZpkError);
				}
			}
		} else {
			if (list != null && list.getSelectionIndex() == -1) {
				return new Status(IStatus.ERROR, LibraryUI.PLUGIN_ID,
						Messages.DeployTargetBlock_LIbraryRequiredError);
			}
		}
		if (versionText.getText().isEmpty()) {
			return new Status(IStatus.ERROR, LibraryUI.PLUGIN_ID,
					Messages.LibraryConfigurationBlock_VersionRequiredError);
		} else {
			LibraryVersion version = LibraryVersion.byName(versionText
					.getText());
			if (version == null || version.getMajor() == -1
					|| version.getMinor() == -1 || version.getBuild() == -1) {
				return new Status(IStatus.ERROR, LibraryUI.PLUGIN_ID,
						Messages.LibraryConfigurationBlock_VersionInvalidError);
			}
		}
		return new Status(IStatus.OK, LibraryUI.PLUGIN_ID, description);
	}

	public LibraryDeployData getData() {
		data.setName(nameText.getText());
		data.setVersion(versionText.getText());
		if (zpkEnableButton.getSelection()) {
			data.setRoot(new File(zpkText.getText()));
		} else {
			IPath path = new Path(DLTKCore.USER_LIBRARY_CONTAINER_ID)
					.append(UserLibraryManager.makeLibraryName(
							libraryNames[list.getSelectionIndex()],
							PHPLanguageToolkit.getDefault()));
			try {
				IBuildpathContainer container = DLTKCore.getBuildpathContainer(
						path, tempProject);
				IBuildpathEntry[] entries = container.getBuildpathEntries();
				for (IBuildpathEntry entry : entries) {
					data.setRoot(EnvironmentPathUtils.getLocalPath(
							entry.getPath()).toFile());
				}
			} catch (ModelException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (warnUpdate.isEnabled()) {
			data.setWarnSynchronize(warnUpdate.getSelection());
		} else {
			data.setWarnSynchronize(false);
		}
		if (data.isEnableAddLibrary() && addPHPLibrary.isEnabled()) {
			data.setAddPHPLibrary(addPHPLibrary.getSelection());
		} else {
			data.setAddPHPLibrary(false);
		}
		return data;
	}

	private void updateEnablement() {
		boolean enabled = zpkEnableButton.getSelection();
		zpkText.setEnabled(enabled);
		zpkBrowseButton.setEnabled(enabled);
		manageButton.setEnabled(!enabled);
		list.setEnabled(!enabled);
		addPHPLibrary.setEnabled(enabled);
		if (enabled) {
			nameText.setEnabled(!enabled);
			versionText.setEnabled(!enabled);
		} else {
			nameText.setEnabled(false);
		}
	}

	private static IScriptProject createPlaceholderProject() {
		String name = "####internal"; //$NON-NLS-1$
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		while (true) {
			IProject project = root.getProject(name);
			if (!project.exists()) {
				return DLTKCore.create(project);
			}
			name += '1';
		}
	}

}
