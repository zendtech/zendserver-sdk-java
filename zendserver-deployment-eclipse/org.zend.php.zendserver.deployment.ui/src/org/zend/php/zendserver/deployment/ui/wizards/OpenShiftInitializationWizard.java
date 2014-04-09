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
import org.zend.php.zendserver.deployment.core.targets.EclipseApiKeyDetector;
import org.zend.php.zendserver.deployment.core.targets.OpenShiftTargetInitializer;
import org.zend.php.zendserver.deployment.ui.Activator;
import org.zend.sdklib.SdkException;
import org.zend.sdklib.internal.target.OpenShiftTarget;
import org.zend.sdklib.internal.target.OpenShiftTarget.Type;
import org.zend.sdklib.target.IZendTarget;

/**
 * Zend Server initialization wizard for OpenShift targets.
 * 
 * @author Wojciech Galanciak, 2012
 * 
 */
public class OpenShiftInitializationWizard extends Wizard {

	private OpenShiftTargetData data;

	private OpenShiftTargetPage osPage;
	private OpenShiftEulaPage eulaPage;
	private IZendTarget target;

	public OpenShiftInitializationWizard(IZendTarget target) {
		super();
		this.target = target;
		this.data = new OpenShiftTargetData();
		this.osPage = new OpenShiftTargetPage(data, target.getHost().getHost(),
				true);
		this.eulaPage = new OpenShiftEulaPage(data, true);
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
		setWindowTitle(Messages.OpenShiftInitializationWizard_WizardTitle);
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
					monitor.beginTask(
							Messages.OpenShiftInitializationWizard_JobTitle,
							IProgressMonitor.UNKNOWN);
					if (Type.create(data.getGearProfile()) == Type.UNKNOWN) {
						String val = target
								.getProperty(OpenShiftTarget.GEAR_PROFILE);
						if (val != null) {
							data.setGearProfile(val);
						}
					}
					OpenShiftTargetInitializer initializer = new OpenShiftTargetInitializer(
							getName(), getDomain(), OpenShiftTarget
									.getLibraDomain(), data.getPassword(), data
									.getConfirmPassword(), data
									.getGearProfile());
					IStatus status = initializer.initialize();
					if (status.getSeverity() == IStatus.ERROR) {
						throw new InvocationTargetException(new Exception(
								status.getMessage()));
					}
					if (Boolean.valueOf(target
							.getProperty(OpenShiftTarget.BOOTSTRAP))) {
						try {
							OpenShiftTarget osTarget = new OpenShiftTarget(
									null, null, new EclipseApiKeyDetector(
											"admin", data.getPassword())); //$NON-NLS-1$
							osTarget.setupWebApiKeyZend6(target);
						} catch (SdkException e) {
							osPage.setErrorMessage(e.getMessage());
						}
					}
					monitor.done();
				}
			});
		} catch (InvocationTargetException e) {
			Activator.log(e);
			Throwable a = e.getTargetException();
			if (a != null) {
				osPage.setErrorMessage(OpenShiftTarget.getOpenShiftMessage(a));
				return false;
			}
		} catch (InterruptedException e) {
			Activator.log(e);
		}
		return true;
	}

	private String getDomain() {
		String host = target.getHost().getHost();
		int index = host.indexOf('-');
		host = host.substring(index + 1);
		index = host.indexOf('.');
		return host.substring(0, index);
	}

	private String getName() {
		String host = target.getHost().getHost();
		int index = host.indexOf('-');
		return host.substring(0, index);
	}

}
