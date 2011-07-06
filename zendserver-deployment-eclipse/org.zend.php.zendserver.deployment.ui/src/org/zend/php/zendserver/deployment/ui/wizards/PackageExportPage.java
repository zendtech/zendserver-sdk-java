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
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.zend.php.zendserver.deployment.core.descriptor.DescriptorContainerManager;

public class PackageExportPage extends WizardPage implements Listener {

	private class DeploymentContentProvider implements IStructuredContentProvider {

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
	private IResource initialSelection;

	protected PackageExportPage() {
		super("Package Export");
		setDescription("Create Application Deployment Package");
		setTitle("Export Deployment Package");
	}

	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		container.setLayout(new GridLayout(2, false));
		tableViewer = CheckboxTableViewer.newCheckList(container, SWT.BORDER);
		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.horizontalSpan = 2;
		tableViewer.getTable().setLayoutData(gd);
		tableViewer.setContentProvider(new DeploymentContentProvider());
		tableViewer.setLabelProvider(WorkbenchLabelProvider.getDecoratingWorkbenchLabelProvider());
		tableViewer.setInput(ResourcesPlugin.getWorkspace());
		tableViewer.addCheckStateListener(new ICheckStateListener() {

			public void checkStateChanged(CheckStateChangedEvent event) {
				validatePage();
			}
		});
		if (initialSelection != null) {
			tableViewer.setSelection(new StructuredSelection(initialSelection));
			tableViewer.setChecked(initialSelection, true);
		}

		Label directoryLabel = new Label(container, SWT.NONE);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 2;
		directoryLabel.setLayoutData(gd);
		directoryLabel.setText("To director&y:");

		directoryField = new Combo(container, SWT.SINGLE | SWT.BORDER);
		directoryField.addListener(SWT.Modify, this);
		directoryField.addListener(SWT.Selection, this);
		GridData data = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL);
		directoryField.setLayoutData(data);

		browseButton = new Button(container, SWT.PUSH);
		browseButton.setText("Browse");
		browseButton.addListener(SWT.Selection, this);
		browseButton.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));

		if (initialSelection != null) {
			directoryField.setFocus();
		}
		
		setControl(container);
		validatePage();
	}

	public void handleEvent(Event e) {
		if (e.widget == browseButton) {
			handleDestinationBrowseButtonPressed();
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

	protected void handleDestinationBrowseButtonPressed() {
		DirectoryDialog dialog = new DirectoryDialog(getContainer().getShell(), SWT.SAVE
				| SWT.SHEET);
		dialog.setMessage("Select a directory to export to.");
		dialog.setText("Export to Directory");
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
			setErrorMessage("Select at least one project to export");
			return false;
		}
		return true;
	}

	private boolean validateDirectory() {
		String destination = getDestinationValue();
		File destDirectory = new File(destination);
		if (destDirectory.exists() && destDirectory.isDirectory()) {
			return true;
		}
		setErrorMessage("Destination does not exist or is not a directory");
		return false;
	}

	public void setSelection(IResource object) {
		if (tableViewer != null) {
			tableViewer.setSelection(new StructuredSelection(object));
			tableViewer.setChecked(object, true);
		} else {
			initialSelection = object;
		}
	}

}
