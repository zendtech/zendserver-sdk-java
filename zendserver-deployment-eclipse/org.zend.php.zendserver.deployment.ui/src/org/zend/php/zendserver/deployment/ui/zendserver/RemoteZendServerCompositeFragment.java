/*******************************************************************************
 * Copyright (c) 2015 Zend Technologies.
 * All rights reserved. This program and the accompanying materials
 * are the copyright of Zend Technologies and is protected under
 * copyright laws of the United States.
 * You must not copy, adapt or redistribute this document for 
 * any use.
 *******************************************************************************/
package org.zend.php.zendserver.deployment.ui.zendserver;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.MessageFormat;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.php.internal.server.core.Server;
import org.eclipse.php.internal.ui.wizards.IControlHandler;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.zend.php.server.ui.ServersUI;
import org.zend.php.server.ui.fragments.AbstractCompositeFragment;
import org.zend.php.server.ui.types.ZendServerType;
import org.zend.php.zendserver.deployment.ui.Activator;

/**
 * @author Bartlomiej Laczkowski, 2015
 * 
 * Wizard page to set the server install directory.
 */
@SuppressWarnings("restriction")
public class RemoteZendServerCompositeFragment extends
		AbstractCompositeFragment {

	protected Text nameText;
	protected Text urlText;

	protected String name;
	protected String url;
	
	/**
	 * ServerCompositeFragment
	 * 
	 * @param parent
	 *            the parent composite
	 * @param wizard
	 *            the wizard handle
	 */
	public RemoteZendServerCompositeFragment(Composite parent,
			IControlHandler handler, boolean isForEditing) {
		super(parent, handler, isForEditing, Messages.RemoteZendServerCompositeFragment_Name,
				Messages.RemoteZendServerCompositeFragment_Title,
				Messages.RemoteZendServerCompositeFragment_Description);
		setImageDescriptor(ServersUI
				.getImageDescriptor(ServersUI.ZEND_SERVER_WIZ));
		handler.setImageDescriptor(getImageDescriptor());
	}

	/* (non-Javadoc)
	 * @see org.eclipse.php.internal.ui.wizards.CompositeFragment#performOk()
	 */
	public boolean performOk() {
		saveValues();
		return true;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.php.internal.ui.wizards.CompositeFragment#validate()
	 */
	public void validate() {
		
		if (name == null || name.trim().equals("")) { //$NON-NLS-1$
			setIncompleteMessage(Messages.RemoteZendServerCompositeFragment_Missing_server_name);
			return;
		}
		
		if(isDuplicateName(name)) {
			setMessage(Messages.RemoteZendServerCompositeFragment_Duplicated_server_name, IMessageProvider.ERROR);
			return;
		}

		if (url == null || url.trim().equals("")) { //$NON-NLS-1$
			setMessage(Messages.RemoteZendServerCompositeFragment_Missing_server_URL, IMessageProvider.ERROR);
			return;
		}
		
		URL serverUrl = null;
		try {
			serverUrl = new URL(url);
		} catch (MalformedURLException e) {
			setMessage(Messages.RemoteZendServerCompositeFragment_Please_enter_valid_URL, IMessageProvider.ERROR);
			return;
		}
		if(serverUrl.getHost().isEmpty()) {
			setMessage(Messages.RemoteZendServerCompositeFragment_Please_enter_valid_URL, IMessageProvider.ERROR);
			return;						
		}
		if(serverUrl.getPath().length() != 0){
			setMessage(Messages.RemoteZendServerCompositeFragment_Please_enter_valid_URL, IMessageProvider.ERROR);
			return;			
		}
		
		if(checkServerUrl(url)) {
			setMessage(Messages.RemoteZendServerCompositeFragment_Duplicated_server_URL, IMessageProvider.ERROR);
			return;
		}
		
		setMessage(getDescription(), IMessageProvider.NONE);
	}

	/**
	 * Provide a wizard page to change the Server's installation directory.
	 */
	protected void createContents(Composite parent) {
		Composite nameGroup = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		nameGroup.setLayout(layout);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan = 3;
		nameGroup.setLayoutData(gridData);
		Label label = new Label(nameGroup, SWT.NONE);
		label.setText(Messages.RemoteZendServerCompositeFragment_Server_name);
		GridData data = new GridData();
		label.setLayoutData(data);
		nameText = new Text(nameGroup, SWT.BORDER);
		data = new GridData(GridData.FILL_HORIZONTAL);
		nameText.setLayoutData(data);
		nameText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				name = nameText.getText();
				validate();
			}
		});
		nameText.forceFocus();
		
		createURLGroup(parent);
	}

	/**
	 * @param parent
	 */
	protected void createURLGroup(Composite parent) {
		// Main composite
		Composite urlGroupComposite = new Composite(parent, SWT.NONE);
		GridLayout sLayout = new GridLayout();
		urlGroupComposite.setLayout(sLayout);
		GridData sGridData = new GridData(SWT.FILL, SWT.FILL, true, true);
		sGridData.horizontalSpan = 3;
		urlGroupComposite.setLayoutData(sGridData);
		Group group = new Group(urlGroupComposite, SWT.NONE);
		group.setFont(urlGroupComposite.getFont());
		GridLayout layout = new GridLayout(2, false);
		group.setLayout(layout);
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		group.setLayoutData(data);
		group.setText(Messages.RemoteZendServerCompositeFragment_Server_properties);
		Label urlLabel = new Label(group, SWT.None);
		urlLabel.setText(Messages.RemoteZendServerCompositeFragment_Base_URL);
		urlText = new Text(group, SWT.BORDER);
		GridData layoutData = new GridData(GridData.FILL_HORIZONTAL);
		urlText.setLayoutData(layoutData);
		urlText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				url = urlText.getText();
				validate();
			}
		});
	
	}

	/* (non-Javadoc)
	 * @see org.zend.php.server.ui.fragments.AbstractCompositeFragment#init()
	 */
	protected void init() {
		Server server = getServer();
		if (server != null) {
			nameText.setText(server.getName());
			urlText.setText(server.getBaseURL());
		} else {
			urlText.setText(ZendServerType.DEFAULT_BASE_URL);
		}
		validate();
	}

	protected void saveValues() {
		Server server = getServer();
		server.setName(name);
		try {
			server.setBaseURL(url);
			URL serverURL = new URL(url);
			server.setHost(serverURL.getHost());
			server.setPort(String.valueOf(serverURL.getPort()));
		} catch (MalformedURLException e) {
			// should not occur at this time
			// if it does it means that validation does not work well
			String message = MessageFormat.format(Messages.RemoteZendServerCompositeFragment_Invalid_server_URL, url);
			Activator.logError(message);
			return;
		}
	}

	private boolean checkServerUrl(String url) {
		Server tempServer = new Server();
		try {
			tempServer.setBaseURL(url);
		} catch (MalformedURLException e) {
			// should not occur
			String message = MessageFormat.format(Messages.RemoteZendServerCompositeFragment_Invalid_server_URL, url);
			Activator.logError(message);
		}
		return (getConflictingServer(tempServer) != null);
	}

}
