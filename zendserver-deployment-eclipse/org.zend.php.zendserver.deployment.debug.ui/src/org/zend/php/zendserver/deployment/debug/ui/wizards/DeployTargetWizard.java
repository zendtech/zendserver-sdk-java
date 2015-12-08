/*******************************************************************************
 * Copyright (c) 2013 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.php.zendserver.deployment.debug.ui.wizards;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.zend.php.zendserver.deployment.core.debugger.LibraryDeployData;
import org.zend.php.zendserver.deployment.debug.ui.Activator;
import org.zend.php.zendserver.deployment.debug.ui.Messages;
import org.zend.sdklib.target.IZendTarget;

/**
 * @author Wojciech Galanciak, 2013
 * 
 */
public class DeployTargetWizard extends AbstractLibraryWizard {

	private DeployTargetPage page;

	public DeployTargetWizard(IZendTarget target) {
		setWindowTitle(Messages.DeployTargetWizard_Title);
		setDefaultPageImageDescriptor(Activator
				.getImageDescriptor(Activator.IMAGE_WIZBAN_DEPLOY_LIBRARY));
		setNeedsProgressMonitor(true);
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
		try {
			getContainer().run(false, false, new IRunnableWithProgress() {

				public void run(IProgressMonitor monitor)
						throws InvocationTargetException, InterruptedException {
					monitor.beginTask(
							Messages.BuildpathContainerWizard_InitJob,
							IProgressMonitor.UNKNOWN);
					setData(page.getData());
					monitor.done();
				}
			});
		} catch (InvocationTargetException e) {
			Activator.log(e);
		} catch (InterruptedException e) {
			Activator.log(e);
		}
		if (getData().getRoot() == null) {
			page.setErrorMessage(Messages.DeployTargetWizard_EmptyLibraryError);
			return false;
		}
		return true;
	}

	private void init(IZendTarget target) {
		LibraryDeployData data = new LibraryDeployData();
		data.setTargetId(target.getId());
		setData(data);
	}

}
