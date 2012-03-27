/*******************************************************************************
 * Copyright (c) May 18, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.php.zendserver.deployment.core.tunnel;

import java.io.IOException;

import org.zend.php.zendserver.deployment.core.DeploymentCore;
import org.zend.php.zendserver.deployment.core.Messages;
import org.zend.sdklib.internal.target.ZendDevCloud;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.ProxyHTTP;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.UserInfo;

/**
 * Opens tunnel to Zend DevCloud, <br/>
 * see more details at {@link https
 * ://my.phpcloud.com/help/putty-ssh-debug-tunnel}
 * 
 * @author Roy, 2011
 */
public class ZendDevCloudTunnel {

	public enum State {

		CONNECTING,

		CONNECTED,

		DISCONNECTING,

		DISCONNECTED,

		ERROR,

		NOT_SUPPORTED;
	}

	private static int database_port = 13306;

	private String user;
	private String privateKey;

	private String baseUrl;
	private UserInfo ui;
	private Session session;

	/**
	 * @param user
	 *            - username devcloud account
	 * @param baseUrl
	 *            - of devcloud host
	 * @param filename
	 *            - of the public key (should be in PEM format)
	 */
	public ZendDevCloudTunnel(String user, String baseUrl, String privateKey,
			UserInfo ui) {
		if (user == null || privateKey == null || ui == null) {
			throw new IllegalArgumentException(
					"error setting user or filename to Ssh Tunnel"); //$NON-NLS-1$
		}
		this.user = user;
		this.baseUrl = baseUrl;
		this.privateKey = privateKey;
		this.ui = ui;
	}

	public ZendDevCloudTunnel(String user, String privateKey) {
		this(user, ZendDevCloud.DEVPASS_HOST, privateKey, new EmptyUserInfo());
	}

	public State connect() throws IOException {
		if (session != null) {
			if (!session.isConnected()) {
				try {
					session.connect();
					return State.CONNECTING;
				} catch (JSchException e) {
					throw new IOException(e);
				}
			} else {
				return State.CONNECTED;
			}
		}
		if (session == null) {
			try {
				createSession();
				session.connect();
			} catch (JSchException e) {
				throw new IOException(e);
			}

			try {
				session.setPortForwardingR(10137, "127.0.0.1", 10137); //$NON-NLS-1$
				session.setPortForwardingL(database_port++, user
						+ "-db." + baseUrl, 3306); //$NON-NLS-1$
				return State.CONNECTING;
			} catch (JSchException e) {
				final String msg = Messages.bind(Messages.ZendDevCloudTunnel_1,
						user, baseUrl);
				throw new IOException(msg);
			}
		}
		return State.ERROR;
	}

	public String getUser() {
		return user;
	}

	public boolean isConnected() {
		return session != null ? session.isConnected() : false;
	}

	public void disconnect() {
		if (isConnected()) {
			session.disconnect();
		}
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

	private String getPrivateKeyFile() throws IOException {
		return privateKey;
	}

	private void createSession() throws IOException {
		JSch jsch = new JSch();
		try {
			session = jsch.getSession(user, user + "." + baseUrl, 22); //$NON-NLS-1$
			session.setUserInfo(ui);
 	        session.setConfig("compression_level", "9"); //$NON-NLS-1$ //$NON-NLS-2$
			final ProxyHTTP proxy = new ProxyHTTP(user + "." + baseUrl, 21653); //$NON-NLS-1$
			session.setProxy(proxy);
			jsch.addIdentity(getPrivateKeyFile());
		} catch (JSchException e) {
			throw new IOException(e);
		}
	}

}
