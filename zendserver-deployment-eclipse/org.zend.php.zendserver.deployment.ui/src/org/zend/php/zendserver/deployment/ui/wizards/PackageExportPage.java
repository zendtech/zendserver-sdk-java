/*******************************************************************************
 * Copyright (c) 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.php.zendserver.deployment.ui.wizards;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.php.internal.core.PHPToolkitUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.HelpEvent;
import org.eclipse.swt.events.HelpListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.dialogs.ContainerSelectionDialog;
import org.eclipse.ui.internal.help.WorkbenchHelpSystem;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.zend.php.zendserver.deployment.core.DeploymentNature;
import org.zend.php.zendserver.deployment.core.descriptor.DescriptorContainerManager;
import org.zend.php.zendserver.deployment.core.descriptor.IDeploymentDescriptor;
import org.zend.php.zendserver.deployment.core.descriptor.ProjectType;
import org.zend.php.zendserver.deployment.ui.Activator;
import org.zend.php.zendserver.deployment.ui.HelpContextIds;

public class PackageExportPage extends WizardPage implements Listener {

	private class DeploymentContentProvider implements
			IStructuredContentProvider {

		public Object[] getElements(Object input) {
			List<IProject> result = new ArrayList<IProject>();
			
			if (input instanceof IWorkspace) {
				IWorkspace workspace = (IWorkspace) input;
				IProject[] projects = workspace.getRoot().getProjects();
				sortByNameIgnoreCase(projects);
				
				for (IProject project : projects) {
					try {
						if (PHPToolkitUtil.isPhpProject(project)) {
							result.add(project);
						}
					} catch (CoreException e) {
						Activator.log(e);
					}
				}
			}
			
			return result.toArray(new IProject[result.size()]);
		}

		public void dispose() {
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}
		
		private void sortByNameIgnoreCase(IProject[] projects) {
			Arrays.sort(projects, new Comparator<IProject>() {
				@Override
				public int compare(IProject p1, IProject p2) {
					return p1.getName().compareToIgnoreCase(p2.getName());
				}
			});
		}
	}

	private static final String S_DESTINATION = "destination"; //$NON-NLS-1$
	private static final String S_OVERWRITE = "overwrite"; //$NON-NLS-1$
	private static final String S_PRODUCTION = "production"; //$NON-NLS-1$
	private static final String S_USE_EXISTING_CONFIGS = "useExistingConfigs"; //$NON-NLS-1$
	private static final String S_CONFIGS_DIRECTORY = "configsDirectory"; //$NON-NLS-1$
	
	private Combo destinationField;
	private Button browseButton;
	private TableViewer projectList;
	private IProject initialSelection;
	private Button overwriteCheckbox;
	
	private Button productionCheckbox;
	private Group configsGroup;
	private Button reuseConfigsRadio;
	private Button alternativeConfigsRadio;
	private Combo configsDirectoryField;
	private Button workspaceButton;
	private Button fileSystemButton;

	protected PackageExportPage() {
		super(Messages.PackageExportPage_Name);
		setTitle(Messages.PackageExportPage_Title);
		setDescription(Messages.PackageExportPage_Description);
	}

	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		container.setLayout(new GridLayout());
		
		createProjectSelectionGroup(container);
		createDestinationGroup(container);
		createProductionModeGroup(container);

		setControl(container);
				
		getControl().setData(WorkbenchHelpSystem.HELP_KEY, HelpContextIds.EXPORTING_THE_APPLICATION_PACKAGE); 
		getControl().addHelpListener(new HelpListener() { 
		     public void helpRequested(HelpEvent arg0) 
		     { 
		          org.eclipse.swt.program.Program.launch(HelpContextIds.EXPORTING_THE_APPLICATION_PACKAGE); 
		     } 
		});
		
		// validate the page
		handleEvent(new Event());
	}
	
	private void createProjectSelectionGroup(Composite parent) {
		Label label = new Label(parent, SWT.NONE);
		label.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		label.setText(Messages.PackageExportPage_ProjectListLabel);
		
		projectList = new TableViewer(parent, SWT.BORDER);
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd.minimumHeight = 100;
		gd.heightHint = 150;
		projectList.getControl().setLayoutData(gd);
		
		projectList.setContentProvider(new DeploymentContentProvider());
		projectList.setLabelProvider(WorkbenchLabelProvider
				.getDecoratingWorkbenchLabelProvider());
		projectList.setInput(ResourcesPlugin.getWorkspace());
		
		if (initialSelection != null) {
			projectList.setSelection(new StructuredSelection(initialSelection), true);
		}
		
		projectList.getControl().addListener(SWT.Selection, this);
	}
	
	private void createDestinationGroup(Composite parent) {
		Group group = new Group(parent, SWT.NONE);
		group.setText(Messages.PackageExportPage_DestinationGroupText);
		group.setLayout(new GridLayout(2, false));
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.verticalIndent = 8;
		group.setLayoutData(gd);

		destinationField = new Combo(group, SWT.SINGLE | SWT.BORDER);
		setInitialDestination();
		destinationField.addListener(SWT.Modify, this);
		destinationField.addListener(SWT.Selection, this);
		destinationField.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		if (initialSelection != null) {
			destinationField.setFocus();
		}

		browseButton = new Button(group, SWT.PUSH);
		browseButton.setText(Messages.PackageExportPage_BrowseButtonText);
		browseButton.addListener(SWT.Selection, this);

		// Overwrite existing files without warning message
		overwriteCheckbox = new Button(group, SWT.CHECK);
		overwriteCheckbox.setText(Messages.PackageExportPage_OverwriteCheckboxText);
		initializeCheckbox(S_OVERWRITE, overwriteCheckbox, true);
		overwriteCheckbox.addListener(SWT.Selection, this);
		overwriteCheckbox.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false, 2, 1));
	}
	
	private void createProductionModeGroup(Composite parent) {
		productionCheckbox = new Button(parent, SWT.CHECK);
		productionCheckbox.setText(Messages.PackageExportPage_ProductionCheckboxText);
		initializeCheckbox(S_PRODUCTION, productionCheckbox, false);
		productionCheckbox.addListener(SWT.Selection, this);
		GridData gd = new GridData(SWT.FILL, SWT.NONE, true, false, 2, 1);
		gd.verticalIndent = 8;
		productionCheckbox.setLayoutData(gd);
		
		configsGroup = new Group(parent, SWT.NONE);
		configsGroup.setText(Messages.PackageExportPage_ConfigsGroupText);
		configsGroup.setLayout(new GridLayout(2, false));
		configsGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		reuseConfigsRadio = new Button(configsGroup, SWT.RADIO);
		reuseConfigsRadio.setText(Messages.PackageExportPage_ReuseRadioText);
		reuseConfigsRadio.addListener(SWT.Selection, this);
		reuseConfigsRadio.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false, 2, 1));
		
		alternativeConfigsRadio = new Button(configsGroup, SWT.RADIO);
		alternativeConfigsRadio.setText(Messages.PackageExportPage_AlternativeConfigsRadioText);
		alternativeConfigsRadio.addListener(SWT.Selection, this);
		alternativeConfigsRadio.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false, 2, 1));
		
		initializeRadioGroup(S_USE_EXISTING_CONFIGS, reuseConfigsRadio, alternativeConfigsRadio);
		
		configsDirectoryField = new Combo(configsGroup, SWT.SINGLE | SWT.BORDER);
		initializeCombo(S_CONFIGS_DIRECTORY, configsDirectoryField);
		configsDirectoryField.addListener(SWT.Modify, this);
		configsDirectoryField.addListener(SWT.Selection, this);
		configsDirectoryField.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false, 2, 1));

		workspaceButton = new Button(configsGroup, SWT.PUSH);
		workspaceButton.setText(Messages.PackageExportPage_WorkspaceButtonText);
		workspaceButton.addListener(SWT.Selection, this);
		workspaceButton
				.setLayoutData(new GridData(SWT.END, SWT.NONE, true, false));
		
		fileSystemButton = new Button(configsGroup, SWT.PUSH);
		fileSystemButton.setText(Messages.PackageExportPage_FileSystemButtonText);
		fileSystemButton.addListener(SWT.Selection, this);
		fileSystemButton
				.setLayoutData(new GridData(SWT.END, SWT.NONE, false, false));
	}
	
	public void handleEvent(Event e) {
		if (e.widget == browseButton) {
			handleDestinationBrowseButtonPressed();
		} else if (e.widget == workspaceButton) {
			handleWorkspaceButtonPressed();
		} else if (e.widget == fileSystemButton) {
			handleFileSystemButtonPressed();
		}
		
		updateEnableState();
		validatePage();
	}

	public IProject getSelectedProject() {
		IStructuredSelection selection = (IStructuredSelection) projectList.getSelection();
		return (IProject) selection.getFirstElement();
	}

	public String getDestinationDirectory() {
		return destinationField.getText().trim();
	}
	
	public boolean isProductionModeSelected() {
		return productionCheckbox.getSelection();
	}
	
	public String getConfigsDirectory() {
		if (reuseConfigsRadio.getSelection()) {
			return getSelectedProject().getFolder("config/autoload").getLocation().toOSString(); //$NON-NLS-1$
		} else {
			return configsDirectoryField.getText().trim();
		}
	}
	
	public void setInitialSelection(IProject initialSelection) {
		if (initialSelection != null) {
			this.initialSelection = initialSelection;
		}
	}

	public boolean isOverwriteSelected() {
		return overwriteCheckbox.getSelection();
	}

	protected void handleDestinationBrowseButtonPressed() {
		DirectoryDialog dialog = new DirectoryDialog(getContainer().getShell(),
				SWT.SAVE | SWT.SHEET);
		dialog.setMessage(Messages.PackageExportPage_DestinationDialogMessage);
		dialog.setText(Messages.PackageExportPage_DirectorySelectionDialogTitle);
		dialog.setFilterPath(getDestinationDirectory());
		String selectedDirectory = dialog.open();
		if (selectedDirectory != null) {
			destinationField.setText(selectedDirectory);
		}
	}

	private void handleWorkspaceButtonPressed() {
		ContainerSelectionDialog dialog = new ContainerSelectionDialog(
				getContainer().getShell(),
				getSelectedProject(),
				false,
				Messages.PackageExportPage_ConfigsDirectorySelectionDialogMessage);
		dialog.setTitle(Messages.PackageExportPage_DirectorySelectionDialogTitle);
		dialog.open();
		
	    Object[] results = dialog.getResult();
	    if (results != null && results.length == 1 && results[0] instanceof IPath) {
	        IPath path = (IPath) results[0];
	        String absolutePath = ResourcesPlugin.getWorkspace().getRoot().getFolder(path).getLocation().toOSString();
	        configsDirectoryField.setText(absolutePath);
	    }
	}

	private void handleFileSystemButtonPressed() {
		DirectoryDialog dialog = new DirectoryDialog(getContainer().getShell(),
				SWT.SAVE | SWT.SHEET);
		dialog.setMessage(Messages.PackageExportPage_ConfigsDirectorySelectionDialogMessage);
		dialog.setText(Messages.PackageExportPage_DirectorySelectionDialogTitle);
		dialog.setFilterPath(getConfigsDirectory());
		String selectedDirectory = dialog.open();
		if (selectedDirectory != null) {
			configsDirectoryField.setText(selectedDirectory);
		}
	}

	protected void validatePage() {
		setMessage(null, NONE);
		
		boolean canFinish = validateSelection() && validateDestinationDirectory()
				&& validateProductionMode() && validateConfigsDirectory();
		
		setPageComplete(canFinish);
	}

	private boolean validateSelection() {
		if (projectList.getTable().getItemCount() == 0) {
			setMessage(Messages.PackageExportPage_NoProjectAvailableError, ERROR);
			return false;
		}
		
		IProject project = getSelectedProject();
		if (project == null) {
			setMessage(Messages.PackageExportPage_NoProjectSelectedError, ERROR);
			return false;
		}
		
		try {
			if (!project.hasNature(DeploymentNature.ID)) {
				setMessage(Messages.PackageExportPage_NoDeploymentSupportWarning, WARNING);
			}
		} catch (CoreException e) {
			Activator.log(e);
			setMessage(Messages.PackageExportPage_ProjectCorruptedError, ERROR);
			return false;
		}
		
		return true;
	}

	private boolean validateDestinationDirectory() {
		String destinationDirectory = getDestinationDirectory();
		if (destinationDirectory.isEmpty() && getMessageType() == NONE) {
			setMessage(Messages.PackageExportPage_SelectDestinationMessage, NONE);
			return false;
		}
		
		File destDir = new File(destinationDirectory);
		if (!destDir.exists()) {
			setMessage(Messages.PackageExportPage_DestinationNotExistError, ERROR);
			return false;
		}
			
		if (!destDir.isDirectory()) {
			setMessage(Messages.PackageExportPage_DestinationNotDirectoryError, ERROR);
			return false;
		}
		return true;
	}

	private boolean validateProductionMode() {
		if (isProductionModeSelected()) {
			IProject selectedProject = getSelectedProject();
			if (!ZFUtils.isZF2Project(selectedProject)) {
				setMessage(Messages.PackageExportPage_NotZF2ProjectError, ERROR);
				return false;
			} else if (isPHPLibrary(selectedProject)) {
				setMessage(Messages.PackageExportPage_PHPLibraryError, ERROR);
				return false;
			}
		}
		return true;
	}
	
	private boolean validateConfigsDirectory() {
		if (isProductionModeSelected()) {
			if (getMessageType() == NONE) {
				setMessage(Messages.PackageExportPage_SelectConfigDirectoryMessage, NONE);
			}
			
			String configsDirectory = getConfigsDirectory();
			if (configsDirectory.isEmpty()) {
				return false;
			}
			
			File configsDir = new File(configsDirectory);
			if (!configsDir.exists()) {
				setMessage(Messages.PackageExportPage_ConfigDirectoryNotExistError, ERROR);
				return false;
			}
				
			if (!configsDir.isDirectory()) {
				setMessage(Messages.PackageExportPage_ConfigLocationNotDirectoryError, ERROR);
				return false;
			}
		}
		
		return true;
	}

	private void setInitialDestination() {
		initializeCombo(S_DESTINATION, destinationField);
		if (destinationField.getText().trim().isEmpty()) {
			// no history available - default to the user home directory
			File home = new File(System.getProperty("user.home")); //$NON-NLS-1$
			if (home.exists()) {
				destinationField.setText(home.getAbsolutePath());
			}
		}
	}
	
	private void updateEnableState() {
		boolean prodMode = productionCheckbox.getSelection();
		configsGroup.setEnabled(prodMode);
		reuseConfigsRadio.setEnabled(prodMode);
		alternativeConfigsRadio.setEnabled(prodMode);
		
		boolean reuseConfigs = reuseConfigsRadio.getSelection();
		configsDirectoryField.setEnabled(prodMode && !reuseConfigs);
		workspaceButton.setEnabled(prodMode && !reuseConfigs);
		fileSystemButton.setEnabled(prodMode && !reuseConfigs);
	}
	
	private boolean isPHPLibrary(IProject project) {
		IDeploymentDescriptor descriptor = DescriptorContainerManager
				.getService().openDescriptorContainer(project).getDescriptorModel();
		return descriptor.getType() == ProjectType.LIBRARY;
	}

	public void saveSettings() {
		saveCombo(S_DESTINATION, destinationField);
		saveCheckbox(S_OVERWRITE, overwriteCheckbox);
		saveCheckbox(S_PRODUCTION, productionCheckbox);
		saveRadioGroup(S_USE_EXISTING_CONFIGS, reuseConfigsRadio, alternativeConfigsRadio);
		saveCombo(S_CONFIGS_DIRECTORY, configsDirectoryField);
	}

	private void initializeCombo(String key, Combo combo) {
		IDialogSettings settings = getDialogSettings();

		for (int i = 0; i < 6; i++) {
			String curr = settings.get(key + String.valueOf(i));
			if (curr != null && combo.indexOf(curr) == -1) {
				combo.add(curr);
			}
		}

		if (combo.getItemCount() > 0) {
			combo.setText(combo.getItem(0));
		}
	}

	private void saveCombo(String key, Combo combo) {
		IDialogSettings settings = getDialogSettings();

		if (!combo.getText().trim().isEmpty()) {
			settings.put(key + String.valueOf(0), combo.getText().trim());
			String[] items = combo.getItems();
			int nEntries = Math.min(items.length, 5);
			for (int i = 0; i < nEntries; i++) {
				settings.put(key + String.valueOf(i + 1), items[i].trim());
			}
		}
	}
	
	private void initializeCheckbox(String key, Button checkbox, boolean defaultValue) {
		IDialogSettings settings = getDialogSettings();
		
		boolean selection = defaultValue;
		if (settings.get(key) != null) {
			selection = settings.getBoolean(key);
		}
		
		checkbox.setSelection(selection);
	}
	
	private void saveCheckbox(String key, Button checkbox) {
		getDialogSettings().put(key, checkbox.getSelection());
	}
	
	private void initializeRadioGroup(String key, Button... radioButtons) {
		IDialogSettings settings = getDialogSettings();
		
		int selectionIndex = 0;
		if (settings.get(key) != null) {
			selectionIndex = settings.getInt(key);
			if (selectionIndex > radioButtons.length - 1) {
				selectionIndex = 0;
			}
		}
		
		radioButtons[selectionIndex].setSelection(true);
	}

	private void saveRadioGroup(String key, Button... radioButtons) {
		int selectionIndex = 0;
		for (Button radio : radioButtons) {
			if (radio.getSelection()) {
				getDialogSettings().put(key, selectionIndex);
			} else {
				selectionIndex++;
			}
		}
	}

}
