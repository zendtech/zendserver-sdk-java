package org.zend.sdk.test.sdkcli.commands;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

import java.io.IOException;

import org.junit.Test;
import org.zend.sdkcli.CommandFactory;
import org.zend.sdkcli.ICommand;
import org.zend.sdkcli.ParseError;
import org.zend.sdkcli.internal.commands.CommandLine;
import org.zend.sdkcli.internal.commands.ListApplicationsCommand;
import org.zend.sdklib.target.IZendTarget;
import org.zend.webapi.core.WebApiException;

public class TestListApplicationsCommand extends AbstractTargetCommandTest {

	private String[] validCommand = new String[] { "list", "applications",
			"-t", "1" };

	private String[] validCommandAppIds = new String[] { "list",
			"applications", "-t", "1", "-appId", "1", "2" };

	@Test
	public void testByCommandFactory() throws ParseError {
		CommandLine cmdLine = new CommandLine(validCommand);
		ICommand command = CommandFactory.createCommand(cmdLine);
		assertNotNull(command);
		assertTrue(command instanceof ListApplicationsCommand);
		assertTrue(command.execute());
	}

	@Test
	public void testExecute() throws ParseError, WebApiException, IOException {
		ListApplicationsCommand command = getCommand(validCommand);
		assertNotNull(command);
		IZendTarget target = getTarget();
		doReturn(target).when(manager).add(any(IZendTarget.class));
		assertTrue(command.execute());
	}

	@Test
	public void testExecuteAppIds() throws ParseError, WebApiException,
			IOException {
		ListApplicationsCommand command = getCommand(validCommandAppIds);
		assertNotNull(command);
		IZendTarget target = getTarget();
		doReturn(target).when(manager).add(any(IZendTarget.class));
		assertTrue(command.execute());
	}

	private ListApplicationsCommand getCommand(String[] args) throws ParseError {
		CommandLine cmdLine = new CommandLine(args);
		ListApplicationsCommand command = spy((ListApplicationsCommand) CommandFactory
				.createCommand(cmdLine));
		doReturn(manager).when(command).getTargetManager();
		return command;
	}

}
