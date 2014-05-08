/*******************************************************************************
 * Copyright (c) 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.php.zendserver.deployment.ui.wizards;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.zend.php.zendserver.deployment.core.targets.OpenShiftTargetInitializer;
import org.zend.php.zendserver.deployment.ui.Activator;
import org.zend.sdklib.SdkException;
import org.zend.sdklib.internal.target.OpenShiftTarget;

/**
 * OpenShift target creation wizard.
 * 
 * @author Wojciech Galanciak, 2012
 * 
 */
public class OpenShiftTargetWizard extends Wizard {

	private OpenShiftTargetData data;

	private OpenShiftTargetPage osPage;
	private OpenShiftDomainPage domainPage;
	private OpenShiftEulaPage eulaPage;

	public OpenShiftTargetWizard(OpenShiftTargetData data) {
		super();
		this.data = data;
		if (data.getGearProfiles().isEmpty()) {
			this.domainPage = new OpenShiftDomainPage(this, data);
		}
		this.osPage = new OpenShiftTargetPage(this, data);
		this.eulaPage = new OpenShiftEulaPage(data);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.wizard.Wizard#createPageControls(org.eclipse.swt.widgets
	 * .Composite)
	 */
	public void createPageControls(Composite pageContainer) {
		super.createPageControls(pageContainer);
		setWindowTitle(Messages.OpenShiftTargetWizard_Title);
		setNeedsProgressMonitor(true);
		setDefaultPageImageDescriptor(Activator
				.getImageDescriptor(Activator.IMAGE_WIZ_OPENSHIFT));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.wizard.Wizard#addPages()
	 */
	public void addPages() {
		super.addPages();
		if (domainPage != null) {
			addPage(domainPage);
		}
		addPage(osPage);
		addPage(eulaPage);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.wizard.Wizard#performFinish()
	 */
	public boolean performFinish() {
		osPage.updateData();
		eulaPage.updateData();
		try {
			getContainer().run(true, false, new IRunnableWithProgress() {

				public void run(IProgressMonitor monitor)
						throws InvocationTargetException, InterruptedException {
					try {
						monitor.beginTask(
								Messages.OpenShiftTargetWizard_Description,
								IProgressMonitor.UNKNOWN);
						final String message = data.getTarget().create(
								data.getName(), data.getGearProfile(),
								data.hasMySQLSupport(),
								data.getMySqlCartridge(),
								data.getCartridge().getName());
						String domain = data.getTarget().getDomainName();
						String libraDomain = data.getTarget().getLibraDomain();
						OpenShiftTargetInitializer initializer = new OpenShiftTargetInitializer(
								data.getName(), domain, libraDomain, data
										.getPassword(), data
										.getConfirmPassword(), data
										.getCartridge());
						IStatus status = initializer.initialize();
						if (status.getSeverity() == IStatus.ERROR) {
							throw new InvocationTargetException(new Exception(
									status.getMessage()));
						}
						if (message != null) {
							Display.getDefault().asyncExec(new Runnable() {

								public void run() {
									MySQLCredentialsDialog dialog = new MySQLCredentialsDialog(
											Display.getDefault()
													.getActiveShell(), message);
									dialog.open();
								}
							});
						}
						monitor.done();
					} catch (SdkException e) {
						Activator.log(e);
						throw new InvocationTargetException(e.getCause());
					}
				}
			});
		} catch (InvocationTargetException e) {
			Activator.log(e);
			Throwable a = e.getTargetException();
			if (a != null) {
				eulaPage.setErrorMessage(OpenShiftTarget.getOpenShiftMessage(a));
				return false;
			}
		} catch (InterruptedException e) {
			Activator.log(e);
		}
		return true;
	}

}
