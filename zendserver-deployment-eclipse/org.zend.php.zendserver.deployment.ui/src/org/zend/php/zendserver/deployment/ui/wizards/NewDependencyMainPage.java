/*******************************************************************************
 * Copyright (c) May 18, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.php.zendserver.deployment.ui.wizards;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.ManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.zend.php.zendserver.deployment.core.descriptor.IModelObject;
import org.zend.php.zendserver.deployment.ui.Activator;
import org.zend.php.zendserver.deployment.ui.editors.DetailsPageProvider;
import org.zend.php.zendserver.deployment.ui.editors.SectionDetailPage;

public class NewDependencyMainPage extends WizardPage {

	private final IModelObject element;

	public NewDependencyMainPage(IModelObject element) {
		super("Title", "Title", Activator
				.getImageDescriptor(Activator.IMAGE_WIZBAN_DEP));
		setMessage("Instructions");

		this.element = element;
	}

	public void createControl(Composite parent) {
		final DetailsPageProvider detailsPageProvider = new DetailsPageProvider(
				null, null);
		final SectionDetailPage page = (SectionDetailPage) detailsPageProvider
				.getPage(element.getClass());
		
		final FormToolkit toolkit = new FormToolkit(parent.getDisplay());
		toolkit.setBackground(parent.getBackground());
		
		final ManagedForm form = new ManagedForm(toolkit, toolkit.createScrolledForm(parent));
		page.initialize(form);
		page.setNoSection();
		page.createContents(form.getForm().getBody());
		page.setFormInput(element);
		
		setControl(parent);
		setPageComplete(false);
	}
}
