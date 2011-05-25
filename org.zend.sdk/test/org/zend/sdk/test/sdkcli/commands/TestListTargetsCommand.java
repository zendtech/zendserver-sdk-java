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

import java.net.MalformedURLException;

import org.junit.Test;
import org.zend.sdkcli.CommandFactory;
import org.zend.sdkcli.ParseError;
import org.zend.sdkcli.internal.commands.CommandLine;
import org.zend.sdkcli.internal.commands.ListTargetsCommand;
import org.zend.sdklib.logger.Log;
import org.zend.sdklib.target.IZendTarget;
import org.zend.webapi.core.WebApiException;

public class TestListTargetsCommand extends AbstractTargetCommandTest {

	@Test
	public void testNoTargetsAvailable() throws ParseError,
			MalformedURLException {
		CommandLine cmdLine = new CommandLine(new String[] { "list", "targets",
				"-status" }, Log.getInstance().getLogger("AbstractTest"));
		ListTargetsCommand command = spy((ListTargetsCommand) CommandFactory
				.createCommand(cmdLine));
		when(command.getTargetManager()).thenReturn(manager);
		assertTrue(command.execute(cmdLine));
	}

	@Test
	public void testTargetAvailable() throws ParseError, MalformedURLException,
			WebApiException {
		IZendTarget target = getTarget();
		manager.add(target);
		assertTrue(manager.getTargets().length != 0);
		CommandLine cmdLine = new CommandLine(new String[] { "list", "targets",
				"-status" }, Log.getInstance().getLogger("AbstractTest"));
		ListTargetsCommand command = spy((ListTargetsCommand) CommandFactory
				.createCommand(cmdLine));
		when(command.getTargetManager()).thenReturn(manager);
		assertTrue(command.execute(cmdLine));
	}

}
