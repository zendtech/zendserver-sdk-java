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
import org.zend.php.zendserver.deployment.core.descriptor.ChangeEvent;
import org.zend.php.zendserver.deployment.core.descriptor.DeploymentDescriptorPackage;
import org.zend.php.zendserver.deployment.core.descriptor.IDescriptorChangeListener;
import org.zend.php.zendserver.deployment.core.descriptor.IModelObject;
import org.zend.php.zendserver.deployment.ui.Activator;
import org.zend.php.zendserver.deployment.ui.editors.DependencyDetailsPage;
import org.zend.php.zendserver.deployment.ui.editors.DetailsPageProvider;

public class NewDependencyMainPage extends WizardPage {

	private final IModelObject element;

	public NewDependencyMainPage(IModelObject element) {
		super("", "", Activator.getImageDescriptor(Activator.IMAGE_WIZBAN_DEP)); //$NON-NLS-1$ //$NON-NLS-2$
		this.element = element;
	}

	public void createControl(Composite parent) {
		final DetailsPageProvider detailsPageProvider = new DetailsPageProvider(
				null, null);
		final DependencyDetailsPage page = (DependencyDetailsPage) detailsPageProvider
				.getPage(element.getClass());
		element.set(DeploymentDescriptorPackage.DEPENDENCY_MIN, ""); //$NON-NLS-1$
		element.addListener(new IDescriptorChangeListener() {
			public void descriptorChanged(ChangeEvent event) {
				setPageComplete(validate(element));
			}
		});

		final FormToolkit toolkit = new FormToolkit(parent.getDisplay());
		toolkit.setBackground(parent.getBackground());

		final ManagedForm form = new ManagedForm(toolkit,
				toolkit.createScrolledForm(parent));
		page.initialize(form);
		page.setSection(false);
		page.createContents(form.getForm().getBody());
		page.setFormInput(element);

		setTitle(page.sectionTitle);
		setMessage(page.sectionDescription);
		setControl(parent);
		setPageComplete(false);
	}

	protected boolean validate(IModelObject element) {
		// Validate element here
		return true;
	}

}
