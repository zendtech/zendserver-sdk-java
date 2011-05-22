/*******************************************************************************
 * Copyright (c) May 18, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.sdk.test.sdkcli.commands;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Random;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.zend.sdk.test.AbstractTest;
import org.zend.sdkcli.CommandFactory;
import org.zend.sdkcli.ParseError;
import org.zend.sdkcli.internal.commands.CommandLine;
import org.zend.sdkcli.internal.commands.ListTargetsCommand;
import org.zend.sdklib.internal.target.UserBasedTargetLoader;
import org.zend.sdklib.internal.target.ZendTarget;
import org.zend.sdklib.logger.Log;
import org.zend.sdklib.manager.TargetsManager;
import org.zend.sdklib.target.ITargetLoader;
import org.zend.sdklib.target.IZendTarget;
import org.zend.webapi.core.WebApiException;

public class TestListTargetsCommand extends AbstractTest {

	private ITargetLoader loader;
	private IZendTarget target;

	private TargetsManager manager;
	private File file;

	@Before
	public void startup() {
		final String tempDir = System.getProperty("java.io.tmpdir");
		file = new File(tempDir + File.separator + new Random().nextInt());
		file.mkdir();
		loader = new UserBasedTargetLoader(file);
		manager = new TargetsManager(loader);
	}

	@After
	public void shutdown() {
		file.deleteOnExit();
	}

	@Test
	public void testNoTargetsAvailable() throws ParseError,
			MalformedURLException {
		CommandLine cmdLine = new CommandLine(new String[] { "list", "targets",
				"-status" }, Log.getInstance().getLogger("AbstractTest"));
		ListTargetsCommand command = spy((ListTargetsCommand) CommandFactory
				.createCommand(cmdLine));
		when(command.getTargetManager()).thenReturn(manager);
		assertTrue(command.execute());
	}

	@Test
	public void testTargetAvailable() throws ParseError, MalformedURLException,
			WebApiException {
		addTarget();
		CommandLine cmdLine = new CommandLine(new String[] { "list", "targets",
				"-status" }, Log.getInstance().getLogger("AbstractTest"));
		ListTargetsCommand command = spy((ListTargetsCommand) CommandFactory
				.createCommand(cmdLine));
		when(command.getTargetManager()).thenReturn(manager);
		assertTrue(command.execute());
	}

	private void addTarget() throws WebApiException {
		target = spy(getTarget());
		when(target.connect()).thenReturn(true);
		manager.add(target);
		assertTrue(manager.list().length != 0);
	}

	private IZendTarget getTarget() {
		try {
			return new ZendTarget("dev4", new URL("http://localhost:10081"),
					"mykey", "43543");
		} catch (MalformedURLException e) {
			// ignore
		}
		return null;
	}

}
