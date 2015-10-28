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
package org.zend.php.server.ui.fragments;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.php.internal.server.core.Server;
import org.eclipse.php.internal.server.core.manager.ServersManager;
import org.eclipse.php.internal.server.ui.ServerEditPage;
import org.eclipse.php.internal.server.ui.ServersPluginImages;
import org.eclipse.php.internal.ui.wizards.CompositeFragment;
import org.eclipse.php.internal.ui.wizards.IControlHandler;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.zend.php.zendserver.deployment.core.debugger.DeploymentAttributes;

/**
 * Abstract wizard fragment with basic implementation. It is intended to extend
 * this class to provide different implementations of {@link CompositeFragment}.
 * 
 * @author Wojciech Galanciak, 2014
 * 
 */
@SuppressWarnings("restriction")
public abstract class AbstractCompositeFragment extends CompositeFragment {

	protected AbstractCompositeFragment(Composite parent,
			IControlHandler handler, boolean isForEditing, String name,
			String title, String description) {
		super(parent, handler, isForEditing);
		setDisplayName(name);
		setTitle(title);
		setDescription(description);
		controlHandler.setTitle(title);
		controlHandler.setDescription(description);
		controlHandler.setImageDescriptor(ServersPluginImages.DESC_WIZ_SERVER);
		if (isForEditing) {
			setData(((ServerEditPage) controlHandler).getServer());
		}
		init();
	}

	/**
	 * Override the super setData to handle only Server types.
	 * 
	 * @throws IllegalArgumentException
	 *             if the given object is not a {@link Server}
	 */
	public void setData(Object server) throws IllegalArgumentException {
		if (server != null && !(server instanceof Server)) {
			throw new IllegalArgumentException("Given object is not a server"); //$NON-NLS-1$
		}
		super.setData(server);
	}

	/**
	 * Returns {@link Server} instance that is attached to this fragment.
	 * 
	 * @return attached {@link Server} instance
	 */
	public Server getServer() {
		return (Server) getData();
	}

	public void setMessage(final String message, final int type) {
		Display.getDefault().syncExec(new Runnable() {
			public void run() {
				controlHandler.setMessage(message, type);
				setComplete(type != IMessageProvider.ERROR);
				controlHandler.update();
			}
		});
	}

	public void setIncompleteMessage(final String message) {
		Display.getDefault().syncExec(new Runnable() {
			public void run() {
				controlHandler.setMessage(message, IMessageProvider.NONE);
				setComplete(false);
				controlHandler.update();
			}
		});
	}

	protected void createControl() {
		GridLayout layout = new GridLayout();
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		setLayout(layout);
		setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		Composite composite = new Composite(this, SWT.NONE);
		layout = new GridLayout(3, false);
		layout.marginHeight = 10;
		layout.marginWidth = 10;
		composite.setLayout(layout);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		createContents(composite);
	}

	protected abstract void init();

	/**
	 * Check if new server's name is in conflict with any existing server.
	 * 
	 * @param name
	 *            new server's name
	 * @return <code>true</code> if there is a conflict with a name of existing
	 *         server; otherwise return <code>false</code>
	 */
	protected boolean isDuplicateName(String name) {
		name = name.trim();
		if (name.equals(getServer().getName())) {
			return false;
		}
		Server[] allServers = ServersManager.getServers();
		if (allServers != null) {
			int size = allServers.length;
			for (int i = 0; i < size; i++) {
				Server server = allServers[i];
				if (name.equals(server.getName())) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Check if there is any server with the same base URL as a specified one.
	 * 
	 * @param base
	 *            URL
	 * @return {@link Server} instance if there is a server with the same base
	 *         URL; otherwise return <code>false</code>
	 */
	protected Server getConflictingServer(Server server) {
		String baseUrl = server.getBaseURL();
		if (baseUrl != null) {
			Server[] servers = ServersManager.getServers();
			if (servers != null) {
				int size = servers.length;
				for (int i = 0; i < size; i++) {
					Server s = servers[i];
					if (baseUrl.equals(s.getBaseURL())) {
						return s;
					}
				}
			}
		}
		return null;
	}

	/**
	 * Check if deployment is enabled on Deployment tab.
	 * 
	 * @return <code>true</code> if deployment is enabled; otherwise return
	 *         <code>false</code>
	 */
	protected boolean isDeploymentEnabled() {
		Server server = getServer();
		if (server != null) {
			return Boolean.valueOf(server.getAttribute(
					DeploymentAttributes.ENABLED.getName(),
					String.valueOf(false)));
		}
		return false;
	}

}
