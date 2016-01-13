/*******************************************************************************
 * Copyright (c) 2014 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.php.zendserver.deployment.core.tunnel;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.zend.php.zendserver.deployment.core.tunnel.SSHTunnel.State;

import com.jcraft.jsch.JSchException;

/**
 * SSH tunnel manager. It is responsible for managing SSH tunnels for different
 * servers.
 * 
 * @author Wojciech Galanciak, 2014
 * 
 */
public class SSHTunnelManager {

	private static SSHTunnelManager manager;

	private Map<String, SSHTunnel> tunnels;

	private SSHTunnelManager() {
		this.tunnels = new HashMap<String, SSHTunnel>();
	}

	public static SSHTunnelManager getManager() {
		if (manager == null) {
			manager = new SSHTunnelManager();
		}
		return manager;
	}

	/**
	 * Set up SSH tunnel connection base on a configuration provided as an
	 * argument.
	 * 
	 * @param config
	 *            {@link SSHTunnelConfiguration} instance
	 * @return {@link State#CONNECTED} if connection was established
	 *         successfully; {@link State#CONNECTED} if tunnel is already
	 *         connected; {@link State#ERROR} if any issues occurred during
	 *         connection process
	 * @throws TunnelException
	 * @throws JSchException
	 */
	public State connect(SSHTunnelConfiguration config) throws TunnelException,
			JSchException {
		SSHTunnel tunnel = tunnels.get(config.getHost());
		State result = State.CONNECTED;
		if (tunnel == null) {
			tunnel = new SSHTunnel(config);
			try {
				result = tunnel.connect();
			} catch (TunnelException e) {
				tunnel.disconnect();
				tunnels.remove(tunnel.getConfig().getHost());
				throw e;
			}
		} else if (!tunnel.isConnected()) {
			try {
				result = tunnel.connect();
			} catch (Exception e) {
				// If tunnel exists but it is disconnected and failed to
				// connect it again
				// try to remove old tunnel and create a new one for it
				tunnels.remove(tunnel.getConfig().getHost());
				result = connect(tunnel.getConfig());
			}
		}
		tunnels.put(config.getHost(), tunnel);
		return result;
	}

	/**
	 * Disconnect tunnel for spepcifed host.
	 * 
	 * @param host
	 *            host name
	 */
	public void disconnect(String host) {
		SSHTunnel tunnel = tunnels.get(host);
		if (tunnel != null) {
			tunnel.disconnect();
			tunnels.remove(host);
		}
	}

	/**
	 * Disconnect all acitve tunnels.
	 */
	public void disconnectAll() {
		Set<String> keySet = tunnels.keySet();
		for (String key : keySet) {
			disconnect(key);
		}
	}

	/**
	 * Check connection state of SSH tunnel for specified host.
	 * 
	 * @param host
	 *            host name
	 * @return <code>true</code> if tunnel for specified host is opened;
	 *         otherwise return <code>false</code>
	 */
	public boolean isConnected(String host) {
		SSHTunnel tunnel = tunnels.get(host);
		if (tunnel != null && tunnel.isConnected()) {
			return true;
		}
		return false;
	}

}
