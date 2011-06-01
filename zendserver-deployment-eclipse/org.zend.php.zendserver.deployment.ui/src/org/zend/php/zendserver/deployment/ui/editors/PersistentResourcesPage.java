/*******************************************************************************
 * Copyright (c) 2000, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.zend.php.zendserver.deployment.ui.editors;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.zend.php.zendserver.deployment.ui.Activator;
import org.zend.php.zendserver.deployment.ui.actions.DeployAppInCloudAction;
import org.zend.php.zendserver.deployment.ui.actions.ExportApplicationAction;
import org.zend.php.zendserver.deployment.ui.actions.RunApplicationAction;


public class PersistentResourcesPage extends DescriptorEditorPage {
	
	private DeploymentDescriptorEditor editor;
	private ResourceListSection persistent;

	/**
	 * @param id
	 * @param title
	 */
	public PersistentResourcesPage(DeploymentDescriptorEditor editor) {
		super(editor, "upgrade", "Persistent Resources");
		this.editor = editor;
	}
	protected void createFormContent(IManagedForm managedForm) {
		ScrolledForm form = managedForm.getForm();
		form.setText(getTitle());
		form.setImage(Activator.getDefault().getImage(Activator.IMAGE_DESCRIPTOR_REMOVAL));
		IToolBarManager mgr = form.getToolBarManager();
		mgr.add(new RunApplicationAction());
		mgr.add(new DeployAppInCloudAction());
		mgr.add(new ExportApplicationAction());
		mgr.update(true);

		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		form.getBody().setLayout(layout);
		
		persistent = new ResourceListSection(editor, managedForm, "Persistent resources", "Persistent resources to be kept during upgrade.") {
			
			@Override
			public Object[] getElements(Object input) {
				List<String> list = editor.getModel().getDescriptor().getPersistentResources();
				return editor.getResourceMapper().getResources(list.toArray(new String[list.size()]));
			}
			
			@Override
			protected void addPath() {
				Shell shell = editor.getSite().getShell();
				IProject root = editor.getProject();
				String[] newPaths = OpenFileDialog.openMany(shell, root, "Add path", "Select path:", null);
				if (newPaths == null) {
					return;
				}
				try {
					for (int i = 0; i < newPaths.length; i++) {
						editor.getModel().addPersistentResource(newPaths[i]);
					}
				} catch (CoreException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			@Override
			protected void editPath(Object element) {
				String currPath = (String) element;
				
				Shell shell = editor.getSite().getShell();
				IProject root = editor.getProject();
				String newPath = OpenFileDialog.open(shell, root, "Change path", "Select path:", currPath.toString());
				if (newPath == null) {
					return;
				}
				try {
					editor.getModel().removePersistentResource(currPath);
					editor.getModel().addPersistentResource(newPath);
				} catch (CoreException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			@Override
			protected void removePath(Object element) {
				String path = (String) element;
				try {
					editor.getModel().removePersistentResource(path);
				} catch (CoreException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};
		
	}
	
	public void refresh() {
		persistent.refresh();
	}
}
