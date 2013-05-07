/*******************************************************************************
 * Copyright (c) 2013 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.webapi.test.connection.services;

import java.net.MalformedURLException;

import org.junit.Test;
import org.zend.webapi.core.WebApiClient;
import org.zend.webapi.core.WebApiException;
import org.zend.webapi.core.connection.data.LibraryList;
import org.zend.webapi.core.connection.data.values.ServerType;
import org.zend.webapi.core.connection.response.ResponseCode;
import org.zend.webapi.test.AbstractTestServer;
import org.zend.webapi.test.Configuration;
import org.zend.webapi.test.DataUtils;

public class TestLibraryServices extends AbstractTestServer {

	@Test
	public void testLibraryGetStatus() throws WebApiException,
			MalformedURLException {
		initMock(handler.libraryGetStatus(), "libraryGetStatus",
				ResponseCode.OK);
		WebApiClient client = Configuration.getClient();
		
		client.setServerType(ServerType.ZEND_SERVER);
		LibraryList libraryList = client.libraryGetStatus();
		DataUtils.checkValidLibraryList(libraryList);
	}

}
