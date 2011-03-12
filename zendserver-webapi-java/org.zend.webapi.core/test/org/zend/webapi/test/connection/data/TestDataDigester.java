/*******************************************************************************
 * Copyright (c) Feb 2, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.webapi.test.connection.data;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;

import org.junit.Assert;
import org.junit.Test;
import org.restlet.data.MediaType;
import org.restlet.ext.xml.DomRepresentation;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import org.zend.webapi.core.connection.data.DataDigster;
import org.zend.webapi.core.connection.data.IResponseData;
import org.zend.webapi.core.connection.data.ServerInfo;
import org.zend.webapi.core.connection.data.ServersList;
import org.zend.webapi.core.connection.data.SystemInfo;
import org.zend.webapi.core.connection.data.values.LicenseInfoStatus;
import org.zend.webapi.core.connection.data.values.SystemEdition;
import org.zend.webapi.core.connection.data.values.WebApiVersion;

/**
 * Test {@link DataDigster}
 * 
 * @author Roy
 * 
 */
public class TestDataDigester {

	public static class DomRepresentationHelper extends DomRepresentation {

		public DomRepresentationHelper(String fileName) throws Exception {
			super(MediaType.APPLICATION_ALL_XML);
			setDocument(createDocument(fileName));
		}

		private Document createDocument(String fileName) throws IOException,
				SAXException {
			final DocumentBuilder documentBuilder = getDocumentBuilder();
			final URL resource = this.getClass().getResource(fileName);
			final InputStream content = (InputStream) resource.getContent();
			return documentBuilder.parse(content);
		}
	}

	@Test
	public void testSystemInfo1() throws Exception {
		final SystemInfo responseData = (SystemInfo) getResponseData(
				"systemInfo1.xml", IResponseData.ResponseType.SYSTEM_INFO);
		Assert.assertEquals(responseData.getEdition(),
				SystemEdition.ZEND_SERVER_CLUSER_MANAGER);
		Assert.assertEquals(responseData.getManagerLicenseInfo().getStatus(),
				LicenseInfoStatus.SERVER_LIMIT_EXCEEDED);
	}

	@Test
	public void testSystemInfo2() throws Exception {
		final SystemInfo responseData = (SystemInfo) getResponseData(
				"systemInfo2.xml", IResponseData.ResponseType.SYSTEM_INFO);

		Assert.assertEquals(responseData.getEdition(),
				SystemEdition.ZEND_SERVER_COMMUNITY_EDITION);
		Assert.assertEquals(responseData.getSupportedApiVersions().get(0),
				WebApiVersion.V1);
		Assert.assertEquals(responseData.getMessageList().getError(), null);
	}

	@Test
	public void testSystemInfo3() throws Exception {
		final SystemInfo responseData = (SystemInfo) getResponseData(
				"systemInfo3.xml", IResponseData.ResponseType.SYSTEM_INFO);
		Assert.assertTrue(responseData.getMessageList().getError().size() > 0);
	}

	@Test
	public void testRestartPhp() throws Exception {
		final ServersList responseData = (ServersList) getResponseData(
				"restartPhp.xml", IResponseData.ResponseType.SERVERS_LIST);
		Assert.assertTrue(responseData.getServerInfo().size() == 3);
	}

	@Test
	public void testClusterEnableServer() throws Exception {
		final ServerInfo responseData = (ServerInfo) getResponseData(
				"clusterEnableServer.xml", IResponseData.ResponseType.SERVER_INFO);
		Assert.assertEquals(responseData.getAddress(), "https://www-02.local:10082/ZendServer");
	}

	@Test
	public void testClusterDisableServer() throws Exception {
		final ServerInfo responseData = (ServerInfo) getResponseData(
				"clusterDisableServer.xml", IResponseData.ResponseType.SERVER_INFO);
		Assert.assertEquals(responseData.getAddress(), "https://www-02.local:10082/ZendServer");
	}
	
	@Test
	public void testClusterAddServer() throws Exception {
		final ServerInfo responseData = (ServerInfo) getResponseData(
				"clusterAddServer.xml", IResponseData.ResponseType.SERVER_INFO);
		Assert.assertEquals(responseData.getAddress(), "https://www-05.local:10082/ZendServer");
	}
	
	@Test
	public void testClusterRemoveServer() throws Exception {
		final ServerInfo responseData = (ServerInfo) getResponseData(
				"clusterRemoveServer.xml", IResponseData.ResponseType.SERVER_INFO);
		Assert.assertEquals(responseData.getAddress(), "https://www-02.local:10082/ZendServer");
	}
	
	@Test
	public void testClusterGetServerStatus() throws Exception {
		final ServersList responseData = (ServersList) getResponseData(
				"clusterGetServerStatus.xml", IResponseData.ResponseType.SERVERS_LIST);
		Assert.assertEquals(responseData.getServerInfo().size(), 2);
	}

	/**
	 * @param fileName
	 * @param type
	 * @return
	 * @throws Exception
	 */
	private IResponseData getResponseData(String fileName,
			IResponseData.ResponseType type) throws Exception {
		DomRepresentation representation = new DomRepresentationHelper(fileName);
		final DataDigster dataDigster = new DataDigster(type, representation);
		dataDigster.digest();
		return dataDigster.getResponseData();
	}
}
