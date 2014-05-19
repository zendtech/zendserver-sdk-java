/*******************************************************************************
 * Copyright (c) 2014 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.php.zendserver.deployment.core.tunnel;

import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

/**
 * Port forwarding for SSH tunneling. It supports both local and remote side
 * port forwarding.
 * 
 * @author Wojciech Galanciak, 2014
 * 
 */
public class PortForwarding {

	private static final String COLON = ":"; //$NON-NLS-1$

	/**
	 * Port forwarding sides. Possible values are local or remote.
	 */
	public enum Side {
		LOCAL("-L", "local"), //$NON-NLS-1$ //$NON-NLS-2$

		REMOTE("-R", "remote"); //$NON-NLS-1$ //$NON-NLS-2$

		private String sideSwitch;
		private String name;

		private Side(String sideSwitch, String name) {
			this.sideSwitch = sideSwitch;
			this.name = name;
		}

		public String getSideSwitch() {
			return sideSwitch;
		}

		public String getName() {
			return name;
		}

		public static Side bySwitch(String name) {
			if (name != null) {
				Side[] sides = values();
				for (Side side : sides) {
					if (side.sideSwitch.equals(name)) {
						return side;
					}
				}
			}
			return null;
		}

		public static Side byName(String name) {
			if (name != null) {
				Side[] sides = values();
				for (Side side : sides) {
					if (side.name.equals(name)) {
						return side;
					}
				}
			}
			return null;
		}
	}

	private Side side;
	private String localAddress;
	private int localPort;
	private String remoteAddress;
	private int remotePort;

	private PortForwarding(Side side, String localAddress, int localPort,
			String remoteAddress, int remotePort) {
		super();
		this.side = side;
		this.localAddress = localAddress;
		this.localPort = localPort;
		this.remoteAddress = remoteAddress;
		this.remotePort = remotePort;
	}

	/**
	 * Factory method to create a local side port forwarding.
	 * 
	 * @param localAddress
	 *            the network interface we should be listening on
	 * @param localPort
	 *            the local port to listen on
	 * @param remoteAddress
	 *            the remote host (i.e. at the server-side) to forward the
	 *            connections to
	 * @param remotePort
	 *            the port at the remote host to forward the connections to
	 * @return
	 */
	public static PortForwarding createLocal(String localAddress,
			int localPort, String remoteAddress, int remotePort) {
		return new PortForwarding(Side.LOCAL, localAddress, localPort,
				remoteAddress, remotePort);
	}

	/**
	 * Factory method to create a local side port forwarding.
	 * 
	 * @param localPort
	 *            the local port to listen on
	 * @param remoteAddress
	 *            the remote host (i.e. at the server-side) to forward the
	 *            connections to
	 * @param remotePort
	 *            the port at the remote host to forward the connections to
	 * @return
	 */
	public static PortForwarding createLocal(int localPort,
			String remoteAddress, int remotePort) {
		return createLocal(null, localPort, remoteAddress, remotePort);
	}

	/**
	 * Factory method to create a remote side port forwarding.
	 * 
	 * @param remoteAddress
	 *            the network interface to bind on on the remote side
	 * @param remotePort
	 *            the port to listen on on the remote side
	 * @param localAddress
	 *            the host on the local side to forward connections to
	 * @param localPort
	 *            the port at host to forward connections to
	 * @return
	 */
	public static PortForwarding createRemote(String remoteAddress,
			int remotePort, String localAddress, int localPort) {
		return new PortForwarding(Side.REMOTE, localAddress, localPort,
				remoteAddress, remotePort);
	}

	/**
	 * Factory method to create a remote side port forwarding.
	 * 
	 * @param remotePort
	 *            the port to listen on on the remote side
	 * @param localAddress
	 *            the host on the local side to forward connections to
	 * @param localPort
	 *            the port at host to forward connections to
	 * @return
	 */
	public static PortForwarding createRemote(int remotePort,
			String localAddress, int localPort) {
		return createRemote(null, remotePort, localAddress, localPort);
	}

	/**
	 * Parse port forwarding from its string representation. Supported input is
	 * a full command line switch for creating particular type of port
	 * forwarding:
	 * <ul>
	 * <li>local-side: <code>-L [bind_address:]port:host:hostport]</code></li>
	 * <li>remote-side: <code>-R [bind_address:]port:host:hostport]</code></li>
	 * </ul>
	 * 
	 * @param input
	 *            string representation of port forwarding
	 * @return {@link PortForwarding} instance
	 */
	public static PortForwarding deserialize(String input) {
		String[] segments = input.split(" "); //$NON-NLS-1$
		if (segments.length == 2) {
			String[] parts = segments[1].split(COLON);
			switch (Side.bySwitch(segments[0])) {
			case LOCAL:
				if (parts.length == 4) {
					// -L bind_address:port:host:hostport
					return createLocal(parts[0], Integer.valueOf(parts[1]),
							parts[2], Integer.valueOf(parts[3]));
				} else if (parts.length == 3) {
					// -L port:host:hostport
					return createLocal(null, Integer.valueOf(parts[0]),
							parts[1], Integer.valueOf(parts[2]));
				}
				break;
			case REMOTE:
				if (parts.length == 4) {
					// -R bind_address:port:host:hostport
					return createRemote(parts[0], Integer.valueOf(parts[1]),
							parts[2], Integer.valueOf(parts[3]));
				} else if (parts.length == 3) {
					// -R port:host:hostport
					return createRemote(null, Integer.valueOf(parts[0]),
							parts[1], Integer.valueOf(parts[2]));
				}
				break;
			default:
				break;
			}
		}
		return null;
	}

	/**
	 * Serialize {@link PortForwarding} instance to its string representation.
	 * Possible outputs are following:
	 * <ul>
	 * <li>for local-side: <code>-L [bind_address:]port:host:hostport]</code></li>
	 * <li>for remote-side: <code>-R [bind_address:]port:host:hostport]</code></li>
	 * </ul>
	 * 
	 * @return string representation of port forwarding
	 */
	public String serialize() {
		StringBuilder result = new StringBuilder();
		result.append(side.getSideSwitch());
		result.append(" "); //$NON-NLS-1$
		switch (side) {
		case LOCAL:
			if (localAddress != null) {
				result.append(localAddress);
				result.append(COLON);
			}
			result.append(localPort);
			result.append(COLON);
			result.append(remoteAddress);
			result.append(COLON);
			result.append(remotePort);
			break;
		case REMOTE:
			if (remoteAddress != null) {
				result.append(remoteAddress);
				result.append(COLON);
			}
			result.append(remotePort);
			result.append(COLON);
			result.append(localAddress);
			result.append(COLON);
			result.append(localPort);
			break;
		default:
			break;
		}
		return result.toString();
	}

	/**
	 * Setup particular port forwarding for specified SSH session.
	 * 
	 * @param session
	 *            {@link Session} instance
	 * @throws JSchException
	 */
	public void setup(Session session) throws JSchException {
		switch (side) {
		case LOCAL:
			if (localAddress != null) {
				session.setPortForwardingL(localAddress, localPort,
						remoteAddress, remotePort);
			} else {
				session.setPortForwardingL(localPort, remoteAddress, remotePort);
			}
			break;
		case REMOTE:
			if (remoteAddress != null) {
				session.setPortForwardingR(remoteAddress, remotePort,
						localAddress, localPort);
			} else {
				session.setPortForwardingR(remotePort, localAddress, localPort);
			}
			break;
		default:
			break;
		}
	}

	/**
	 * @return local address
	 */
	public String getLocalAddress() {
		return localAddress;
	}

	/**
	 * @return local port
	 */
	public int getLocalPort() {
		return localPort;
	}

	/**
	 * @return remote address
	 */
	public String getRemoteAddress() {
		return remoteAddress;
	}

	/**
	 * @return remote port
	 */
	public int getRemotePort() {
		return remotePort;
	}

	/**
	 * @return port forwarding side
	 * @see Side
	 */
	public Side getSide() {
		return side;
	}

}
