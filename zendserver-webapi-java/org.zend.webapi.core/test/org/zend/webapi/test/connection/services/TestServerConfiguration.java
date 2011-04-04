/*******************************************************************************
 * Copyright (c) Feb 9, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.webapi.test.connection.services;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import junit.framework.Assert;

import org.junit.Test;
import org.restlet.engine.io.BioUtils;
import org.restlet.representation.Representation;
import org.zend.webapi.core.WebApiException;
import org.zend.webapi.core.connection.data.ServerConfig;
import org.zend.webapi.core.connection.data.ServersList;
import org.zend.webapi.core.connection.request.RequestParameter;
import org.zend.webapi.core.connection.response.ResponseCode;
import org.zend.webapi.internal.core.connection.exception.InternalWebApiException;
import org.zend.webapi.internal.core.connection.request.MultipartRepresentation;
import org.zend.webapi.test.AbstractTestServer;
import org.zend.webapi.test.Configuration;

public class TestServerConfiguration extends AbstractTestServer {

	@Test
	public void testExportConfiguration() throws WebApiException,
			FileNotFoundException, IOException {
		initConfigMock(handler.configurationExport(), "configurationExport",
				ResponseCode.OK);
		final ServerConfig config = Configuration.getClient()
				.configuratioExport();
		Assert.assertTrue(config.getFileSize() > 0);

		final File file = File.createTempFile("abc", "test");
		FileOutputStream fos = new FileOutputStream(file);
		fos.write(config.getFileContent());
		fos.close();
		System.out.println("file is " + file.getAbsolutePath());
	}

	@Test
	public void testImportConfiguration() throws WebApiException,
			FileNotFoundException, IOException {
		final File tFile = prepareFile();
		final ServersList list = Configuration.getClient()
				.configuratioImport(tFile);

		Assert.assertTrue(list.getServerInfo().size() > 0);
	}

	@Test
	public void testMultipart() throws IOException {
		final ArrayList<RequestParameter<?>> arrayList = new ArrayList<RequestParameter<?>>();
		arrayList.add(new RequestParameter<Boolean>("ignoreSystemMismatch",
				true));
		File file = getTempFile("mySavedConfig");
		file.deleteOnExit();
		arrayList.add(new RequestParameter<File>("configFile", file));
		Representation representation = new MultipartRepresentation(arrayList,
				"--bla-bla-bla--");
		final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		representation.write(outputStream);
		final String actual = outputStream.toString();
		final InputStream resourceAsStream = this.getClass()
				.getResourceAsStream("multipart.txt");

		String expected = BioUtils.toString(resourceAsStream);
		expected = expected.replace("%filename%", file.getName());
		Assert.assertEquals("Error comparing expected/actual", expected, actual);
	}

	private File getTempFile(String prefix) throws IOException {
		File file = File.createTempFile(prefix, ".zcfg");
		FileWriter writer = new FileWriter(file);
		writer.write("[...binary data follows...]");
		writer.flush();
		writer.close();
		return file;
	}

	private File prepareFile() throws InternalWebApiException {
		File tFile = null;
		try {
			tFile = File.createTempFile("test", "zcfg");
		} catch (IOException e) {
		}
		tFile.deleteOnExit();

		final InputStream isSource = this.getClass().getResourceAsStream(
				"myConfig.cfg");
		try {
			BioUtils.copy(isSource, new FileOutputStream(tFile));
		} catch (Exception e) {
			throw new InternalWebApiException(e);
		}
		return tFile;
	}
}
