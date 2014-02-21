/*******************************************************************************
 * Copyright (c) 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.php.zendserver.deployment.ui.wizards;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;
import org.zend.php.zendserver.deployment.ui.Activator;
import org.zend.sdklib.SdkException;
import org.zend.sdklib.internal.target.OpenShiftTarget;

/**
 * OpenShift target creation wizard dialog. It customize switching between pages
 * when domian has not been created yet.
 * 
 * @author Wojciech Galanciak, 2012
 * 
 */
public class OpenShiftTargetWizardDialog extends WizardDialog {

	private OpenShiftTargetData data;
	private boolean init;

	public OpenShiftTargetWizardDialog(Shell parentShell, IWizard newWizard,
			OpenShiftTargetData data) {
		super(parentShell, newWizard);
		this.data = data;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.wizard.WizardDialog#nextPressed()
	 */
	protected void nextPressed() {
		final IWizardPage page = getWizard().getStartingPage();
		if (!init && page instanceof OpenShiftDomainPage) {
			final OpenShiftDomainPage domainPage = (OpenShiftDomainPage) page;
			final String domainName = domainPage.getDomainName();
			final OpenShiftTarget target = domainPage.getTarget();
			try {
				run(true, false, new IRunnableWithProgress() {

					public void run(IProgressMonitor monitor)
							throws InvocationTargetException,
							InterruptedException {
						try {
							monitor.beginTask(
									Messages.OpenShiftTargetWizardDialog_CreateDomainJobTitle,
									IProgressMonitor.UNKNOWN);
							target.createDomain(domainName);
							init = true;
							final List<String> gearProfiles = new ArrayList<String>();
							final List<String> zendTargets = new ArrayList<String>();
							gearProfiles.addAll(target
									.getAvaliableGearProfiles());
							zendTargets.addAll(target.getAllZendTargets());
							IWizardPage page = getWizard().getNextPage(
									domainPage);
							if (page instanceof OpenShiftTargetPage) {
								OpenShiftTargetPage osPage = (OpenShiftTargetPage) page;
								data.setZendTargets(zendTargets);
								data.setGearProfiles(gearProfiles);
								osPage.initializeValues();
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
					domainPage.setErrorMessage(OpenShiftTarget
							.getOpenShiftMessage(a));
					return;
				}
			} catch (InterruptedException e) {
				Activator.log(e);
			}
		}
		super.nextPressed();
	}
}
