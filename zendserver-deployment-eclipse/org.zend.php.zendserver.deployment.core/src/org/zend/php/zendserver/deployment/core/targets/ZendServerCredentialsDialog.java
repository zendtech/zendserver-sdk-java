/*******************************************************************************
 * Copyright (c) 2012 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.php.zendserver.deployment.core.targets;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.zend.sdklib.manager.TargetsManager;

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
	private String name;

	public ZendServerCredentialsDialog(Shell parentShell, String title,
			String message, String name) {
		super(parentShell);
		this.title = title;
		this.message = message;
		this.name = name;
	}

	public ZendServerCredentialsDialog(Shell parentShell, String title,
			String message) {
		this(parentShell, title, null, null);
	}

	public ZendServerCredentialsDialog(Shell parentShell, String title) {
		this(parentShell, title, null, null);
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
		gd.widthHint = 350;
		GridLayout layout = (GridLayout) comp.getLayout();
		layout.numColumns = 2;
		
		Composite infoComposite = new Composite(comp, SWT.NONE);
		GridLayoutFactory.fillDefaults().numColumns(2).equalWidth(false).margins(0, 0).applyTo(infoComposite);
		GridDataFactory.fillDefaults().span(2, 1).grab(true, false).align(SWT.FILL, SWT.TOP).applyTo(infoComposite);
		
		Label infoLabel = new Label(infoComposite, SWT.WRAP);
		infoLabel.setText(Messages.ZendServerCredentialsDialog_Info1_Text);
		GridDataFactory.fillDefaults().span(2, 1).grab(true, false).align(SWT.FILL, SWT.TOP).applyTo(infoLabel);
		Label infoIcon = new Label(infoComposite, SWT.NONE);
		infoIcon.setImage(JFaceResources.getImage(Dialog.DLG_IMG_MESSAGE_INFO));
		GridDataFactory.fillDefaults().grab(false, false).align(SWT.LEFT, SWT.TOP).applyTo(infoIcon);
		Link infoLink = new Link(infoComposite, SWT.WRAP);
		infoLink.setText(Messages.ZendServerCredentialsDialog_Info2_Text);
		GridDataFactory.fillDefaults().grab(true, false).align(SWT.FILL, SWT.TOP).applyTo(infoLink);
		infoLink.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				org.eclipse.swt.program.Program.launch("http://files.zend.com/help/Zend-Server/content/web_api_reference_guide.htm"); //$NON-NLS-1$
			}
		});
		
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
		if (name != null && TargetsManager.isOpenShift(name)) {
			int index = name.lastIndexOf('-');
			if (index != -1) {
				name = name.substring(0, index);
				index = name.lastIndexOf('/');
				if (index != -1) {
					name = name.substring(index + 1);
				}
			}
			Label appLabel = new Label(comp, SWT.RIGHT);
			appLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.FILL, false,
					false));
			appLabel.setText(Messages.ZendServerCredentialsDialog_Application);
			Label appName = new Label(comp, SWT.RIGHT);
			appName.setText(name);
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