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
import org.zend.php.server.internal.ui.Messages;
import org.zend.php.zendserver.deployment.core.targets.TargetsManagerService;
import org.zend.sdklib.manager.TargetsManager;
import org.zend.sdklib.target.IZendTarget;
import org.zend.webapi.core.connection.data.values.ZendServerVersion;

/**
 * Component for presenting combo box with list of servers which fulfill
 * specified requirements.
 * 
 * @author Wojciech Galanciak, 2014
 * 
 */
@SuppressWarnings("restriction")
public class ServersCombo {

	public interface IAddServerListener {

		public void serverAdded(String name);

	}

	/**
	 * Type of servers which should be populated in a combo.
	 * 
	 */
	public enum Type {
		ALL,

		PHPCLOUD,

		OPENSHIFT,

		ZEND_SERVER_6;
	}

	private TargetsManager targetsManager = TargetsManagerService.INSTANCE
			.getTargetManager();

	private Combo serversCombo;

	private Button addServerButton;

	private Server[] serversList = new Server[0];

	private String labelText;

	private String tooltip;

	private Type type;

	private boolean addServer;

	private IAddServerListener listener;

	public ServersCombo() {
		this(Type.ALL, false);
	}

	public ServersCombo(Type type) {
		this(type, false);
	}

	public ServersCombo(boolean addTarget) {
		this(Type.ALL, addTarget);
	}

	public ServersCombo(Type type, boolean addTarget) {
		this.type = type;
		this.addServer = addTarget;
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
			IZendTarget target = getTarget(serversList[i]);
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
	public Server getSelected() {
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
		return getTarget(serversList[idx]);
	}

	/**
	 * Update list of servers and populate combo box with current values.
	 */
	public void updateItems() {
		serversList = filterServers(ServersManager.getServers());
		serversCombo.removeAll();
		String defaultId = targetsManager.getDefaultTargetId();
		int defaultNo = 0;

		if (serversList.length != 0) {
			int i = 0;
			for (Server server : serversList) {
				IZendTarget target = getTarget(server);
				if (target != null && target.getId().equals(defaultId)) {
					defaultNo = i;
				}
				serversCombo.add(server.getName());
				i++;
			}
		}
		if (serversCombo.getItemCount() > 0) {
			serversCombo.select(defaultNo);
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
		Label label = new Label(parent, SWT.NONE);
		label.setText(labelText);
		Composite comboContainer = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(2, false);
		layout.horizontalSpacing = 0;
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		layout.verticalSpacing = 0;
		comboContainer.setLayout(layout);
		comboContainer.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false));
		serversCombo = new Combo(comboContainer, SWT.SIMPLE | SWT.DROP_DOWN
				| SWT.READ_ONLY);
		serversCombo.setToolTipText(tooltip);
		serversCombo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
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
			for (Server server : servers) {
				IZendTarget target = getTarget(server);
				if (target == null) {
					continue;
				} else if (type == Type.PHPCLOUD
						&& !TargetsManager.isPhpcloud(target)) {
					continue;
				} else if (type == Type.OPENSHIFT
						&& !TargetsManager.isOpenShift(target)) {
					continue;
				} else if (type == Type.ZEND_SERVER_6) {
					if (!TargetsManager.checkExactVersion(target,
							ZendServerVersion.v6_X_X)) {
						continue;
					}
				}
				result.add(server);
			}
		}
		return result.toArray(new Server[0]);
	}

	private IZendTarget getTarget(Server server) {
		if (server != null) {
			TargetsManager manager = TargetsManagerService.INSTANCE
					.getTargetManager();
			if (server != null) {
				String serverName = server.getName();
				IZendTarget[] targets = manager.getTargets();
				for (IZendTarget target : targets) {
					if (serverName.equals(target.getServerName())) {
						return target;
					}
				}
			}
		}
		return null;
	}

}
