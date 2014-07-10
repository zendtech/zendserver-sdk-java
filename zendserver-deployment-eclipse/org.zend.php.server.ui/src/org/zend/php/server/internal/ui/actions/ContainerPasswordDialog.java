/*******************************************************************************
 * Copyright (c) 2014 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.php.server.internal.ui.actions;

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
import org.zend.php.server.internal.ui.Messages;

/**
 * Container password dialog for database connection.
 * 
 * @author Wojciech Galanciak, 2014
 * 
 */
public class ContainerPasswordDialog extends Dialog {

	private Text passwordText;
	private Button saveButton;
	private Text usernameText;

	private String title;
	private boolean save;
	private boolean askUsername;

	private String password;
	private String username;

	public ContainerPasswordDialog(Shell parentShell, String title,
			boolean askUsername) {
		super(parentShell);
		this.title = title;
		this.askUsername = askUsername;
	}

	public String getPassword() {
		return password;
	}

	public String getUsername() {
		return username;
	}

	public boolean getSave() {
		return save;
	}

	protected Control createDialogArea(Composite parent) {
		Composite comp = (Composite) super.createDialogArea(parent);
		GridLayout layout = (GridLayout) comp.getLayout();
		GridData gd = (GridData) comp.getLayoutData();
		gd.widthHint = 250;
		layout.numColumns = 2;
		if (askUsername) {
			Label usernameLabel = new Label(comp, SWT.RIGHT);
			usernameLabel
					.setText(Messages.ContainerPasswordDialog_UsernameLabel);
			usernameText = new Text(comp, SWT.SINGLE | SWT.BORDER);
			GridData data = new GridData(GridData.FILL_HORIZONTAL);
			usernameText.setLayoutData(data);
		}
		Label passwordLabel = new Label(comp, SWT.RIGHT);
		passwordLabel.setText(Messages.ContainerPasswordDialog_PasswordLabel);
		passwordText = new Text(comp, SWT.SINGLE | SWT.PASSWORD | SWT.BORDER);
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		passwordText.setLayoutData(data);
		saveButton = new Button(comp, SWT.CHECK);
		saveButton.setText(Messages.ContainerPasswordDialog_SaveLabel);
		getShell().setText(title);
		return comp;
	}

	protected void createButtonsForButtonBar(Composite parent) {
		super.createButtonsForButtonBar(parent);
	}

	@Override
	protected void okPressed() {
		password = passwordText.getText();
		if (askUsername) {
			username = usernameText.getText();
		}
		save = saveButton.getSelection();
		super.okPressed();
	}

}