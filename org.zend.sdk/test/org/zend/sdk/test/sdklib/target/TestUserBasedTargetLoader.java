/*******************************************************************************
 * Copyright (c) May 18, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.sdk.test.sdklib.target;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Random;

import org.junit.Test;
import org.zend.sdklib.internal.target.UserBasedTargetLoader;
import org.zend.sdklib.internal.target.ZendTarget;

public class TestUserBasedTargetLoader {

	@Test
	public void testAdd() throws MalformedURLException {
		final String tempDir = System.getProperty("java.io.tmpdir");
		File file = new File(tempDir + File.separator + new Random().nextInt());
		file.mkdir();

		UserBasedTargetLoader loader = new UserBasedTargetLoader(file);
		final ZendTarget target = new ZendTarget("dev3", new URL(
				"http://localhost:10081"), "mykey", "43543");
		loader.add(target);

		file.deleteOnExit();
	}

	@Test
	public void testRemove() throws MalformedURLException {
		final String tempDir = System.getProperty("java.io.tmpdir");
		File file = new File(tempDir + File.separator + new Random().nextInt());
		file.mkdir();

		UserBasedTargetLoader loader = new UserBasedTargetLoader(file);
		final ZendTarget target = new ZendTarget("dev3", new URL(
				"http://localhost:10081"), "mykey", "43543");
		loader.add(target);
		loader.remove(target);

		file.deleteOnExit();
	}

	@Test
	public void testList() throws MalformedURLException {
		final String tempDir = System.getProperty("java.io.tmpdir");
		File file = new File(tempDir + File.separator + new Random().nextInt());
		file.mkdir();

		UserBasedTargetLoader loader = new UserBasedTargetLoader(file);
		final ZendTarget target = new ZendTarget("dev3", new URL(
				"http://localhost:10081"), "mykey", "43543");
		loader.add(target);
		assertTrue(loader.loadAll().length > 0);

		file.deleteOnExit();
	}

	@Test
	public void testUser() throws MalformedURLException {
		UserBasedTargetLoader loader = new UserBasedTargetLoader();
		final ZendTarget target = new ZendTarget("dev3", new URL(
				"http://localhost:10081"), "mykey", "43543");
		loader.add(target);
		loader.remove(target);
		assertTrue(loader.loadAll().length == 0);
	}
}
