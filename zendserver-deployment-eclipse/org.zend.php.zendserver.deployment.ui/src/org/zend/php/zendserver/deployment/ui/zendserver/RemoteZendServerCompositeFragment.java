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

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.php.internal.server.core.Server;
import org.eclipse.php.internal.server.core.manager.ServersManager;
import org.eclipse.php.internal.ui.wizards.IControlHandler;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.zend.php.server.ui.ServersUI;
import org.zend.php.server.ui.fragments.AbstractCompositeFragment;

/**
 * @author Bartlomiej Laczkowski, 2015
 * 
 * Wizard page to set the server install directory.
 */
@SuppressWarnings("restriction")
public class RemoteZendServerCompositeFragment extends
		AbstractCompositeFragment {

	protected Text name;
	protected Text url;
	protected Combo combo;

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

	/**
	 * Override the super setData to handle only Server types.
	 * 
	 * @throws IllegalArgumentException
	 *             if the given object is not a {@link Server}
	 */
	public void setData(Object server) throws IllegalArgumentException {
		if (server != null && !(server instanceof Server)) {
			throw new IllegalArgumentException(
					"The given object is not a Server"); //$NON-NLS-1$
		}
		super.setData(server);
		init();
		validate();
	}

	/* (non-Javadoc)
	 * @see org.zend.php.server.ui.fragments.AbstractCompositeFragment#getServer()
	 */
	public Server getServer() {
		return (Server) getData();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.php.internal.ui.wizards.CompositeFragment#performOk()
	 */
	public boolean performOk() {
		return true;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.php.internal.ui.wizards.CompositeFragment#validate()
	 */
	public void validate() {
		if (getServer() == null) {
			setMessage("", IMessageProvider.ERROR); //$NON-NLS-1$
			return;
		}
		setMessage(getDescription(), IMessageProvider.NONE);
		String urlStr = url.getText();
		if (urlStr != null && !urlStr.trim().equals("")) { //$NON-NLS-1$
			boolean ok = checkServerUrl(urlStr);
			if (!ok) {
				setMessage(Messages.RemoteZendServerCompositeFragment_Duplicated_server_URL, IMessageProvider.ERROR);
			}
		}
		try {
			URL url = new URL(urlStr);
			if (url.getPath() != null && url.getPath().length() != 0) {
				urlStr = null;
			}
		} catch (MalformedURLException e1) {
			// in case of Malformed URL - reset
			urlStr = null;
		}
		if (urlStr == null || urlStr.equals("")) { //$NON-NLS-1$
			setMessage(
					Messages.RemoteZendServerCompositeFragment_Please_enter_valid_URL,
					IMessageProvider.ERROR);
			return;
		}
		try {
			URL baseURL = new URL(urlStr);
			String host = baseURL.getHost();
			if (host.trim().length() == 0) {
				setMessage(Messages.RemoteZendServerCompositeFragment_URL_is_empty, IMessageProvider.ERROR);
			}
			int port = baseURL.getPort();
	
			getServer().setHost(host);
			getServer().setPort(String.valueOf(port));
		} catch (Exception e) {
			setMessage(
					Messages.RemoteZendServerCompositeFragment_Please_enter_valid_URL,
					IMessageProvider.ERROR);
			return;
		}
		String serverName = getServer().getName();
		if (serverName == null || serverName.trim().equals("")) { //$NON-NLS-1$
			setMessage(Messages.RemoteZendServerCompositeFragment_Missing_server_name, IMessageProvider.ERROR);
		} else {
			boolean ok = checkServerName(serverName);
			if (!ok) {
				setMessage(Messages.RemoteZendServerCompositeFragment_Duplicated_server_name, IMessageProvider.ERROR);
			}
		}
		controlHandler.update();
	}

	/**
	 * Provide a wizard page to change the Server's installation directory.
	 */
	protected void createContents(Composite parent) {
		Composite nameGroup = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(1, true);
		layout = new GridLayout();
		layout.numColumns = 2;
		nameGroup.setLayout(layout);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan = 3;
		nameGroup.setLayoutData(gridData);
		Label label = new Label(nameGroup, SWT.NONE);
		label.setText(Messages.RemoteZendServerCompositeFragment_Server_name);
		GridData data = new GridData();
		label.setLayoutData(data);
		name = new Text(nameGroup, SWT.BORDER);
		data = new GridData(GridData.FILL_HORIZONTAL);
		name.setLayoutData(data);
		name.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				getServer().setName(name.getText());
				validate();
			}
		});
		createURLGroup(parent);
		init();
		validate();
		Dialog.applyDialogFont(parent);
		name.forceFocus();
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
		url = new Text(group, SWT.BORDER);
		GridData layoutData = new GridData(GridData.FILL_HORIZONTAL);
		url.setLayoutData(layoutData);
		url.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				if (getServer() != null) {
					String urlStr = url.getText();
					try {
						getServer().setBaseURL(urlStr);
					} catch (MalformedURLException e1) {
						// ignore
					}
				}
				validate();
			}
		});
	
	}

	/* (non-Javadoc)
	 * @see org.zend.php.server.ui.fragments.AbstractCompositeFragment#init()
	 */
	protected void init() {
		Server server = getServer();
		if (name == null || server == null)
			return;
		if (getServer().getName() != null) {
			boolean nameSet = false;
			String serverName = getServer().getName();
			String orgName = serverName;
			if (!isForEditing()) {
				for (int i = 0; i < 10; i++) {
					boolean ok = checkServerName(serverName);
					if (ok) {
						name.setText(serverName);
						getServer().setName(serverName);
						;
						nameSet = true;
						break;
					}
					serverName = orgName + " (" + Integer.toString(i + 2) + ")"; //$NON-NLS-1$ //$NON-NLS-2$
				}
				if (!nameSet) {
					name.setText(""); //$NON-NLS-1$
					getServer().setName(""); //$NON-NLS-1$
				}
			} else {
				name.setText(serverName);
			}
		} else {
			name.setText(""); //$NON-NLS-1$
		}
		String baseURL = getServer().getBaseURL();
		if (!baseURL.equals("")) { //$NON-NLS-1$
			url.setText(baseURL);
			try {
				URL originalURL = new URL(baseURL);
				int port = originalURL.getPort();
				getServer().setPort(String.valueOf(port));
			} catch (Exception e) {
				setMessage(
						Messages.RemoteZendServerCompositeFragment_Please_enter_valid_URL,
						IMessageProvider.ERROR);
			}
		} else {
			baseURL = "http://" + server.getHost(); //$NON-NLS-1$
			url.setText(baseURL);
			try {
				getServer().setBaseURL(baseURL);
				URL createdURL = new URL(baseURL);
				int port = createdURL.getPort();
				getServer().setPort(String.valueOf(port));
			} catch (Exception e) {
				setMessage(
						Messages.RemoteZendServerCompositeFragment_Please_enter_valid_URL,
						IMessageProvider.ERROR);
			}
		}
	}

	private boolean checkServerName(String name) {
		name = name.trim();
		Server[] allServers = ServersManager.getServers();
		if (allServers != null) {
			int size = allServers.length;
			for (int i = 0; i < size; i++) {
				Server server = allServers[i];
				if (name.equals(server.getName())
						&& !getServer().getUniqueId().equals(
								server.getUniqueId()))
					return false;
			}
		}
		return true;
	}

	private boolean checkServerUrl(String url) {
		url = url.trim();
		Server[] allServers = ServersManager.getServers();
		if (allServers != null) {
			int size = allServers.length;
			for (int i = 0; i < size; i++) {
				Server server = allServers[i];
				if (url.equals(server.getBaseURL())
						&& !getServer().getUniqueId().equals(
								server.getUniqueId()))
					return false;
			}
		}
		return true;
	}

}
