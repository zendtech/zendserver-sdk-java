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

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.layout.PixelConverter;
import org.eclipse.jface.resource.JFaceResources;
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
import org.eclipse.ui.forms.widgets.Section;
import org.zend.php.zendserver.deployment.core.descriptor.ChangeEvent;
import org.zend.php.zendserver.deployment.core.descriptor.IDescriptorChangeListener;
import org.zend.php.zendserver.deployment.core.descriptor.IParameter;
import org.zend.php.zendserver.deployment.ui.Messages;

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
	public ResourceListSection(DeploymentDescriptorEditor editor,
			IManagedForm mForm, String title, String description, Composite body) {
		this.title = title;
		this.description = description;
		this.editor = editor;
		this.mForm = mForm;

		createSection(body);
		createActions();
	}

	private void createSection(Composite body) {
		FormToolkit toolkit = mForm.getToolkit();

		Section section = createSection(title, description, body);
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
				upadateEnablement();
				mForm.fireSelectionChanged(spart, event.getSelection());
				mForm.getForm().reflow(true);
			}
		});
		viewer.setContentProvider(new MasterContentProvider());
		viewer.setLabelProvider(new LabelProvider());
		viewer.setInput(editor.getModel());
		editor.getModel().addListener(new IDescriptorChangeListener() {

			public void descriptorChanged(ChangeEvent event) {
				if (event.target instanceof IParameter) {
					refreshViewer((IParameter) event.target);
				}
			}
		});

		Composite buttons = toolkit.createComposite(client, SWT.WRAP);
		GridLayout layout = new GridLayout(1, false);
		buttons.setLayout(layout);
		gd = new GridData(SWT.DEFAULT, SWT.TOP, false, false);
		buttons.setLayoutData(gd);

		addButton = createButton(toolkit, buttons,
				Messages.ResourceListSection_Add);

		editButton = createButton(toolkit, buttons,
				Messages.ResourceListSection_Edit);

		removeButton = createButton(toolkit, buttons,
				Messages.ResourceListSection_Remove);
		
		upadateEnablement();
		
		toolkit.paintBordersFor(client);
	}
	
	protected void upadateEnablement() {
		boolean enabled = !viewer.getSelection().isEmpty();
		editButton.setEnabled(enabled);
		removeButton.setEnabled(enabled);
	}

	private Button createButton(FormToolkit toolkit, Composite buttons,
			String message) {
		Button button = toolkit.createButton(buttons, message, SWT.NONE);
		GridData gd = new GridData(
				SWT.FILL | GridData.VERTICAL_ALIGN_BEGINNING, SWT.TOP, true,
				false);
		button.setLayoutData(gd);

		// Set the default button size
		button.setFont(JFaceResources.getDialogFont());
		PixelConverter converter = new PixelConverter(button);
		int widthHint = converter
				.convertHorizontalDLUsToPixels(IDialogConstants.BUTTON_WIDTH);
		gd.widthHint = Math.max(widthHint,
				button.computeSize(SWT.DEFAULT, SWT.DEFAULT, true).x);
		return button;
	}

	protected void refreshViewer(final IParameter target) {
		viewer.getControl().getDisplay().asyncExec(new Runnable() {

			public void run() {
				viewer.refresh(target);
			}
		});

	}

	private Section createSection(String title, String description, Composite body) {
		FormToolkit toolkit = mForm.getToolkit();

		Section section = toolkit.createSection(body,
				Section.TITLE_BAR | Section.DESCRIPTION | Section.TWISTIE);
		
		Composite client = toolkit.createComposite(section, SWT.WRAP);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;

		client.setLayout(layout);
		toolkit.paintBordersFor(client);

		section.setClient(client);
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
				IStructuredSelection sel = (IStructuredSelection) viewer
						.getSelection();
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
				IStructuredSelection sel = (IStructuredSelection) viewer
						.getSelection();
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
