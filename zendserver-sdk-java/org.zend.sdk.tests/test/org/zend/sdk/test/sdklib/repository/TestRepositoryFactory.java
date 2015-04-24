/*******************************************************************************
 * Copyright (c) May 18, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.sdk.test.sdklib.repository;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.zend.sdklib.SdkException;
import org.zend.sdklib.internal.repository.http.HttpRepository;
import org.zend.sdklib.internal.repository.local.FileBasedRepository;
import org.zend.sdklib.repository.IRepository;
import org.zend.sdklib.repository.RepositoryFactory;

public class TestRepositoryFactory {

	@Test
	public void testFile() throws SdkException {
		final IRepository createRepository = RepositoryFactory
				.createRepository("file:/c:\\temp", "test1");
		assertTrue(createRepository instanceof FileBasedRepository);
	}

	@Test
	public void testHttp() throws SdkException {
		final IRepository createRepository = RepositoryFactory
				.createRepository("http://www.cnn.com", "test2");
		assertTrue(createRepository instanceof HttpRepository);
	}

}
