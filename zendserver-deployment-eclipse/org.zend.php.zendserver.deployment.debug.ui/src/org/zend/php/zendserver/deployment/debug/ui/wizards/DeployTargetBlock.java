/*******************************************************************************
 * Copyright (c) 2013 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.php.zendserver.deployment.debug.ui.wizards;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Arrays;

import javax.xml.parsers.ParserConfigurationException;

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
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
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
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.PreferencesUtil;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import org.zend.php.zendserver.deployment.core.debugger.LibraryDeployData;
import org.zend.php.zendserver.deployment.core.descriptor.ProjectType;
import org.zend.php.zendserver.deployment.core.utils.DLTKLibraryUtils;
import org.zend.php.zendserver.deployment.core.utils.LibraryUtils;
import org.zend.php.zendserver.deployment.core.utils.LibraryVersion;
import org.zend.php.zendserver.deployment.debug.ui.Activator;
import org.zend.php.zendserver.deployment.debug.ui.Messages;
import org.zend.php.zendserver.deployment.debug.ui.listeners.IStatusChangeListener;

/**
 * @author Wojciech Galanciak, 2013
 * 
 */
@SuppressWarnings("restriction")
public class DeployTargetBlock extends AbstractLibraryBlock {

	private static final String ZPK_EXTENSION = "zpk"; //$NON-NLS-1$

	private Text zpkText;
	private Text nameText;
	private Text versionText;
	private Button addPHPLibrary;
	private Button zpkEnableButton;
	private Button zpkBrowseButton;
	private ListViewer listViewer;
	private Button phpLibraryEnableButton;
	private Button manageButton;

	private String[] libraryNames = new String[] {};
	private LibraryDeployData data;
	private IScriptProject tempProject;
	private boolean isUpdating = false;

	protected DeployTargetBlock(IStatusChangeListener listener, LibraryDeployData data) {
		super(listener);
		this.data = data;
		this.tempProject = createPlaceholderProject();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.zend.php.library.internal.ui.wizards.AbstractBlock#createContents
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
		zpkEnableButton.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 3, 1));
		zpkEnableButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				data.setEnableAddLibrary(true);
				updateEnablement();
				updateAndValidate();
			}
		});
		new Label(container, SWT.NONE);
		zpkText = new Text(container, SWT.BORDER);
		zpkText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		zpkText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				updateAndValidate();
			}
		});

		zpkBrowseButton = new Button(container, SWT.NONE);
		zpkBrowseButton.setText(Messages.DeployTargetBlock_Browse);
		GridData gd = new GridData(SWT.RIGHT, SWT.TOP, false, false);
		gd.widthHint = 80;
		zpkBrowseButton.setLayoutData(gd);
		zpkBrowseButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				FileDialog dialog = new FileDialog(e.display.getActiveShell(), SWT.SINGLE);
				dialog.setText(Messages.DeployTargetBlock_BrowseDialogTitle);
				dialog.setFilterExtensions(new String[] { "*." + ZPK_EXTENSION }); //$NON-NLS-1$
				final String res = dialog.open();
				if (res == null) {
					return;
				}
				zpkText.setText(res);
			}
		});
		phpLibraryEnableButton = new Button(container, SWT.RADIO);
		phpLibraryEnableButton.setText(Messages.DeployTargetBlock_EnablePHPLibrary);
		phpLibraryEnableButton.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 3, 1));
		phpLibraryEnableButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				data.setEnableAddLibrary(false);
				updateEnablement();
				updateAndValidate();
			}
		});
		new Label(container, SWT.NONE);
		listViewer = new ListViewer(container, SWT.BORDER | SWT.V_SCROLL);
		listViewer.setLabelProvider(new LabelProvider() {

			public String getText(Object element) {
				return (String) element;
			}
		});
		listViewer.setContentProvider(new ArrayContentProvider());
		listViewer.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				updateAndValidate();

			}
		});
		listViewer.getControl().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		manageButton = new Button(container, SWT.PUSH);
		manageButton.setText(Messages.DeployTargetBlock_Manage);
		manageButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				String id = UserLibraryPreferencePage.getPreferencePageId(PHPLanguageToolkit.getDefault());
				PreferencesUtil
						.createPreferenceDialogOn(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), id,
								new String[] { id }, data)
						.open();
				initInput();
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
		lblVersion.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
		lblVersion.setText(Messages.DeployTargetBlock_Version);

		versionText = new Text(container, SWT.BORDER);
		versionText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		versionText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				if (isUpdating)
					return;

				LibraryVersion version = LibraryVersion.byName(versionText.getText());
				if (version == null || version.getMajor() == -1 || version.getMinor() == -1
						|| version.getBuild() == -1) {
					listener.statusChanged(new Status(IStatus.ERROR, Activator.PLUGIN_ID,
							Messages.LibraryConfigurationBlock_VersionInvalidError));
					return;
				}

				listener.statusChanged(
						new Status(IStatus.OK, Activator.PLUGIN_ID, Messages.DeployTargetBlock_Description));
			}
		});

		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);

		addPHPLibrary = createLabelWithCheckbox(Messages.LibraryConfigurationBlock_AddPHPLibrary, null, container);

		initInput();
		zpkEnableButton.setSelection(true);
		updateEnablement();

		return container;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.zend.php.library.internal.ui.wizards.AbstractBlock#validatePage()
	 */
	public IStatus validatePage() {
		if (zpkEnableButton.getSelection()) {
			IPath path = new Path(zpkText.getText());
			if (path.isEmpty()) {
				return new Status(IStatus.INFO, Activator.PLUGIN_ID, Messages.ImportZpkBlock_InvalidZpkPathError);
			}

			File file = path.toFile();
			if (!file.exists()) {
				return new Status(IStatus.ERROR, Activator.PLUGIN_ID, Messages.DeployTargetBlock_ZpkDoesNotExistError);
			}

			if (!file.isFile()) {
				return new Status(IStatus.ERROR, Activator.PLUGIN_ID, Messages.ImportZpkBlock_PathIsNotFileError);
			}

			if (!path.getFileExtension().equalsIgnoreCase(ZPK_EXTENSION)) {
				return new Status(IStatus.ERROR, Activator.PLUGIN_ID, Messages.DeployTargetBlock_InvalidZpkError);
			}

			try {
				Document descriptorDocument = LibraryUtils.getDeploymentDescriptor(file);

				if (LibraryUtils.getProjectType(descriptorDocument) != ProjectType.LIBRARY) {
					return new Status(IStatus.ERROR, Activator.PLUGIN_ID, Messages.ImportZpkBlock_NotLibaryError);
				}

				String name = LibraryUtils.getLibraryName(descriptorDocument);
				if (name == null || name.isEmpty()) {
					return new Status(IStatus.ERROR, Activator.PLUGIN_ID, Messages.ImportZpkBlock_CannotParseNameError);
				}

				String version = LibraryUtils.getLibraryVersion(descriptorDocument);
				if (version == null || version.isEmpty()) {
					return new Status(IStatus.ERROR, Activator.PLUGIN_ID,
							Messages.ImportZpkBlock_CannotParseVersionError);
				}

			} catch (IOException | ParserConfigurationException | SAXException e) {
				String message = MessageFormat.format(Messages.DeployTargetBlock_CouldNotReadZpk_Error,
						e.getLocalizedMessage());
				return new Status(IStatus.ERROR, Activator.PLUGIN_ID, message);
			}
		} else {
			String name = getFirstSelectedLibraryName();
			if (name == null) {
				return new Status(IStatus.INFO, Activator.PLUGIN_ID, Messages.DeployTargetBlock_LIbraryRequiredError);
			}

			String version = DLTKLibraryUtils.getUserLibraryVersion(name, PHPLanguageToolkit.getDefault());
			versionText.setEnabled(version == null);
			if (version == null || version.isEmpty()) {
				return new Status(IStatus.ERROR, Activator.PLUGIN_ID,
						Messages.LibraryConfigurationBlock_VersionRequiredError);
			}
		}
		return new Status(IStatus.OK, Activator.PLUGIN_ID, Messages.DeployTargetBlock_Description);
	}

	public LibraryDeployData getData() {
		data.setName(nameText.getText());
		data.setVersion(versionText.getText());
		if (zpkEnableButton.getSelection()) {
			data.setRoot(new File(zpkText.getText()));
			data.setZpkPackage(true);
		} else {
			String name = getFirstSelectedLibraryName();
			IPath path = new Path(DLTKCore.USER_LIBRARY_CONTAINER_ID)
					.append(UserLibraryManager.makeLibraryName(name, PHPLanguageToolkit.getDefault()));
			data.setRoot(null);
			try {
				IBuildpathContainer container = DLTKCore.getBuildpathContainer(path, tempProject);
				IBuildpathEntry[] entries = container.getBuildpathEntries();
				for (IBuildpathEntry entry : entries) {
					data.setRoot(EnvironmentPathUtils.getLocalPath(entry.getPath()).toFile());
				}
			} catch (ModelException e) {
				Activator.log(e);
			}
		}
		data.setAddPHPLibrary(addPHPLibrary.getSelection());
		return data;
	}

	protected void updateAndValidate() {
		try {
			isUpdating = true;
			IStatus status = validatePage();
			listener.statusChanged(status);
			updateLibraryProperties(status);
		} finally {
			isUpdating = false;
		}
	}

	protected void updateLibraryProperties(IStatus validationStatus) {
		nameText.setText(""); //$NON-NLS-1$
		versionText.setText(""); //$NON-NLS-1$

		if (zpkEnableButton.getSelection()) {
			updateLibraryPropertiesFromZpk(validationStatus);
			return;
		}

		updateLibraryPropertiesFromList(validationStatus);
	}

	protected void updateLibraryPropertiesFromList(IStatus validationStatus) {
		String name = getFirstSelectedLibraryName();
		if (name == null)
			return;

		nameText.setText(name);

		String version = DLTKLibraryUtils.getUserLibraryVersion(name, PHPLanguageToolkit.getDefault());
		versionText.setText(version != null ? version : ""); //$NON-NLS-1$
	}

	protected void updateLibraryPropertiesFromZpk(IStatus validationStatus) {
		if (validationStatus.getSeverity() != IStatus.OK) {
			return;
		}

		try {
			File location = new File(zpkText.getText());
			Document doc = LibraryUtils.getDeploymentDescriptor(location);
			String name = LibraryUtils.getLibraryName(doc);
			String version = LibraryUtils.getLibraryVersion(doc);
			nameText.setText(name);
			versionText.setText(version);
		} catch (IOException | ParserConfigurationException | SAXException e) {
			// should not occur; if it does it means the validation does not
			// work well; log it
			Activator.log(e);
		}
	}

	protected void updateEnablement() {
		boolean enabled = zpkEnableButton.getSelection();
		zpkText.setEnabled(enabled);
		zpkBrowseButton.setEnabled(enabled);
		manageButton.setEnabled(!enabled);
		listViewer.getControl().setEnabled(!enabled);
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

	private void initInput() {
		// store current selection
		String name = getFirstSelectedLibraryName();

		libraryNames = DLTKCore.getUserLibraryNames(PHPLanguageToolkit.getDefault());
		Arrays.sort(libraryNames, String.CASE_INSENSITIVE_ORDER);
		if (libraryNames.length > 0) {
			listViewer.setInput(libraryNames);
			// restore selection
			if (name != null)
				listViewer.setSelection(new StructuredSelection(name), true);
		}
	}
	
	private String getFirstSelectedLibraryName() {
		ISelection selection = listViewer.getSelection();
		if (selection instanceof IStructuredSelection) {
			IStructuredSelection ssel = (IStructuredSelection) selection;
			return (String) ssel.getFirstElement();
		}
		return null;
	}
}
