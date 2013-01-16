/*******************************************************************************
 * Copyright (c) May 18, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.php.zendserver.deployment.core.tunnel;

import org.zend.php.zendserver.deployment.core.DeploymentCore;

import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.UserInfo;

/**
 * Represents SSH tunnel which can be used e.g. for debugging or database
 * connection.
 * 
 * @author Roy, 2011
 */
public abstract class AbstractSSHTunnel {

	public enum State {

		CONNECTING,

		CONNECTED,

		DISCONNECTING,

		DISCONNECTED,

		ERROR,

		NOT_SUPPORTED;
	}

	protected String baseUrl;
	protected String privateKey;

	protected UserInfo ui;
	protected Session session;

	/**
	 * @param baseUrl
	 *            host
	 * @param filename
	 *            the private key
	 */
	public AbstractSSHTunnel(String baseUrl, String privateKey, UserInfo ui) {
		this.baseUrl = baseUrl;
		this.privateKey = privateKey;
		this.ui = ui;
	}

	public AbstractSSHTunnel(String baseUrl, String privateKey) {
		this(baseUrl, privateKey, new EmptyUserInfo());
	}

	public State connect() throws TunnelException, JSchException {
		if (session != null) {
			if (!session.isConnected()) {
				session.connect();
				return State.CONNECTING;
			} else {
				return State.CONNECTED;
			}
		}
		if (session == null) {
			createSession();
			session.connect();
			configureSession();
			return State.CONNECTING;
		}
		return State.ERROR;
	}
	
	public void disconnect() {
		if (isConnected()) {
			session.disconnect();
		}
	}

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

	protected abstract void configureSession() throws TunnelException;

	protected abstract void createSession() throws TunnelException, JSchException;

	protected String getPrivateKeyFile() {
		return privateKey;
	}

}
