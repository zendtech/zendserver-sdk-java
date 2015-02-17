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
package org.zend.php.zendserver.deployment.ui.openshift;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.osgi.service.prefs.BackingStoreException;
import org.zend.php.zendserver.deployment.core.DeploymentCore;
import org.zend.php.zendserver.deployment.ui.Activator;
import org.zend.sdklib.internal.target.OpenShiftTarget;

/**
 * OpenShift preferences.
 * 
 * @author Wojciech Galanciak, 2012
 * 
 */
public class OpenShiftPreferencesPage extends PreferencePage implements
		IWorkbenchPreferencePage {

	private Text serverURL;
	private Text domain;

	private IEclipsePreferences prefs;

	public OpenShiftPreferencesPage() {
		this.prefs = InstanceScope.INSTANCE.getNode(DeploymentCore.PLUGIN_ID);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.preference.PreferencePage#performOk()
	 */
	public boolean performOk() {
		String serverOldValue = prefs.get(OpenShiftTarget.LIBRA_SERVER_PROP, (String) null);
		String serverNewValue = serverURL.getText();
		String domainOldValue = prefs.get(OpenShiftTarget.LIBRA_DOMAIN_PROP, (String) null);
		String domainNewValue = domain.getText();
		if (!serverNewValue.equals(serverOldValue)) {
			prefs.put(OpenShiftTarget.LIBRA_SERVER_PROP, serverNewValue);
			OpenShiftTarget.setLibraServer(serverNewValue);
		}
		if (!domainNewValue.equals(domainOldValue)) {
			prefs.put(OpenShiftTarget.LIBRA_DOMAIN_PROP, domainNewValue);
			OpenShiftTarget.setLibraDomain(domainNewValue);
		}
		
		try {
			prefs.flush();
		} catch (BackingStoreException e) {
			Activator.log(e);
		}
		return super.performOk();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.preference.PreferencePage#performDefaults()
	 */
	protected void performDefaults() {
		serverURL.setText(OpenShiftTarget.getDefaultLibraServer());
		domain.setText(OpenShiftTarget.getDefaultLibraDomain());
		super.performDefaults();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.preference.PreferencePage#createContents(org.eclipse
	 * .swt.widgets.Composite)
	 */
	@Override
	protected Control createContents(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		composite.setLayout(new GridLayout(1, false));

		createServerURL(composite);

		initializeValues();

		return composite;
	}

	private void createServerURL(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		composite.setLayout(new GridLayout(2, false));
		Label label = new Label(composite, SWT.NONE);
		label.setText(Messages.OpenShiftPreferencesPage_0);
		label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, true));
		serverURL = new Text(composite, SWT.SINGLE | SWT.BORDER | SWT.READ_ONLY);
		serverURL
				.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		serverURL.setEditable(true);
		Label labelDomain = new Label(composite, SWT.NONE);
		labelDomain.setText(Messages.OpenShiftPreferencesPage_1);
		labelDomain.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, true));
		domain = new Text(composite, SWT.SINGLE | SWT.BORDER | SWT.READ_ONLY);
		domain
				.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		domain.setEditable(true);
	}

	private void initializeValues() {
		String serverValue = prefs.get(OpenShiftTarget.LIBRA_SERVER_PROP, (String) null);
		String defaultServerValue = OpenShiftTarget.getLibraServer();
		if (serverValue == null) {
			serverURL.setText(defaultServerValue);
		} else {
			serverURL.setText(serverValue);
		}
		String domainValue = prefs.get(OpenShiftTarget.LIBRA_DOMAIN_PROP, (String) null);
		String defaultDomainValue = OpenShiftTarget.getLibraDomain();
		if (domainValue == null) {
			domain.setText(defaultDomainValue);
		} else {
			domain.setText(domainValue);
		}
	}

}
