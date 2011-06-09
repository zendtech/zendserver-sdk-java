/*******************************************************************************
 * Copyright (c) May 18, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.sdk.test.sdklib.repository;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.io.File;
import java.net.MalformedURLException;
import java.util.Random;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.zend.sdk.test.AbstractTest;
import org.zend.sdklib.SdkException;
import org.zend.sdklib.internal.repository.UserBasedRepositoryLoader;
import org.zend.sdklib.internal.repository.local.FileBasedRepository;
import org.zend.sdklib.manager.RepositoryManager;
import org.zend.sdklib.repository.IRepository;
import org.zend.sdklib.repository.IRepositoryLoader;
import org.zend.sdklib.repository.site.Site;
import org.zend.webapi.core.WebApiException;

public class TestRepositoryManager extends AbstractTest {

	private IRepositoryLoader loader;
	private File file;

	@Before
	public void startup() {
		final String tempDir = System.getProperty("java.io.tmpdir");
		file = new File(tempDir + File.separator + new Random().nextInt());
		file.mkdir();
		loader = new UserBasedRepositoryLoader(file);
	}

	@After
	public void shutdown() {
		loader = null;
		file.deleteOnExit();
	}

	@Test
	public void testCreateManagerWithInvalidTarget() throws WebApiException,
			MalformedURLException, SdkException {
		IRepositoryLoader loader = spy(new UserBasedRepositoryLoader(file));
		when(loader.loadAll()).thenReturn(
				new IRepository[] { new FileBasedRepository("file://tmp/me",
						new File("/tmp/me")) });
		RepositoryManager manager = new RepositoryManager(loader);
		assertTrue(manager.getRepositories().length == 1);

	}

	@Test
	public void testAddDuplicatedTarget() throws SdkException {
		RepositoryManager manager = new RepositoryManager(loader);
		IRepository target = getRepository();
		manager.add(target);
		manager.add(target);
		assertTrue(manager.getRepositories().length == 1);
	}

	@Test
	public void testAddRemoveTarget() throws SdkException {
		RepositoryManager manager = new RepositoryManager(loader);
		IRepository target = getRepository();
		manager.add(target);
		manager.remove(target);
		assertTrue(manager.getRepositories().length == 0);
	}

	private IRepository getRepository() throws SdkException {
		FileBasedRepository repository = spy(new FileBasedRepository(
				"file://tmp/me", new File("/tmp/me")));
		doReturn(new Site()).when(repository).getSite();
		return repository;
	}

}
