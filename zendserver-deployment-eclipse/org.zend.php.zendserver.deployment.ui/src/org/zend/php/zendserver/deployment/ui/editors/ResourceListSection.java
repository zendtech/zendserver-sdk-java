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
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.zend.php.zendserver.deployment.core.descriptor.ChangeEvent;
import org.zend.php.zendserver.deployment.core.descriptor.IDescriptorChangeListener;
import org.zend.php.zendserver.deployment.core.descriptor.IParameter;


public abstract class ResourceListSection {
	
	private class MasterContentProvider implements IStructuredContentProvider {

		public void dispose() {
			// empty
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			// empty
		}

		public Object[] getElements(Object input) {
			return ResourceListSection.this.getElements(input);
		}
		
	}
	
	private static class MasterLabelProvider extends LabelProvider {
		@Override
		public String getText(Object element) {
			return super.getText(element);
		}
	}
	
	private IManagedForm mForm;
	
	private Button addButton;
	private Button editButton;
	private Button removeButton;

	private DeploymentDescriptorEditor editor;

	private TableViewer viewer;

	private String title;

	private String description;
	

	/**
	 * @param id
	 * @param title
	 */
	public ResourceListSection(DeploymentDescriptorEditor editor, IManagedForm mForm, String title, String description) {
		this.title = title;
		this.description = description;
		this.editor = editor;
		this.mForm = mForm;
		
		createSection();
		createActions();
	}
	
	private void createSection() {
		FormToolkit toolkit = mForm.getToolkit();
		
		Section section = createSection(title, description);
		Composite client = (Composite) section.getClient();
		final SectionPart spart = new SectionPart(section);
		mForm.addPart(spart);
		
		Table t = toolkit.createTable(client, SWT.NULL);
		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.heightHint = 200;
		gd.widthHint = 100;
		t.setLayoutData(gd);
		viewer = new TableViewer(t);
		viewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				mForm.fireSelectionChanged(spart, event.getSelection());
				mForm.getForm().reflow(true);
			}
		});
		viewer.setContentProvider(new MasterContentProvider());
		viewer.setLabelProvider(new WorkbenchLabelProvider());
		viewer.setInput(editor.getModel());
		editor.getModel().addListener(new IDescriptorChangeListener() {
			
			public void descriptorChanged(ChangeEvent event) {
				if (event.target instanceof IParameter) {
					refreshViewer((IParameter)event.target);
				}
			}
		});
		
		Composite buttons = toolkit.createComposite(client, SWT.WRAP);
		GridLayout layout = new GridLayout(1, false);
		buttons.setLayout(layout);
		gd = new GridData(SWT.DEFAULT, SWT.TOP, false, false);
		buttons.setLayoutData(gd);
		
		addButton = toolkit.createButton(buttons, "Add...", SWT.NONE);
		gd = new GridData(SWT.FILL, SWT.TOP, true, false);
		addButton.setLayoutData(gd);
		
		editButton = toolkit.createButton(buttons, "Edit...", SWT.NONE);
		gd = new GridData(SWT.FILL, SWT.TOP, true, false);
		editButton.setLayoutData(gd);
		
		removeButton = toolkit.createButton(buttons, "Remove...", SWT.NONE);
		gd = new GridData(SWT.FILL, SWT.TOP, true, false);
		removeButton.setLayoutData(gd);
	}
	
	protected void refreshViewer(final IParameter target) {
		viewer.getControl().getDisplay().asyncExec(new Runnable() {
			
			public void run() {
				viewer.refresh(target);
			}
		});
		
	}

	private Section createSection(String title, String description) {
		ScrolledForm form = mForm.getForm();
		FormToolkit toolkit = mForm.getToolkit();
		
		Section section =
				toolkit.createSection(
					form.getBody(), Section.TITLE_BAR|Section.DESCRIPTION);
		section.setActiveToggleColor(
			toolkit.getHyperlinkGroup().getActiveForeground());
		section.setToggleColor(
			toolkit.getHyperlinkGroup().getActiveForeground());
		
		Composite client = toolkit.createComposite(section, SWT.WRAP);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;

		client.setLayout(layout);
		toolkit.paintBordersFor(client);
		
		section.setClient(client);
		GridData gd = new GridData(GridData.FILL_BOTH);
		section.setLayoutData(gd);
		
		section.setExpanded(true);
		section.addExpansionListener(new ExpansionAdapter() {
			public void expansionStateChanged(ExpansionEvent e) {
				mForm.getForm().reflow(false);
			}
		});
		section.setText(title);
		section.setDescription(description);
		
		return section;
	}
	
	private void createActions() {
		addButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				addPath();
				viewer.refresh();
			}
		});
		
		editButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				IStructuredSelection sel = (IStructuredSelection) viewer.getSelection();
				if (sel.isEmpty()) {
					return;
				}
				editPath(sel.getFirstElement());
				viewer.refresh();
			}
		});
		
		removeButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				IStructuredSelection sel = (IStructuredSelection) viewer.getSelection();
				if (sel.isEmpty()) {
					return;
				}
				removePath(sel.getFirstElement());
				viewer.refresh();
			}
		});
	}
	
	abstract protected void removePath(Object element);
	
	abstract protected void editPath(Object element);
	
	abstract protected void addPath();

	public abstract Object[] getElements(Object input);

	public void refresh() {
		viewer.refresh();
	}
}
