/*******************************************************************************
 * Copyright (c) May 18, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.php.zendserver.deployment.core.tunnel;

import org.zend.php.zendserver.deployment.core.Messages;
import org.zend.php.zendserver.deployment.core.targets.JSCHPubKeyDecryptor;
import org.zend.sdklib.internal.target.PublicKeyNotFoundException;
import org.zend.sdklib.internal.target.ZendDevCloud;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.ProxyHTTP;
import com.jcraft.jsch.UserInfo;

/**
 * Opens tunnel to Zend DevCloud, <br/>
 * see more details at {@link https
 * ://my.phpcloud.com/help/putty-ssh-debug-tunnel}
 * 
 * @author Roy, 2011
 */
public class ZendDevCloudTunnel extends AbstractSSHTunnel {

	private static int database_port = 13306;

	private String user;

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
		super(baseUrl, privateKey, ui);
		if (user == null || privateKey == null || ui == null) {
			throw new IllegalArgumentException(
					"error setting user or filename to Ssh Tunnel"); //$NON-NLS-1$
		}
		this.user = user;
	}

	public ZendDevCloudTunnel(String user, String privateKey) {
		this(user, ZendDevCloud.DEVPASS_HOST, privateKey, new EmptyUserInfo());
	}


	public String getUser() {
		return user;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.zend.php.zendserver.deployment.core.tunnel.SSHTunnel#configureSession
	 * ()
	 */
	protected void configureSession() throws TunnelException {
		try {
			session.setPortForwardingR(10137, "127.0.0.1", 10137); //$NON-NLS-1$
			session.setPortForwardingL(database_port++,
					user + "-db." + baseUrl, 3306); //$NON-NLS-1$
		} catch (JSchException e) {
			final String msg = Messages.bind(Messages.ZendDevCloudTunnel_1,
					user, baseUrl);
			throw new TunnelException(msg);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.zend.php.zendserver.deployment.core.tunnel.SSHTunnel#createSession()
	 */
	protected void createSession() throws TunnelException, JSchException {
		JSch jsch = new JSch();
		try {
			session = jsch.getSession(user, user + "." + baseUrl, 22); //$NON-NLS-1$
			session.setUserInfo(ui);
			session.setConfig("compression_level", "9"); //$NON-NLS-1$ //$NON-NLS-2$
			//session.setConfig("StrictHostKeyChecking", "no"); //$NON-NLS-1$ //$NON-NLS-2$
			final ProxyHTTP proxy = new ProxyHTTP(user + "." + baseUrl, 21653); //$NON-NLS-1$
			session.setProxy(proxy);
			JSCHPubKeyDecryptor decryptor = new JSCHPubKeyDecryptor();
			String privateKey = getPrivateKeyFile();
			String passphrase = decryptor.getPassphase(privateKey);
			if (passphrase != null && passphrase.length() > 0) {
				jsch.addIdentity(privateKey, passphrase);
			} else {
				jsch.addIdentity(privateKey);
			}
		} catch (JSchException e) {
			throw e;
		} catch (PublicKeyNotFoundException e) {
			throw new TunnelException(Messages.ZendDevCloudTunnel_CannotFindKey);
		}
	}

}
