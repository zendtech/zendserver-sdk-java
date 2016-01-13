/*******************************************************************************
 * Copyright (c) 2014 Zend Technologies Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Zend Technologies Ltd. - initial API and implementation
 *******************************************************************************/
package org.zend.php.server.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.php.internal.server.core.Server;
import org.eclipse.php.internal.server.core.manager.ServersManager;
import org.eclipse.php.internal.server.ui.ServerWizard;
import org.eclipse.php.internal.ui.wizards.WizardModel;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.zend.php.server.core.utils.ServerUtils;
import org.zend.php.server.internal.ui.Messages;
import org.zend.sdklib.target.IZendTarget;

/**
 * Component for presenting combo box with list of servers which fulfill
 * specified requirements.
 * 
 * @author Wojciech Galanciak, 2014
 * 
 */
@SuppressWarnings("restriction")
public class ServersCombo {

	/**
	 * Get all servers.
	 */
	public static final IServerFilter ALL_SERVERS_FILTER = new IServerFilter() {
		@Override
		public Server[] filter(Server[] servers) {
			return servers;
		}
	};

	/**
	 * Get all servers with deployment support.
	 */
	public static final IServerFilter DEPLOYMENT_FILTER = new IServerFilter() {
		@Override
		public Server[] filter(Server[] servers) {
			List<Server> result = new ArrayList<Server>();
			for (Server server : servers) {
				if (ServerUtils.getTarget(server) != null) {
					result.add(server);
				}
			}
			return result.toArray(new Server[result.size()]);
		}
	};

	private Button useDefaultServerButton;
	private Combo serversCombo;
	private Button addServerButton;

	private Server[] serversList = new Server[0];

	private String labelText;
	private String tooltip;

	private boolean useDefaultServer;
	private boolean addServer;

	private IAddServerListener listener;
	private IServerFilter filter;

	/**
	 * Create ServersCombo populated by servers which match specified filter.
	 * and Add Server button visible. By default "PHP Servers:" label is used.
	 * To change it call {@link ServersCombo#setLabel(String)}.
	 */
	public ServersCombo(IServerFilter filter, boolean addButton, boolean defaultServerButton) {
		this.filter = filter;
		this.addServer = addButton;
		this.useDefaultServer = defaultServerButton;
		this.labelText = Messages.ServersCombo_DefaultLabel;
	}

	/**
	 * Select server using server's name.
	 * 
	 * @param name
	 *            name of a server which should be selected
	 */
	public void selectByServer(String name) {
		for (int i = 0; i < serversList.length; i++) {
			if (serversList[i].getName().equals(name)) {
				serversCombo.select(i);
				return;
			}
		}
	}

	/**
	 * Select server using id of a target which is associated with particular
	 * server.
	 * 
	 * @param id
	 *            target's id
	 */
	public void selectByTarget(String id) {
		for (int i = 0; i < serversList.length; i++) {
			IZendTarget target = ServerUtils.getTarget(serversList[i]);
			if (target.getId().equals(id)) {
				serversCombo.select(i);
				return;
			}
		}
	}

	/**
	 * @param enabled
	 */
	public void setEnabled(boolean enabled) {
		serversCombo.setEnabled(enabled);
		if (useDefaultServer) {
			useDefaultServerButton.setEnabled(enabled);
		}
		if (addServer) {
			addServerButton.setEnabled(enabled);
		}
	}

	/**
	 * Get selected server.
	 * 
	 * @return {@link Server} instance instance if server name is selected;
	 *         otherwise return <code>null</code>
	 */
	public Server getSelectedServer() {
		if (useDefaultServer && useDefaultServerButton.getSelection())
			// Return workspace default server
			return ServersManager.getDefaultServer(null);
		int idx = serversCombo.getSelectionIndex();
		if (idx <= -1) {
			return null;
		}
		return serversList[idx];
	}

	/**
	 * Get target which is associated with selected server.
	 * 
	 * @return {@link IZendTarget} instance if selected server has associated
	 *         target; otherwise return <code>null</code>
	 */
	public IZendTarget getSelectedTarget() {
		int idx = serversCombo.getSelectionIndex();
		if (idx <= -1) {
			return null;
		}
		return ServerUtils.getTarget(serversList[idx]);
	}

	/**
	 * Gets info if "Default PHP Web Server" check-box is checked
	 * 
	 * @return <code>true</code> if checked, ,<code>false</code> otherwise
	 */
	public boolean isUseDefaultServer() {
		return useDefaultServer && useDefaultServerButton.getSelection();
	}
	
	/**
	 * Update list of servers and populate combo box with current values.
	 */
	public void updateItems() {
		serversList = filterServers(ServersManager.getServers());
		if (serversList.length > 1) {
			Arrays.sort(serversList, new Comparator<Server>() {
				@Override
				public int compare(Server first, Server second) {
					return String.CASE_INSENSITIVE_ORDER.compare(
							first.getName(), second.getName());
				}
			});
		}
		serversCombo.removeAll();
		int toSelect = 0;
		Server workspaceDefaultServer = ServersManager.getDefaultServer(null);
		if (serversList.length != 0) {
			for (int i = 0; i < serversList.length; i++) {
				Server server = serversList[i];
				serversCombo.add(server.getName());
				if (server.equals(workspaceDefaultServer)) {
					toSelect = i;
				}
			}
		}
		if (serversCombo.getItemCount() > 0) {
			serversCombo.select(toSelect);
		}
	}

	/**
	 * @return {@link Combo} instance
	 */
	public Combo getCombo() {
		return serversCombo;
	}

	/**
	 * Create combo with label and optionally Add Server button.
	 * 
	 * @param parent
	 *            parent component
	 */
	public void createControl(Composite parent) {
		if (labelText != null) {
			Label label = new Label(parent, SWT.NONE);
			if (useDefaultServer) {
				label.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, true));
			}
			label.setText(labelText);
		}
		Composite comboContainer = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(2, false);
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		comboContainer.setLayout(layout);
		comboContainer.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		if (useDefaultServer) {
			useDefaultServerButton = new Button(comboContainer, SWT.CHECK);
			GridData udsbGridData = new GridData(SWT.FILL, SWT.FILL, true, true);
			udsbGridData.horizontalSpan = 2;
			useDefaultServerButton.setLayoutData(udsbGridData);
			useDefaultServerButton.setText("Default PHP Web Server");
			useDefaultServerButton.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					boolean useDefault = useDefaultServerButton.getSelection();
					if (useDefault) {
						selectByServer(ServersManager.getDefaultServer(null).getName());
					}
					serversCombo.setEnabled(!useDefault);
					addServerButton.setEnabled(!useDefault);
				}
			});
			useDefaultServerButton.setSelection(true);
		}
		serversCombo = new Combo(comboContainer, SWT.SIMPLE | SWT.DROP_DOWN | SWT.READ_ONLY);
		serversCombo.setToolTipText(tooltip);
		serversCombo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		serversCombo.setEnabled(useDefaultServer ? false : true);
		if (addServer) {
			addServerButton = new Button(comboContainer, SWT.PUSH);
			addServerButton.setText(Messages.ServersCombo_AddLabel);
			addServerButton.setLayoutData(new GridData(SWT.RIGHT, SWT.FILL,
					false, false));
			addServerButton.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					Shell shell = PlatformUI.getWorkbench()
							.getActiveWorkbenchWindow().getShell();
					ServerWizard wizard = new ServerWizard();
					WizardDialog dialog = new WizardDialog(shell, wizard);
					if (dialog.open() == Window.CANCEL) {
						return;
					}
					Server server = (Server) wizard.getRootFragment()
							.getWizardModel().getObject(WizardModel.SERVER);
					ServersManager.addServer(server);
					ServersManager.save();
					updateItems();
					String name = server.getName();
					selectByServer(name);
					if (listener != null) {
						listener.serverAdded(name);
					}
				}
			});
			addServerButton.setEnabled(useDefaultServer ? false : true);
		}
		updateItems();
	}

	/**
	 * Set combo label. If not set then "PHP Server" label is used.
	 * 
	 * @param label
	 *            combo label
	 */
	public void setLabel(String label) {
		this.labelText = label;
	}

	/**
	 * Set combo tooltip.
	 * 
	 * @param tooltip
	 */
	public void setTooltip(String tooltip) {
		this.tooltip = tooltip;
	}

	/**
	 * Set listener of Add Server button selection. It will be called after Add
	 * Server action is finished.
	 * 
	 * @param listener
	 */
	public void setListener(IAddServerListener listener) {
		this.listener = listener;
	}

	private Server[] filterServers(Server[] servers) {
		List<Server> result = new ArrayList<Server>();
		if (servers != null && servers.length > 0) {
			return filter.filter(servers);
		}
		return result.toArray(new Server[0]);
	}

}
