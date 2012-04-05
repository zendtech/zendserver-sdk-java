/*******************************************************************************
 * Copyright (c) 2012 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.php.zendserver.deployment.core.targets;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.zend.sdklib.internal.target.ZendDevCloud;
import org.zend.sdklib.target.IZendTarget;

/**
 * Phpcloud account password dialog.
 * 
 * @author Wojciech Galanciak, 2012
 * 
 */
public class PhpcloudPasswordDialog extends Dialog {

	private Text passwordText;
	private Button saveButton;

	private String username;
	private String password;
	private boolean save;

	private IZendTarget target;

	public PhpcloudPasswordDialog(Shell parentShell, IZendTarget target) {
		super(parentShell);
		this.target = target;
		this.username = target.getProperty(ZendDevCloud.TARGET_USERNAME);
	}

	public String getPassword() {
		return password;
	}

	public boolean getSave() {
		return save;
	}

	protected Control createDialogArea(Composite parent) {
		Composite comp = (Composite) super.createDialogArea(parent);
		GridLayout layout = (GridLayout) comp.getLayout();
		layout.numColumns = 2;
		Label usernameLabel = new Label(comp, SWT.RIGHT);
		usernameLabel.setText("Username: "); //$NON-NLS-1$
		Text usernameText = new Text(comp, SWT.SINGLE | SWT.BORDER
				| SWT.READ_ONLY);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		usernameText.setLayoutData(gd);
		usernameText.setText(username);
		Label passwordLabel = new Label(comp, SWT.RIGHT);
		passwordLabel.setText("Password: "); //$NON-NLS-1$
		passwordText = new Text(comp, SWT.SINGLE | SWT.PASSWORD | SWT.BORDER);
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		passwordText.setLayoutData(data);
		saveButton = new Button(comp, SWT.CHECK);
		saveButton.setText("Save Password"); //$NON-NLS-1$
		getShell().setText("Phpcloud Account Password"); //$NON-NLS-1$
		return comp;
	}

	protected void createButtonsForButtonBar(Composite parent) {
		super.createButtonsForButtonBar(parent);
	}

	@Override
	protected void okPressed() {
		password = passwordText.getText();
		save = saveButton.getSelection();
		if (password != null && save) {
			ZendDevCloud cloud = new ZendDevCloud();
			cloud.setPassword(target, password);
		}
		super.okPressed();
	}

}