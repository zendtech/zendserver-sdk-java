/*******************************************************************************
 * Copyright (c) 2014 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.php.zendserver.deployment.core.tunnel;

import junit.framework.TestCase;

/**
 * 
 * @author Wojciech Galanciak, 2014
 * 
 */
public class PortForwardingTests extends TestCase {

	private static final int DEFAULT_REMOTE_PORT = 20000;
	private static final int DEFAULT_LOCAL_PORT = 10000;
	private static final String DEFAULT_REMOTE_HOST = "192.168.0.1"; //$NON-NLS-1$
	private static final String DEFAULT_LOCAL_HOST = "127.0.0.1"; //$NON-NLS-1$

	public void testSerializeLocal() {
		PortForwarding portForwarding = PortForwarding.createLocal(
				DEFAULT_LOCAL_HOST, DEFAULT_LOCAL_PORT, DEFAULT_REMOTE_HOST,
				DEFAULT_REMOTE_PORT);
		String result = portForwarding.serialize();
		assertEquals("-L 127.0.0.1:10000:192.168.0.1:20000", result); //$NON-NLS-1$
	}

	public void testSerializeLocalNoHost() {
		PortForwarding portForwarding = PortForwarding.createLocal(
				DEFAULT_LOCAL_PORT, DEFAULT_REMOTE_HOST, DEFAULT_REMOTE_PORT);
		String result = portForwarding.serialize();
		assertEquals("-L 10000:192.168.0.1:20000", result); //$NON-NLS-1$
	}

	public void testSerializeRemote() {
		PortForwarding portForwarding = PortForwarding.createRemote(
				DEFAULT_REMOTE_HOST, DEFAULT_REMOTE_PORT, DEFAULT_LOCAL_HOST,
				DEFAULT_LOCAL_PORT);
		String result = portForwarding.serialize();
		assertEquals("-R 192.168.0.1:20000:127.0.0.1:10000", result); //$NON-NLS-1$
	}

	public void testSerializeRemoteNoHost() {
		PortForwarding portForwarding = PortForwarding.createRemote(
				DEFAULT_REMOTE_PORT, DEFAULT_LOCAL_HOST, DEFAULT_LOCAL_PORT);
		String result = portForwarding.serialize();
		assertEquals("-R 20000:127.0.0.1:10000", result); //$NON-NLS-1$
	}

	public void testDeserializeLocal() {
		PortForwarding portForwarding = PortForwarding
				.deserialize("-L 127.0.0.1:10000:192.168.0.1:20000"); //$NON-NLS-1$
		assertEquals(DEFAULT_LOCAL_HOST, portForwarding.getLocalAddress());
		assertEquals(DEFAULT_LOCAL_PORT, portForwarding.getLocalPort());
		assertEquals(DEFAULT_REMOTE_HOST, portForwarding.getRemoteAddress());
		assertEquals(DEFAULT_REMOTE_PORT, portForwarding.getRemotePort());
	}

	public void testDeserializeLocalNoHost() {
		PortForwarding portForwarding = PortForwarding
				.deserialize("-L 10000:192.168.0.1:20000"); //$NON-NLS-1$
		assertNull(portForwarding.getLocalAddress());
		assertEquals(DEFAULT_LOCAL_PORT, portForwarding.getLocalPort());
		assertEquals(DEFAULT_REMOTE_HOST, portForwarding.getRemoteAddress());
		assertEquals(DEFAULT_REMOTE_PORT, portForwarding.getRemotePort());
	}

	public void testDeserializeRemote() {
		PortForwarding portForwarding = PortForwarding
				.deserialize("-R 192.168.0.1:20000:127.0.0.1:10000"); //$NON-NLS-1$
		assertEquals(DEFAULT_LOCAL_HOST, portForwarding.getLocalAddress());
		assertEquals(DEFAULT_LOCAL_PORT, portForwarding.getLocalPort());
		assertEquals(DEFAULT_REMOTE_HOST, portForwarding.getRemoteAddress());
		assertEquals(DEFAULT_REMOTE_PORT, portForwarding.getRemotePort());
	}

	public void testDeserializeRemoteNoHost() {
		PortForwarding portForwarding = PortForwarding
				.deserialize("-R 20000:127.0.0.1:10000"); //$NON-NLS-1$
		assertEquals(DEFAULT_LOCAL_HOST, portForwarding.getLocalAddress());
		assertEquals(DEFAULT_LOCAL_PORT, portForwarding.getLocalPort());
		assertNull(portForwarding.getRemoteAddress());
		assertEquals(DEFAULT_REMOTE_PORT, portForwarding.getRemotePort());
	}

}
