/*******************************************************************************
 * Copyright (c) 2014 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.php.zendserver.deployment.core.tunnel;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.zend.php.zendserver.deployment.core.DeploymentCore;
import org.zend.php.zendserver.deployment.core.Messages;
import org.zend.php.zendserver.deployment.core.targets.JSCHPubKeyDecryptor;
import org.zend.sdklib.internal.target.PublicKeyNotFoundException;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.ProxyHTTP;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.UserInfo;

/**
 * Represents SSH tunnel which can be used e.g. for debugging or database
 * connection. It is configured using {@link SSHTunnelConfiguration} dedicated
 * to a particular server instance.
 * 
 * @author Wojeciech Galanciak, 2014
 */
public class SSHTunnel {

	/**
	 * Set of possible SSH tunnel connection states.
	 * 
	 */
	public enum State {

		CONNECTING,

		CONNECTED,

		DISCONNECTING,

		DISCONNECTED,

		ERROR,

		NOT_SUPPORTED;
	}

	// connection timeout in ms
	private static final int TIMEOUT = 10000;

	private UserInfo userInfo;
	private Session session;
	private SSHTunnelConfiguration config;

	public SSHTunnel(SSHTunnelConfiguration config, UserInfo userInfo) {
		this.config = config;
		this.userInfo = userInfo;
	}

	public SSHTunnel(SSHTunnelConfiguration config) {
		this(config, new SimpleUserInfo());
	}

	/**
	 * @return {@link SSHTunnelConfiguration} instance used for this SSH tunnel
	 */
	public SSHTunnelConfiguration getConfig() {
		return config;
	}

	/**
	 * Establish SSH tunnel connection. Connection settings are based on
	 * {@link SSHTunnelConfiguration} instance provided during SSH tunnel
	 * creation.
	 * 
	 * @return {@link State#CONNECTED} if connection was established
	 *         successfully; {@link State#CONNECTED} if tunnel is already
	 *         connected; {@link State#ERROR} if any issues occurred during
	 *         connection process
	 * @throws TunnelException
	 * @throws JSchException
	 * @see State
	 */
	public State connect() throws TunnelException, JSchException {
		if (session == null) {
			initSession();
		}
		if (!session.isConnected()) {
			session.connect();
			configurePortForwarding();
			return State.CONNECTING;
		} else {
			return State.CONNECTED;
		}
	}

	/**
	 * Disconnect SSH tunnel session.
	 */
	public void disconnect() {
		if (isConnected()) {
			session.disconnect();
		}
	}

	/**
	 * @return <code>true</code> if connection is established; otherwise return
	 *         <code>false</code>
	 */
	public boolean isConnected() {
		return session != null ? session.isConnected() : false;
	}

	public int getDatabasePort() {
		int result = -1;
		if (session != null) {
			try {
				String[] locals = session.getPortForwardingL();
				for (String local : locals) {
					String[] segments = local.split(":"); //$NON-NLS-1$
					if (segments.length == 3) {
						result = Integer.valueOf(segments[0]);
						break;
					}
				}
			} catch (Exception e) {
				DeploymentCore.log(e);
			}
		}
		return result;
	}

	private void configurePortForwarding() throws JSchException {
		List<PortForwarding> entries = config.getPortForwardings();
		List<PortForwarding> configured = new ArrayList<PortForwarding>();
		for (PortForwarding entry : entries) {
			try {
				entry.setup(session);
				configured.add(entry);
			} catch (JSchException e) {
				for (PortForwarding config : configured) {
					config.delete(session);
				}
				throw e;
			}
		}
	}

	private void initSession() throws TunnelException, JSchException {
		JSch jsch = new JSch();
		try {
			session = jsch.getSession(config.getUsername(), config.getHost(),
					22);
			if (config.getPassword() != null) {
				session.setPassword(config.getPassword());
			}
			session.setTimeout(TIMEOUT);
			session.setConfig("compression_level", "9"); //$NON-NLS-1$ //$NON-NLS-2$
			session.setConfig("StrictHostKeyChecking", "no"); //$NON-NLS-1$ //$NON-NLS-2$
			if (config.getHttpProxyHost() != null) {
				ProxyHTTP proxy = new ProxyHTTP(config.getHttpProxyHost(),
						Integer.valueOf(config.getHttpProxyPort()));
				session.setProxy(proxy);
			}
			if (config.getPrivateKey() != null) {
				JSCHPubKeyDecryptor decryptor = new JSCHPubKeyDecryptor();
				String privateKey = config.getPrivateKey();
				String passphrase = decryptor.getPassphase(privateKey);
				if (passphrase != null && passphrase.length() > 0) {
					jsch.addIdentity(privateKey, passphrase);
				} else {
					jsch.addIdentity(privateKey);
				}
			}
		} catch (PublicKeyNotFoundException e) {
			throw new TunnelException(MessageFormat.format(
					Messages.SSHTunnel_ConnectionError, config.getHost()));
		}
	}

}
