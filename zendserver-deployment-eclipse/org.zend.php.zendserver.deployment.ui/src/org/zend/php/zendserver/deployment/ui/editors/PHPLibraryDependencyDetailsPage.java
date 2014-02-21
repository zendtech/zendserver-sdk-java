/*******************************************************************************
 * Copyright (c) 2013 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.php.zendserver.deployment.ui.editors;

import org.zend.php.zendserver.deployment.ui.Messages;
import org.zend.php.zendserver.deployment.ui.contentassist.PHPLibrariesProvider;

public class PHPLibraryDependencyDetailsPage extends DependencyDetailsPage {

	public PHPLibraryDependencyDetailsPage(DeploymentDescriptorEditor editor) {
		super(editor, Messages.PHPLibraryDependencyDetailsPage_PHPLibraryTitle,
				Messages.PHPLibraryDependencyDetailsPage_PHPLibraryDetails);

		setNameRequired(Messages.ZendComponentDependencyDetailsPage_Name,
				new PHPLibrariesProvider());
	}

	@Override
	public int getVersionModes() {
		return VersionControl.EQUALS | VersionControl.EXCLUDE
				| VersionControl.RANGE;
	}

}
