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
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;
import org.zend.php.zendserver.deployment.core.descriptor.DeploymentDescriptorPackage;
import org.zend.php.zendserver.deployment.core.descriptor.IModelObject;
import org.zend.php.zendserver.deployment.ui.contentassist.IProposalProvider;

public abstract class DependencyDetailsPage extends DescriptorDetailsPage {

	protected IManagedForm mform;

	protected VersionControl version;
	private TextAssistField name;

	protected boolean isSection = true;
	protected boolean isNameRequired = false;

	private String nameLabel;

	public String sectionTitle;
	public String sectionDescription;

	// provider of proposal list (null if name is not required)
	private IProposalProvider provider;

	private String labelText;

	public DependencyDetailsPage(DeploymentDescriptorEditor editor, String sectionTitle, String sectionDescription) {
		super(editor);
		this.sectionTitle = sectionTitle;
		this.sectionDescription = sectionDescription;
	}

	/**
	 * @return modes this Details page can provide (see {@link VersionControl})
	 */
	public abstract int getVersionModes();

	public void setNameRequired(String nameLabel, IProposalProvider provider) {
		this.isNameRequired = true;
		this.nameLabel = nameLabel;
		this.provider = provider;
	}

	public void setSection(boolean isSection) {
		this.isSection = isSection;
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
		selectionChanged(null, new StructuredSelection(input));
		refresh();
		return true;
	}

	public void setFocus() {
		version.setFocus();
	}

	public boolean isStale() {
		return false;
	}

	public void refresh() {
		version.refresh();
		if (name != null)
			name.refresh();
	}

	public void selectionChanged(IFormPart part, ISelection selection) {
		IStructuredSelection ssel = (IStructuredSelection) selection;
		if (ssel.size() == 1) {
			input = (IModelObject) ssel.getFirstElement();
		} else {
			input = null;
		}
		version.setInput(input);
		if (name != null)
			name.setInput(input);
		super.selectionChanged(part, selection);
	}

	public void createContents(Composite parent) {
		FormToolkit toolkit = mform.getToolkit();
		Composite s = parent;

		if (isSection) {
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
		if (isNameRequired) {
			if (isSection) {
				general = toolkit.createComposite(s);
			} else {
				general = s;
			}
			addName(toolkit, general);
		}

		Composite client = isSection ? toolkit.createComposite(general)
				: general;
		version = new VersionControl(getVersionModes(), input);
		version.createContents(client, toolkit);
		EditorField[] versionFields = version.getFields();
		for (EditorField ef : versionFields) {
			fields.add(ef);
		}
		if (labelText != null) {
			version.setEqualsLabel(labelText);
		}

		if (isSection) {
			// safe to cast into section
			((Section) s).setClient(isNameRequired ? general : client);
		}
	}

	/**
	 * Add the section for this page
	 * 
	 * @param parent
	 * @param toolkit
	 * @return the new section
	 */
	protected Section addSection(Composite parent, FormToolkit toolkit) {
		Section s1 = toolkit.createSection(parent, Section.DESCRIPTION
				| Section.TITLE_BAR);
		s1.marginWidth = 5;
		s1.setText(sectionTitle);
		s1.setDescription(sectionDescription);
		s1.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB,
				TableWrapData.FILL_GRAB));
		return s1;
	}

	/**
	 * Adds a selection component, empty by default
	 * 
	 * @param toolkit
	 * @param general
	 * @return
	 */
	protected void addName(FormToolkit toolkit, Composite general) {
		general.setLayout(new GridLayout(1, true));
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		general.setLayoutData(gd);

		Composite directive = toolkit.createComposite(general);
		directive.setLayout(new GridLayout(3, false));
		gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		directive.setLayoutData(gd);

		provider.init();
		final Composite hint = toolkit.createComposite(directive);
		hint.setLayout(new GridLayout(3, false));
		GridData data = new GridData(SWT.FILL, SWT.TOP, true, true);
		hint.setLayoutData(data);
		name = (TextAssistField) fields.add(new TextAssistField(input,
				DeploymentDescriptorPackage.DEPENDENCY_NAME, nameLabel,
				provider.getNames()));
		name.create(hint, toolkit);
		toolkit.paintBordersFor(hint);
	}
	
	public void setEqualsLabel(String text) {
		this.labelText = text;
	}
}
