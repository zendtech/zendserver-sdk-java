/*******************************************************************************
 * Copyright (c) 2013 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.php.library.internal.ui.wizards;

import org.zend.php.library.core.deploy.LibraryDeployData;
import org.zend.php.library.internal.ui.LibraryUI;
import org.zend.php.library.internal.ui.Messages;
import org.zend.sdklib.target.IZendTarget;

/**
 * @author Wojciech Galanciak, 2013
 * 
 */
public class DeployTargetWizard extends AbstractLibraryWizard {

	private DeployTargetPage page;

	public DeployTargetWizard(IZendTarget target) {
		setWindowTitle(Messages.DeployTargetWizard_Title);
		setDefaultPageImageDescriptor(LibraryUI
				.getImageDescriptor(LibraryUI.IMAGE_DEPLOY_WIZBAN));
		init(target);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.zend.php.zendserver.deployment.debug.ui.wizards.DeploymentWizard#
	 * addPages()
	 */
	public void addPages() {
		page = new DeployTargetPage(getData());
		addPage(page);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.wizard.Wizard#performFinish()
	 */
	public boolean performFinish() {
		setData(page.getData());
		return true;
	}

	private void init(IZendTarget target) {
		LibraryDeployData data = new LibraryDeployData();
		data.setTargetId(target.getId());
		setData(data);
	}

}
