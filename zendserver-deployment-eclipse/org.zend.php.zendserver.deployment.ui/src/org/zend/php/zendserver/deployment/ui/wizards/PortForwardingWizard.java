/*******************************************************************************
 * Copyright (c) 2014 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.php.zendserver.deployment.ui.wizards;

import org.eclipse.jface.wizard.Wizard;
import org.zend.php.zendserver.deployment.core.tunnel.PortForwarding;

/**
 * Wizard for creating and editing port forwarding entry.
 * 
 * @author Wojciech Galanciak, 2014
 * 
 */
public class PortForwardingWizard extends Wizard {

	private PortForwardingPage page;
	private PortForwarding portForwarding;

	public PortForwardingWizard() {
		this(null);
	}

	public PortForwardingWizard(PortForwarding portForwarding) {
		setWindowTitle(portForwarding != null ? Messages.PortForwardingWizard_EditTitle
				: Messages.PortForwardingWizard_CreateTitle);
		this.portForwarding = portForwarding;
	}

	@Override
	public void addPages() {
		super.addPages();
		page = new PortForwardingPage(portForwarding);
		addPage(page);
	}

	@Override
	public boolean performFinish() {
		portForwarding = page.getPortForwarding();
		return true;
	}

	public PortForwarding getResult() {
		return portForwarding;
	}

}
