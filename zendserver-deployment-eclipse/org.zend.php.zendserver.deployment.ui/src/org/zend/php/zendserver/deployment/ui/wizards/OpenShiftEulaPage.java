/*******************************************************************************
 * Copyright (c) 2012 Zend Technologies Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Zend Technologies Ltd. - initial API and implementation
 *******************************************************************************/
package org.zend.php.zendserver.deployment.ui.wizards;

import java.io.IOException;
import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.events.HelpEvent;
import org.eclipse.swt.events.HelpListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.internal.help.WorkbenchHelpSystem;
import org.zend.php.zendserver.deployment.ui.Activator;
import org.zend.php.zendserver.deployment.ui.HelpContextIds;

/**
 * New OpenShift Target wizard page which contains EULA document.
 * 
 * @author Wojciech Galanciak, 2012
 * 
 */
public class OpenShiftEulaPage extends WizardPage {

	private static final String TERMS_FILE = "resources/openshift_eula.html"; //$NON-NLS-1$

	private Button acceptTermOfUse;

	private OpenShiftTargetData data;

	protected OpenShiftEulaPage(OpenShiftTargetData data) {
		super(Messages.OpenShiftTargetPage_PageTitle);
		setTitle(Messages.OpenShiftTargetPage_PageTitle);
		setDescription(Messages.OpenShiftEulaPage_PageDescription);
		this.data = data;
	}
	
	protected OpenShiftEulaPage(OpenShiftTargetData data, boolean init) {
		super(Messages.OpenShiftInitializationWizard_WizardTitle);
		setTitle(Messages.OpenShiftInitializationWizard_WizardTitle);
		setDescription(Messages.OpenShiftEulaPage_PageDescription);
		this.data = data;
	}

	public void updateData() {
		data.setEula(acceptTermOfUse.getSelection());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets
	 * .Composite)
	 */
	public void createControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(1, false));
		Label description = new Label(composite, SWT.NONE);
		description.setText(Messages.OpenShiftEulaPage_EulaDescription);
		description.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		Browser browser = new Browser(composite, SWT.BORDER);
		GridData layoutData = new GridData(SWT.FILL, SWT.FILL, true, true);
		browser.setLayoutData(layoutData);
		browser.setUrl(getTermsOfUseUrl());

		acceptTermOfUse = new Button(composite, SWT.CHECK);
		acceptTermOfUse.setText(Messages.OpenShiftEulaPage_AgreeLabel);
		acceptTermOfUse.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true,
				false));
		acceptTermOfUse.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				setPageComplete(acceptTermOfUse.getSelection());
			}
		});
		parent.setData(WorkbenchHelpSystem.HELP_KEY,
				HelpContextIds.ADDING_A_SERVER_OPENSHFIT_SERVER);
		parent.addHelpListener(new HelpListener() {
			public void helpRequested(HelpEvent event) {
				Program.launch(HelpContextIds.ADDING_A_SERVER_OPENSHFIT_SERVER);
			}
		});
		setControl(composite);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.wizard.WizardPage#isPageComplete()
	 */
	public boolean isPageComplete() {
		return acceptTermOfUse.getSelection();
	}

	private String getTermsOfUseUrl() {
		URL terms = FileLocator.find(Activator.getDefault().getBundle(),
				new Path(TERMS_FILE), null);
		try {
			return FileLocator.toFileURL(terms).toString();
		} catch (IOException e) {
			Activator.log(e);
		}
		return null;
	}

}
