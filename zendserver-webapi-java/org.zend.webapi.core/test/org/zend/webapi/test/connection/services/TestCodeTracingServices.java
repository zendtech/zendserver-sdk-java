/*******************************************************************************
 * Copyright (c) Feb 13, 2012 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.webapi.test.connection.services;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;

import junit.framework.Assert;

import org.junit.Test;
import org.zend.webapi.core.WebApiException;
import org.zend.webapi.core.connection.data.CodeTrace;
import org.zend.webapi.core.connection.data.CodeTraceFile;
import org.zend.webapi.core.connection.data.CodeTracingList;
import org.zend.webapi.core.connection.data.CodeTracingStatus;
import org.zend.webapi.core.connection.response.ResponseCode;
import org.zend.webapi.test.AbstractTestServer;
import org.zend.webapi.test.Configuration;
import org.zend.webapi.test.DataUtils;

public class TestCodeTracingServices extends AbstractTestServer {

	public static final String CONFIG_FOLDER = "codetrace/";
	public static final String EXAMPLE_CODE_TRACE = "1.amf";

	@Test
	public void testCodeTracingDisable() throws WebApiException,
			MalformedURLException {
		initMock(handler.codeTracingDisable(), "codetracingDisable",
				ResponseCode.OK);
		CodeTracingStatus status = Configuration.getClient()
				.codeTracingDisable();
		DataUtils.checkValidCodeTracingStatus(status);
	}

	@Test
	public void testCodeTracingDisableWithRestart() throws WebApiException,
			MalformedURLException {
		initMock(handler.codeTracingDisable(), "codetracingDisable",
				ResponseCode.OK);
		CodeTracingStatus status = Configuration.getClient()
				.codeTracingDisable(true);
		DataUtils.checkValidCodeTracingStatus(status);
	}

	@Test
	public void testCodeTracingEnable() throws WebApiException,
			MalformedURLException {
		initMock(handler.codeTracingEnable(), "codetracingEnable",
				ResponseCode.OK);
		CodeTracingStatus status = Configuration.getClient()
				.codeTracingEnable();
		DataUtils.checkValidCodeTracingStatus(status);
	}

	@Test
	public void testCodeTracingEnableWithRestart() throws WebApiException,
			MalformedURLException {
		initMock(handler.codeTracingEnable(), "codetracingEnable",
				ResponseCode.OK);
		CodeTracingStatus status = Configuration.getClient().codeTracingEnable(
				true);
		DataUtils.checkValidCodeTracingStatus(status);
	}

	@Test
	public void testCodeTracingIsEnabled() throws WebApiException,
			MalformedURLException {
		initMock(handler.codeTracingIsEnabled(), "codetracingIsEnabled",
				ResponseCode.OK);
		CodeTracingStatus status = Configuration.getClient()
				.codeTracingIsEnabled();
		DataUtils.checkValidCodeTracingStatus(status);
	}

	@Test
	public void testCodeTracingCreate() throws WebApiException,
			MalformedURLException {
		initMock(handler.codeTracingCreate(), "codetracingCreate",
				ResponseCode.OK);
		CodeTrace trace = Configuration.getClient().codeTracingCreate(
				"http://example.com");
		DataUtils.checkValidCodeTrace(trace);
	}

	@Test
	public void testCodeTracingDelete() throws WebApiException,
			MalformedURLException {
		initMock(handler.codeTracingDelete(), "codetracingDelete",
				ResponseCode.OK);
		CodeTrace trace = Configuration.getClient()
				.codeTracingDelete("traceId");
		DataUtils.checkValidCodeTrace(trace);
	}

	@Test
	public void testCodeTracingList() throws WebApiException,
			MalformedURLException {
		initMock(handler.codeTracingList(), "codetracingList", ResponseCode.OK);
		CodeTracingList traces = Configuration.getClient().codeTracingList(
				null, null, null, null, (String[]) null);
		DataUtils.checkValidCodeTracingList(traces);
	}

	@Test
	public void testCodetracingDownloadTraceFile() throws WebApiException,
			FileNotFoundException, IOException {
		initCodeTraceMock(handler.codetracingDownloadTraceFile(),
				"codetracingDownloadTraceFile", ResponseCode.OK);
		CodeTraceFile file = Configuration.getClient()
				.codetracingDownloadTraceFile("traceId");
		Assert.assertTrue(file.getFileSize() > 0);
		Assert.assertNotNull(file.getFilename());
		Assert.assertNotNull(file.getFileContent());
		Assert.assertEquals(file.getFileSize(), file.getFileContent().length);
	}

}
