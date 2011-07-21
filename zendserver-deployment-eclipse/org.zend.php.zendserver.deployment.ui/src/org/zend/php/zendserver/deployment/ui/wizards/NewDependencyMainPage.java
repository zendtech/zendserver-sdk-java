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
import org.zend.php.zendserver.deployment.core.descriptor.IModelObject;
import org.zend.php.zendserver.deployment.ui.Activator;

public class NewDependencyMainPage extends WizardPage {

	private final IModelObject element;

	public NewDependencyMainPage(IModelObject element) {
		super("Add Dependency", "Add Dependency", Activator.getImageDescriptor(Activator.IMAGE_PHP));
		this.element = element;

	}

	public void createControl(Composite parent) {

		
		
	}

}
