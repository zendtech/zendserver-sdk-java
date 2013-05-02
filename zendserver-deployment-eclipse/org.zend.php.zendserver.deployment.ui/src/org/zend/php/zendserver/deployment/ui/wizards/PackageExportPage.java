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
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.zend.php.zendserver.deployment.core.descriptor.DescriptorContainerManager;
import org.zend.php.zendserver.deployment.ui.HelpContextIds;

public class PackageExportPage extends WizardPage implements Listener {

	private class DeploymentContentProvider implements
			IStructuredContentProvider {

		public Object[] getElements(Object input) {
			List<IResource> result = new ArrayList<IResource>();
			if (input instanceof IWorkspace) {
				IWorkspace workspace = (IWorkspace) input;
				IWorkspaceRoot root = workspace.getRoot();
				try {
					IResource[] members = root.members();
					for (IResource member : members) {
						if (member instanceof IContainer) {
							IContainer c = (IContainer) member;
							if (c.findMember(DescriptorContainerManager.DESCRIPTOR_PATH) != null) {
								result.add(member);
							}
						}
					}
				} catch (CoreException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			return result.toArray(new IResource[0]);
		}

		public void dispose() {
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}
	}

	private Combo directoryField;
	private Button browseButton;
	private CheckboxTableViewer tableViewer;
	private List<IProject> initialSelection;
	private Button overwriteButton;
	private boolean overwrite;

	protected PackageExportPage() {
		super(Messages.PackageExportPage_1);
		setDescription(Messages.exportPage_Description);
		setTitle(Messages.exportPage_Title);
	}

	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		container.setLayout(new GridLayout(3, false));
		Label tableLabel = new Label(container, SWT.NONE);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 3;
		tableLabel.setLayoutData(gd);
		tableLabel.setText(Messages.exportPage_TableLabel);
		tableViewer = CheckboxTableViewer.newCheckList(container, SWT.BORDER);
		gd = new GridData(GridData.FILL_BOTH);
		gd.horizontalSpan = 2;
		tableViewer.getTable().setLayoutData(gd);
		tableViewer.setContentProvider(new DeploymentContentProvider());
		tableViewer.setLabelProvider(WorkbenchLabelProvider
				.getDecoratingWorkbenchLabelProvider());
		tableViewer.setInput(ResourcesPlugin.getWorkspace());
		tableViewer.addCheckStateListener(new ICheckStateListener() {

			public void checkStateChanged(CheckStateChangedEvent event) {
				validatePage();
			}
		});
		if (initialSelection != null) {
			tableViewer.setSelection(new StructuredSelection(initialSelection));
			tableViewer.setCheckedElements(initialSelection
					.toArray(new IResource[0]));
		}

		createSelectionButtons(container);

		Label directoryLabel = new Label(container, SWT.NONE);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 3;
		directoryLabel.setLayoutData(gd);
		directoryLabel.setText(Messages.exportPage_DestinationLabel);

		directoryField = new Combo(container, SWT.SINGLE | SWT.BORDER);
		directoryField.addListener(SWT.Modify, this);
		directoryField.addListener(SWT.Selection, this);
		gd = new GridData(GridData.HORIZONTAL_ALIGN_FILL
				| GridData.GRAB_HORIZONTAL);
		gd.horizontalSpan = 2;
		directoryField.setLayoutData(gd);
		setInitialDestination();

		browseButton = new Button(container, SWT.PUSH);
		browseButton.setText(Messages.exportPage_Browse);
		browseButton.addListener(SWT.Selection, this);
		browseButton
				.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));

		if (initialSelection != null) {
			directoryField.setFocus();
		}

		// Overwrite existing files without warning message
		overwriteButton = new Button(container, SWT.CHECK);
		overwriteButton.setText(Messages.PackageExportPage_0);
		overwriteButton.addListener(SWT.Selection, this);
		overwriteButton.setLayoutData(new GridData(
				GridData.HORIZONTAL_ALIGN_FILL));
		overwriteButton.setSelection(true);
		overwrite = true;

		setControl(container);
		validatePage();
		
		PlatformUI
				.getWorkbench()
				.getHelpSystem()
				.setHelp(getControl(),
						HelpContextIds.EXPORTING_THE_APPLICATION_PACKAGE);
	}

	public void handleEvent(Event e) {
		if (e.widget == browseButton) {
			handleDestinationBrowseButtonPressed();
		}
		if (e.widget == overwriteButton) {
			overwrite = overwriteButton.getSelection();
		}
		validatePage();
	}

	public String getDestinationValue() {
		return directoryField.getText().trim();
	}

	public IResource[] getSelectedProjects() {
		Object[] projects = tableViewer.getCheckedElements();
		IResource[] result = new IResource[projects.length];
		for (int i = 0; i < result.length; i++) {
			result[i] = (IResource) projects[i];
		}
		return result;
	}

	public void setInitialSelection(List<IProject> initialSelection) {
		if (initialSelection != null) {
			this.initialSelection = initialSelection;
		}
	}

	public boolean isOverwriteWithoutWarning() {
		return overwrite;
	}

	protected void handleDestinationBrowseButtonPressed() {
		DirectoryDialog dialog = new DirectoryDialog(getContainer().getShell(),
				SWT.SAVE | SWT.SHEET);
		dialog.setMessage(Messages.exportPage_DirectoryDialogMessage);
		dialog.setText(Messages.exportPage_DirectoryDialogTitle);
		dialog.setFilterPath(getDestinationValue());
		String selectedDirectory = dialog.open();
		if (selectedDirectory != null) {
			setErrorMessage(null);
			setDestinationValue(selectedDirectory);
		}
	}

	protected void setDestinationValue(String value) {
		directoryField.setText(value);
	}

	protected void validatePage() {
		if (validateSelection()) {
			if (validateDirectory()) {
				setErrorMessage(null);
				setPageComplete(true);
				return;
			}
		}
		setPageComplete(false);
	}

	private boolean validateSelection() {
		Object[] checkedElements = tableViewer.getCheckedElements();
		if (checkedElements.length == 0) {
			setErrorMessage(Messages.exportPage_TableError);
			return false;
		}
		setErrorMessage(null);
		return true;
	}

	private boolean validateDirectory() {
		String destination = getDestinationValue();
		if (destination.length() != 0) {
			File destDirectory = new File(destination);
			if (destDirectory.exists() && destDirectory.isDirectory()) {
				return true;
			}
			setErrorMessage(Messages.exportPage_DestinationError);
		}
		return false;
	}

	private void setInitialDestination() {
		File home = new File(System.getProperty("user.home")); //$NON-NLS-1$
		if (home.exists()) {
			setDestinationValue(home.getAbsolutePath());
		}
	}

	private void createSelectionButtons(Composite composite) {
		Composite buttonsComposite = new Composite(composite, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		buttonsComposite.setLayout(layout);

		buttonsComposite.setLayoutData(new GridData(
				GridData.VERTICAL_ALIGN_BEGINNING));

		Button selectAll = new Button(buttonsComposite, SWT.PUSH);
		selectAll.setText(Messages.exportPage_SelectAll);
		selectAll.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				tableViewer.setAllChecked(true);
				validatePage();
			}
		});
		setButtonLayoutData(selectAll);

		Button deselectAll = new Button(buttonsComposite, SWT.PUSH);
		deselectAll.setText(Messages.exportPage_DeselectAll);
		deselectAll.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				tableViewer.setAllChecked(false);
				validatePage();
			}
		});
		setButtonLayoutData(deselectAll);
	}

}
