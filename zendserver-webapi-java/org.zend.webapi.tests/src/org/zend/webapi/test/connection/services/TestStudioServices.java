/*******************************************************************************
 * Copyright (c) Feb 13, 2012 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.webapi.test.connection.services;

import java.net.MalformedURLException;

import org.junit.Test;
import org.zend.webapi.core.WebApiException;
import org.zend.webapi.core.connection.data.DebugRequest;
import org.zend.webapi.core.connection.data.ProfileRequest;
import org.zend.webapi.core.connection.response.ResponseCode;
import org.zend.webapi.test.AbstractTestServer;
import org.zend.webapi.test.Configuration;
import org.zend.webapi.test.DataUtils;

public class TestStudioServices extends AbstractTestServer {

	@Test
	public void testStudioStartDebug() throws WebApiException,
			MalformedURLException {
		initMock(handler.studioStartDebug(), "studioStartDebug",
				ResponseCode.OK);
		DebugRequest debugRequest = Configuration.getClient().studioStartDebug(
				"uid", null, null);
		DataUtils.checkValidDebugRequest(debugRequest);
	}

	@Test
	public void testStudioStartProfile() throws WebApiException,
			MalformedURLException {
		initMock(handler.studioStartProfile(), "studioStartProfile",
				ResponseCode.OK);
		ProfileRequest profileRequest = Configuration.getClient()
				.studioStartProfile("uid");
		DataUtils.checkValidProfileRequest(profileRequest);
	}

}
