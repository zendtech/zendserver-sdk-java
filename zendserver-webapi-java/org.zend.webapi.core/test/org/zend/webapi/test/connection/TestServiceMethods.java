/*******************************************************************************
 * Copyright (c) Jan 30, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.webapi.test.connection;

import java.net.MalformedURLException;

import junit.framework.Assert;

import org.junit.Test;
import org.zend.webapi.core.WebApiException;
import org.zend.webapi.internal.core.connection.exception.UnexpectedResponseCode;

public class TestServiceMethods {

	@Test
	public void testGetSystemInfo() throws WebApiException,
			MalformedURLException {
		Assert.assertNotNull(TestClientConfiguration.getClient()
				.getSystemInfo());
	}

	@Test
	public void testClusterGetServerStaus() throws WebApiException,
			MalformedURLException {
		Assert.assertNotNull(TestClientConfiguration.getClient()
				.clusterGetServerStatus());
	}

	@Test(expected = UnexpectedResponseCode.class)
	public void testClusterAdd() throws WebApiException, MalformedURLException {
		Assert.assertNotNull(TestClientConfiguration.getClient()
				.clusterAddServer("a", "b", "c"));
	}

	@Test(expected = UnexpectedResponseCode.class)
	public void testClusterRemove() throws WebApiException,
			MalformedURLException {
		Assert.assertNotNull(TestClientConfiguration.getClient()
				.clusterRemoveServer("zend1"));
	}

	@Test(expected = UnexpectedResponseCode.class)
	public void testClusterEnable() throws WebApiException,
			MalformedURLException {
		Assert.assertNotNull(TestClientConfiguration.getClient()
				.clusterEnableServer("zend1"));
	}

	@Test(expected = UnexpectedResponseCode.class)
	public void testClusterDisable() throws WebApiException,
			MalformedURLException {
		Assert.assertNotNull(TestClientConfiguration.getClient()
				.clusterDisableServer("zend1"));
	}

	@Test
	public void testRestartPhp() throws WebApiException, MalformedURLException {
		Assert.assertNotNull(TestClientConfiguration.getClient().restartPhp());
	}
}
