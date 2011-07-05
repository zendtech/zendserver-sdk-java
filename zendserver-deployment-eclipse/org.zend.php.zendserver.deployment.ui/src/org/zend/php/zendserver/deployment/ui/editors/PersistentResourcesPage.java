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
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.ScrolledForm;


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
		super.createFormContent(managedForm);
		
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		ScrolledForm form = managedForm.getForm();
		form.getBody().setLayout(layout);
		
		persistent = new ResourceListSection(editor, managedForm, "Persistent resources", "Persistent resources to be kept during upgrade.") {
			
			@Override
			public Object[] getElements(Object input) {
				List<String> list = editor.getModel().getPersistentResources();
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
				
				for (int i = 0; i < newPaths.length; i++) {
					editor.getModel().getPersistentResources().add(newPaths[i]);
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
				editor.getModel().getPersistentResources().remove(currPath);
				editor.getModel().getPersistentResources().add(newPath);
			}
			
			@Override
			protected void removePath(Object element) {
				String path = (String) element;
				editor.getModel().getPersistentResources().remove(path);
			}
		};
		
	}
	
	public void refresh() {
		persistent.refresh();
	}
}
