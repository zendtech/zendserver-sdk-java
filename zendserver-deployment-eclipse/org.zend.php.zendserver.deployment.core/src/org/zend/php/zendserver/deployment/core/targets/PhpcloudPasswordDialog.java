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
import org.zend.php.zendserver.deployment.core.Messages;
import org.zend.sdklib.internal.target.ZendDevCloud;
import org.zend.sdklib.internal.target.ZendTarget;
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
		GridData gd = (GridData) comp.getLayoutData();
		GridLayout layout = (GridLayout) comp.getLayout();
		layout.numColumns = 2;
		layout.marginWidth = 20;
		layout.marginHeight = 20;
		Label usernameLabel = new Label(comp, SWT.RIGHT);
		usernameLabel.setText(Messages.PhpcloudPasswordDialog_Username);
		Text usernameText = new Text(comp, SWT.SINGLE | SWT.BORDER
				| SWT.READ_ONLY);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		usernameText.setLayoutData(gd);
		usernameText.setText(username);
		usernameText.setEnabled(false);
		Label passwordLabel = new Label(comp, SWT.RIGHT);
		passwordLabel.setText(Messages.PhpcloudPasswordDialog_Password);
		passwordText = new Text(comp, SWT.SINGLE | SWT.PASSWORD | SWT.BORDER);
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		passwordText.setLayoutData(data);
		saveButton = new Button(comp, SWT.CHECK);
		saveButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 2, 1));
		saveButton.setText(Messages.PhpcloudPasswordDialog_SavePassword);
		getShell().setText(Messages.PhpcloudPasswordDialog_Title);
		return comp;
	}

	protected void createButtonsForButtonBar(Composite parent) {
		super.createButtonsForButtonBar(parent);
	}

	@Override
	protected void okPressed() {
		password = passwordText.getText();
		save = saveButton.getSelection();
		if (password != null) {
			if (save) {
			TargetsManagerService.INSTANCE.storePhpcloudPassword(target,
					password);
			}
			((ZendTarget) target).addProperty(ZendDevCloud.TARGET_PASSWORD,
					password);
		}
		super.okPressed();
	}

}