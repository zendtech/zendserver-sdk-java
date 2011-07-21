/*******************************************************************************
 * Copyright (c) May 18, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.php.zendserver.deployment.ui.wizards;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.zend.php.zendserver.deployment.core.descriptor.IModelObject;

/**
 * Wizard for dependency creation
 * 
 * @author Roy, 2011
 */
public class NewDependencyWizard extends Wizard {

	private final IModelObject element;
	private NewDependencyMainPage mainPage;

	public NewDependencyWizard(Object object) {
		if (object == null) {
			throw new IllegalArgumentException();
		}
		this.element = (IModelObject) object;
	}

	@Override
	public void addPages() {
		super.addPages();
		IWizardPage newDependencyMainPage = new NewDependencyMainPage(element);
		addPage(newDependencyMainPage);
	}
	
	@Override
	public boolean performFinish() {
		// TODO Auto-generated method stub
		return false;
	}

}
