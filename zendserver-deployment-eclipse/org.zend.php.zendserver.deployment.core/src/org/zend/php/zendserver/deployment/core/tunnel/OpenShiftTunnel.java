/*******************************************************************************
 * Copyright (c) 2012 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.php.zendserver.deployment.core.tunnel;

import org.zend.php.zendserver.deployment.core.Messages;
import org.zend.php.zendserver.deployment.core.targets.JSCHPubKeyDecryptor;
import org.zend.sdklib.internal.target.PublicKeyNotFoundException;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;

/**
 * SSH tunnel for OpenShift targets debugging.
 * 
 * @author Wojciech Galanciak, 2012
 *
 */
public class OpenShiftTunnel extends AbstractSSHTunnel {
	
	private static int database_port = 12306;

	private String uuid;
	private String internalHost;

	public OpenShiftTunnel(String uuid, String baseUrl, String internalHost, String privateKey) {
		super(baseUrl, privateKey);
		if (uuid == null || privateKey == null) {
			throw new IllegalArgumentException(
					"error setting user or filename to Ssh Tunnel"); //$NON-NLS-1$
		}
		this.uuid = uuid;
		this.internalHost = internalHost;
	}

	public String getUuid() {
		return uuid;
	}
	
	/* (non-Javadoc)
	 * @see org.zend.php.zendserver.deployment.core.tunnel.AbstractSSHTunnel#configureSession()
	 */
	protected void configureSession() throws TunnelException {
		try {
			session.setPortForwardingR(internalHost, 17000, "127.0.0.1", 17000); //$NON-NLS-1$
			session.setPortForwardingL(database_port++, internalHost, 3306);
		} catch (JSchException e) {
			final String msg = Messages.bind(Messages.ZendDevCloudTunnel_1,
					baseUrl);
			throw new TunnelException(msg);
		}
	}

	/* (non-Javadoc)
	 * @see org.zend.php.zendserver.deployment.core.tunnel.AbstractSSHTunnel#createSession()
	 */
	protected void createSession() throws TunnelException, JSchException {
		JSch jsch = new JSch();
		try {
			session = jsch.getSession(uuid, baseUrl, 22);
			session.setConfig("StrictHostKeyChecking", "no"); //$NON-NLS-1$ //$NON-NLS-2$
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
			throw new TunnelException(Messages.OpenShiftTunnel_CannotFindKey);
		}
	}

}
