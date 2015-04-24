/*******************************************************************************
 * Copyright (c) May 18, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.sdk.test.sdklib.target;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Random;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.zend.sdklib.internal.target.UserBasedTargetLoader;
import org.zend.sdklib.internal.target.ZendTarget;
import org.zend.sdklib.target.IZendTarget;

public class TestUserBasedTargetLoader {

	private File file;
	UserBasedTargetLoader loader;

	@Before
	public void startup() {
		final String tempDir = System.getProperty("java.io.tmpdir");
		file = new File(tempDir + File.separator + new Random().nextInt());
		file.mkdir();
		loader = new UserBasedTargetLoader(file);
	}

	@After
	public void shutdown() {
		file.deleteOnExit();
	}

	@Test
	public void testAdd() throws MalformedURLException {
		final ZendTarget target = new ZendTarget("dev3", new URL(
				"http://localhost:10081"), "mykey", "43543");
		loader.add(target);
		assertTrue(loader.loadAll().length == 1);
	}

	@Test
	public void testRemove() throws MalformedURLException {
		final ZendTarget target = new ZendTarget("dev3", new URL(
				"http://localhost:10081"), "mykey", "43543");
		loader.add(target);
		loader.remove(target);
		assertTrue(loader.loadAll().length == 0);
	}

	@Test
	public void testUpdate() throws MalformedURLException {
		final ZendTarget target = new ZendTarget("dev3", new URL(
				"http://localhost:10081"), "mykey", "43543");
		loader.add(target);
		target.setKey("newKey");
		IZendTarget result = loader.update(target);
		assertTrue(loader.loadAll().length == 1);
		assertEquals("newKey", result.getKey());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testUpdateNull() throws MalformedURLException {
		final ZendTarget target = new ZendTarget("dev3", new URL(
				"http://localhost:10081"), "mykey", "43543");
		loader.add(target);
		loader.update(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testUpdateNoTarget() throws MalformedURLException {
		final ZendTarget target = new ZendTarget("dev3", new URL(
				"http://localhost:10081"), "mykey", "43543");
		loader.update(target);
	}

	@Test
	public void testList() throws MalformedURLException {
		final ZendTarget target = new ZendTarget("dev3", new URL(
				"http://localhost:10081"), "mykey", "43543");
		loader.add(target);
		final ZendTarget target2 = new ZendTarget("dev4", new URL(
				"http://localhost:10082"), "mykey", "12121");
		loader.add(target2);
		assertTrue(loader.loadAll().length == 2);
	}

	@Test
	public void testUser() throws MalformedURLException {
		final ZendTarget target = new ZendTarget("dev3", new URL(
				"http://localhost:10081"), "mykey", "43543");
		loader.add(target);
		loader.remove(target);
		assertTrue(loader.loadAll().length == 0);
	}

	@Test(expected = IllegalStateException.class)
	public void testUserDirNotExist() throws MalformedURLException {
		File file = new File(String.valueOf(new Random().nextInt()));
		new UserBasedTargetLoader(file);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testNullTarget() throws MalformedURLException {
		loader.add(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testTargetExists() throws MalformedURLException {
		final ZendTarget target = new ZendTarget("dev3", new URL(
				"http://localhost:10081"), "mykey", "43543");
		loader.add(target);
		loader.add(target);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testRemoveNonexistTarget() throws MalformedURLException {
		final ZendTarget target = new ZendTarget("dev3", new URL(
				"http://localhost:10081"), "mykey", "43543");
		loader.add(target);
		delete(file);
		loader.remove(target);
	}

	private boolean delete(File file) {
		if (file.isDirectory()) {
			String[] children = file.list();
			for (int i = 0; i < children.length; i++) {
				boolean result = delete(new File(file, children[i]));
				if (!result) {
					return false;
				}
			}
		}
		return file.delete();
	}

}
