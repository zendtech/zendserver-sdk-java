/*******************************************************************************
 * Copyright (c) 2014 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.php.zendserver.deployment.ui.wizards;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.zend.php.zendserver.deployment.core.tunnel.PortForwarding;
import org.zend.php.zendserver.deployment.core.tunnel.PortForwarding.Side;
import org.zend.php.zendserver.deployment.ui.Activator;

/**
 * Wizard page for creating and editing port forwarding entry.
 * 
 * @author Wojciech Galanciak, 2014
 * 
 */
public class PortForwardingPage extends WizardPage {

	private ModifyListener modifyListener = new ModifyListener() {

		public void modifyText(ModifyEvent e) {
			setPageComplete(validatePage());
			updateCommandLabel();
		}
	};

	private Combo sideCombo;
	private Text localAddressText;
	private Text localPortText;
	private Text remoteAddressText;
	private Text remotePortText;

	private PortForwarding portForwarding;

	private Label commandLabel;

	public PortForwardingPage(PortForwarding portForwarding) {
		super("Port Forwarding Page"); //$NON-NLS-1$
		this.portForwarding = portForwarding;
		setImageDescriptor(Activator
				.getImageDescriptor(Activator.IMAGE_PORT_FORWARDING_WIZ));
		setTitle(portForwarding != null ? Messages.PortForwardingPage_EditTitle
				: Messages.PortForwardingPage_CreateTitle);
		setDescription(portForwarding != null ? Messages.PortForwardingPage_EditDesc
				: Messages.PortForwardingPage_CreateDesc);
	}

	public PortForwarding getPortForwarding() {
		switch (Side.byName(sideCombo.getText())) {
		case LOCAL:
			String localAddress = localAddressText.getText();
			if (localAddress.isEmpty()) {
				return PortForwarding.createLocal(
						Integer.valueOf(localPortText.getText()),
						remoteAddressText.getText(),
						Integer.valueOf(remotePortText.getText()));
			}
			return PortForwarding.createLocal(localAddressText.getText(),
					Integer.valueOf(localPortText.getText()),
					remoteAddressText.getText(),
					Integer.valueOf(remotePortText.getText()));
		case REMOTE:
			String remoteAddress = remoteAddressText.getText();
			if (remoteAddress.isEmpty()) {
				return PortForwarding.createRemote(
						Integer.valueOf(remotePortText.getText()),
						localAddressText.getText(),
						Integer.valueOf(localPortText.getText()));
			}
			return PortForwarding.createRemote(remoteAddressText.getText(),
					Integer.valueOf(remotePortText.getText()),
					localAddressText.getText(),
					Integer.valueOf(localPortText.getText()));
		}
		return portForwarding;
	}

	public void createControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		composite.setLayout(new GridLayout(2, false));
		Label label = new Label(composite, SWT.NONE);
		label.setText(Messages.PortForwardingPage_SideLabel);
		sideCombo = new Combo(composite, SWT.READ_ONLY);
		sideCombo.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				setPageComplete(validatePage());
				if (isPageComplete()) {
					updateCommandLabel();
				}
			}
		});
		localAddressText = createText(
				Messages.PortForwardingPage_LocalAddressLabel, composite);
		localPortText = createText(Messages.PortForwardingPage_LocalPortLabel,
				composite);
		remoteAddressText = createText(
				Messages.PortForwardingPage_RemoteAddressLabel, composite);
		remotePortText = createText(
				Messages.PortForwardingPage_RemotePortLabel, composite);
		label = new Label(composite, SWT.NONE);
		label = new Label(composite, SWT.NONE);
		label.setText(Messages.PortForwardingPage_ResultLabel);
		label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2,
				1));
		commandLabel = new Label(composite, SWT.NONE);
		commandLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 2, 1));
		commandLabel.setText(Messages.PortForwardingPage_ProvideMessage);
		setControl(composite);
		init();
	}

	private boolean validatePage() {
		setMessage(null);
		setErrorMessage(null);
		Side side = Side.byName(sideCombo.getText());
		if (localAddressText.getText().isEmpty() && side == Side.REMOTE) {
			setMessage(Messages.PortForwardingPage_SpecifyLocalAddressMessage);
			return false;
		}
		if (localPortText.getText().isEmpty()) {
			setMessage(Messages.PortForwardingPage_SpecifyLocalPortMessage);
			return false;
		} else {
			try {
				Integer.valueOf(localPortText.getText());
			} catch (NumberFormatException e) {
				setErrorMessage(Messages.PortForwardingPage_InvalidLocalPortError);
				return false;
			}
		}
		if (remoteAddressText.getText().isEmpty() && side == Side.LOCAL) {
			setMessage(Messages.PortForwardingPage_SpecifyRemoteAddressMessage);
			return false;
		}

		if (remotePortText.getText().isEmpty()) {
			setMessage(Messages.PortForwardingPage_SpecifyRemotePortMessage);
			return false;
		} else {
			try {
				Integer.valueOf(remotePortText.getText());
			} catch (NumberFormatException e) {
				setErrorMessage(Messages.PortForwardingPage_InvalidRemotePortError);
				return false;
			}
		}
		return true;
	}

	private void init() {
		Side[] sides = Side.values();
		for (Side side : sides) {
			sideCombo.add(side.getName());
		}
		sideCombo.select(0);
		if (portForwarding != null) {
			sideCombo.select(sideCombo.indexOf(portForwarding.getSide()
					.getName()));
			String localAddress = portForwarding.getLocalAddress();
			if (localAddress != null) {
				localAddressText.setText(localAddress);
			}
			localPortText
					.setText(String.valueOf(portForwarding.getLocalPort()));
			String remoteAddress = portForwarding.getRemoteAddress();
			if (remoteAddress != null) {
				remoteAddressText.setText(remoteAddress);
			}
			remotePortText.setText(String.valueOf(portForwarding
					.getRemotePort()));
		}
	}

	private void updateCommandLabel() {
		if (isPageComplete()) {
			PortForwarding forwarding = getPortForwarding();
			if (forwarding != null) {
				commandLabel.setText("ssh " + forwarding.serialize()); //$NON-NLS-1$
				return;
			}
		}
		commandLabel.setText(Messages.PortForwardingPage_ProvideMessage);
	}

	private Text createText(String name, Composite parent) {
		Label label = new Label(parent, SWT.NONE);
		label.setText(name);
		Text text = new Text(parent, SWT.BORDER);
		text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		text.addModifyListener(modifyListener);
		return text;
	}

}
