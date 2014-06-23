/*******************************************************************************
 * Copyright (c) 2014 Zend Technologies.
 * All rights reserved. This program and the accompanying materials
 * are the copyright of Zend Technologies and is protected under
 * copyright laws of the United States.
 * You must not copy, adapt or redistribute this document for 
 * any use.
 *******************************************************************************/
package org.zend.php.zendserver.deployment.ui.preferences;

import java.io.File;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.php.internal.server.core.Server;
import org.eclipse.php.internal.server.core.manager.ServersManager;
import org.eclipse.php.internal.ui.wizards.IControlHandler;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.statushandlers.StatusManager;
import org.zend.php.server.ui.fragments.AbstractCompositeFragment;
import org.zend.php.zendserver.deployment.core.targets.EclipseSSH2Settings;
import org.zend.php.zendserver.deployment.core.targets.TargetsManagerService;
import org.zend.php.zendserver.deployment.core.tunnel.SSHTunnelConfiguration;
import org.zend.php.zendserver.deployment.ui.Activator;
import org.zend.sdklib.internal.target.ZendDevCloud;
import org.zend.sdklib.internal.target.ZendTarget;
import org.zend.sdklib.manager.TargetsManager;
import org.zend.sdklib.target.IZendTarget;

/**
 * Abstract composite for Phpcloud and OpenShift fragments.
 * 
 * @author Wojciech Galanciak, 2014
 * 
 */
@SuppressWarnings("restriction")
public abstract class AbstractCloudCompositeFragment extends
		AbstractCompositeFragment {

	private int lastPort = -1;

	protected ModifyListener modifyListener = new ModifyListener() {
		public void modifyText(ModifyEvent e) {
			updateData();
			validate();
		}
	};

	protected AbstractCloudCompositeFragment(Composite parent,
			IControlHandler handler, boolean isForEditing, String title,
			String description) {
		super(parent, handler, isForEditing, title, title, description);
		createControl(isForEditing);
	}

	@Override
	public boolean performOk() {
		return isComplete();
	}

	protected abstract void detectServers(IProgressMonitor monitor);

	/**
	 * Set up SSH settings for specified server base on target type.
	 * 
	 * @param server
	 * @param target
	 */
	protected abstract void setupSSHConfiguration(Server server,
			IZendTarget target);

	/**
	 * @return prefix of a new private key name
	 */
	protected abstract String getGeneratedKeyName();

	protected abstract void updateData();

	/**
	 * Generate new private RSA key.
	 * 
	 * @return full path to generated key
	 */
	protected String generateKey() {
		String sshHome = EclipseSSH2Settings.getSSHHome();
		String file;
		if (sshHome != null) {
			File tmpFile = new File(sshHome, getGeneratedKeyName());
			int i = 1;
			while (tmpFile.exists()) {
				tmpFile = new File(sshHome, getGeneratedKeyName() + i);
				i++;
			}

			file = tmpFile.getAbsolutePath();

			boolean confirm = MessageDialog
					.openConfirm(
							getShell(),
							Messages.AbstarctCloudCompositeFragment_GenerateKeyTitle,
							Messages.bind(
									Messages.AbstarctCloudCompositeFragment_GenerateKeyMessage,
									file));
			if (!confirm) {
				return null;
			}
		} else {
			FileDialog d = new FileDialog(getShell(), SWT.SAVE);
			file = d.open();
			if (file == null) {
				return null;
			}
		}
		try {
			EclipseSSH2Settings.createPrivateKey(ZendDevCloud.KEY_TYPE, file);
			return file;
		} catch (CoreException e) {
			StatusManager.getManager().handle(
					new Status(IStatus.ERROR, Activator.PLUGIN_ID,
							e.getMessage(), e), StatusManager.SHOW);
		}
		return null;
	}

	/**
	 * Create copy of specified temporary target.
	 * 
	 * @param temporaryTarget
	 *            temporary target
	 * @return target copy without temporary flag
	 */
	protected IZendTarget copy(ZendTarget temporaryTarget) {
		ZendTarget target = new ZendTarget(temporaryTarget.getId(),
				temporaryTarget.getHost(),
				temporaryTarget.getDefaultServerURL(),
				temporaryTarget.getKey(), temporaryTarget.getSecretKey());
		String[] keys = temporaryTarget.getPropertiesKeys();
		for (String key : keys) {
			target.addProperty(key, temporaryTarget.getProperty(key));
		}
		return target;
	}

	/**
	 * Check if there is already a server with specified host name.
	 * 
	 * @param host
	 * @return {@link Server} instance which has specified host or
	 *         <code>null</code> if such server does not exist
	 */
	protected Server getExistingServer(String host) {
		Server[] servers = ServersManager.getServers();
		for (Server server : servers) {
			if (server.getHost().equals(host)) {
				return server;
			}
		}
		return null;
	}

	/**
	 * Check if there is already a target with specified host URL
	 * 
	 * @param target
	 * @return {@link IZendTarget} instance which has specified URL or
	 *         <code>null</code> if such target does not exist
	 */
	protected IZendTarget findExistingTarget(IZendTarget target) {
		TargetsManager manager = TargetsManagerService.INSTANCE
				.getTargetManager();
		IZendTarget[] targets = manager.getTargets();
		for (IZendTarget t : targets) {
			if (TargetsManager.isPhpcloud(t)
					&& t.getHost().getHost().equals(target.getHost().getHost())) {
				return t;
			}
		}
		return null;
	}

	/**
	 * Generate new port for database local port forwarding which is not in
	 * conflict of any existing SSH tunnel configuration (including any server
	 * created during cloud servers detection which has not been added yet).
	 * 
	 * @return port for local port forwarding for database connection
	 */
	protected int getNewDatabasePort() {
		int port = SSHTunnelConfiguration.getNewDatabasePort();
		if (lastPort == -1) {
			lastPort = port;
		}
		if (lastPort == port) {
			port++;
			lastPort = port;
		}
		return port;
	}

}
