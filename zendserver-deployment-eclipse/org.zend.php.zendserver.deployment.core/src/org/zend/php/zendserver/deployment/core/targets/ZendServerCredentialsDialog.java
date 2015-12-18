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
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * Zend Server credentials dialog for local target detection.
 * 
 * @author Wojciech Galanciak, 2012
 * 
 */
public class ZendServerCredentialsDialog extends Dialog {

	private Text usernameText;
	private Text passwordText;

	private String title;
	private String password;
	private String username;
	private String message;

	public ZendServerCredentialsDialog(Shell parentShell, String title,
			String message) {
		super(parentShell);
		this.title = title;
		this.message = message;
	}

	public ZendServerCredentialsDialog(Shell parentShell, String title) {
		this(parentShell, title, null);
	}

	public String getPassword() {
		return password;
	}

	public String getUsername() {
		return username;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.dialogs.Dialog#okPressed()
	 */
	protected void okPressed() {
		password = passwordText.getText();
		username = usernameText.getText();
		super.okPressed();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets
	 * .Composite)
	 */
	protected Control createDialogArea(Composite parent) {
		Composite comp = (Composite) super.createDialogArea(parent);
		GridData gd = (GridData) comp.getLayoutData();
		gd.widthHint = 250;
		GridLayout layout = (GridLayout) comp.getLayout();
		layout.numColumns = 2;
		if (message != null) {
			Label messageLabel = new Label(comp, SWT.NONE);
			messageLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.FILL, false,
					false));
			messageLabel.setText(message);
			messageLabel.setForeground(Display.getDefault().getSystemColor(
					SWT.COLOR_RED));
			messageLabel.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true,
					false, 2, 1));
		}
		Label usernameLabel = new Label(comp, SWT.RIGHT);
		usernameLabel.setText(Messages.ZendServerCredentialsDialog_0);
		usernameLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.FILL, false,
				false));
		usernameText = new Text(comp, SWT.SINGLE | SWT.BORDER);
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		usernameText.setLayoutData(data);
		usernameText.setText("admin"); //$NON-NLS-1$
		Label passwordLabel = new Label(comp, SWT.RIGHT);
		passwordLabel.setText(Messages.ZendServerCredentialsDialog_1);
		passwordLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.FILL, false,
				false));
		passwordText = new Text(comp, SWT.SINGLE | SWT.PASSWORD | SWT.BORDER);
		passwordText.setLayoutData(data);
		passwordText.forceFocus();
		getShell().setText(title);
		return comp;
	}

}