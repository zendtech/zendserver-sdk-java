/*******************************************************************************
 * Copyright (c) May 18, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.php.zendserver.deployment.ui.editors;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IDetailsPage;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapLayout;
import org.zend.php.zendserver.deployment.core.descriptor.IModelObject;

public abstract class SectionDetailPage implements IDetailsPage {

	protected IManagedForm mform;
	protected IModelObject input;
	protected VersionControl version;

	protected boolean addSection = true;

	protected boolean addComponent = false;

	public void setNoSection() {
		addSection = false;
	}

	public void initialize(IManagedForm form) {
		this.mform = form;
	}

	public void dispose() {
	}

	public boolean isDirty() {
		return false;
	}

	public void commit(boolean onSave) {
	}

	public boolean setFormInput(Object input) {
		return false;
	}

	public void setFocus() {
		version.setFocus();
	}

	public boolean isStale() {
		return false;
	}

	public void refresh() {
		version.refresh();
	}

	public void selectionChanged(IFormPart part, ISelection selection) {
		IStructuredSelection ssel = (IStructuredSelection) selection;
		if (ssel.size() == 1) {
			input = (IModelObject) ssel.getFirstElement();
		} else
			input = null;
		version.setInput(input);
		refresh();
	}

	public void createContents(Composite parent) {
		FormToolkit toolkit = mform.getToolkit();
		Composite s = parent;
		
		if (addSection) {
			// create table layout
			TableWrapLayout layout = new TableWrapLayout();
			layout.topMargin = 0;
			layout.leftMargin = 0;
			layout.rightMargin = 0;
			layout.bottomMargin = 0;
			layout.numColumns = 1;
			parent.setLayout(layout);
			// create form section
			s = addSection(parent, toolkit);
		} 

		Composite general = s;
		if (addComponent) {
			general = addSection ? toolkit.createComposite(s) : s;
			addComponent(toolkit, general);
		} 

		Composite client = addSection ? toolkit.createComposite(general) : general;
		version.createContents(client, toolkit);

		if (addSection) {
			// safe to cast into section
			((Section) s).setClient(addComponent ? general : client);
		}
	}

	/**
	 * Add the section for this page
	 * 
	 * @param parent
	 * @param toolkit
	 * @return the new section
	 */
	protected abstract Section addSection(Composite parent, FormToolkit toolkit);

	/**
	 * Adds a selection component, empty by default
	 * 
	 * @param toolkit
	 * @param general
	 * @return
	 */
	protected void addComponent(FormToolkit toolkit, Composite general) {
	}

	/**
	 * Adds content assist support to name, empty by default
	 */
	protected void createContentAssist() {
	}
}
