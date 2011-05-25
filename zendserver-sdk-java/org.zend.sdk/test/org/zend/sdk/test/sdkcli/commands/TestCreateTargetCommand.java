package org.zend.sdk.test.sdkcli.commands;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

import java.net.MalformedURLException;

import org.junit.Test;
import org.zend.sdkcli.CommandFactory;
import org.zend.sdkcli.ParseError;
import org.zend.sdkcli.internal.commands.CommandLine;
import org.zend.sdkcli.internal.commands.CreateTargetCommand;
import org.zend.webapi.core.WebApiException;

public class TestCreateTargetCommand extends AbstractTargetCommandTest {

	private String[] validCommand = new String[] { "create", "target", "-t",
			"1", "-key", "mykey", "-secretKey", "123456", "-host",
			"http://test1test" };

	@Test
	public void testExecute() throws ParseError, WebApiException {
		CommandLine cmdLine = new CommandLine(validCommand);
		CreateTargetCommand command = getCommand(cmdLine);
		assertNotNull(command);
		doReturn(getTarget()).when(manager).createTarget(anyString(),
				anyString(), anyString(), anyString());
		assertTrue(command.execute(cmdLine));
	}

	@Test
	public void testExecuteAddFail() throws ParseError, WebApiException {
		CommandLine cmdLine = new CommandLine(validCommand);
		CreateTargetCommand command = getCommand(cmdLine);
		assertNotNull(command);
		doReturn(null).when(manager).createTarget(anyString(), anyString(),
				anyString(), anyString());
		assertFalse(command.execute(cmdLine));
	}

	@Test
	public void testExecuteInvalidUrl() throws ParseError, WebApiException,
			MalformedURLException {
		CommandLine cmdLine = new CommandLine(new String[] { "create",
			"target", "-t", "1", "-key", "mykey", "-secretKey", "123456",
			"-host", "a111:/\test1test" });
		CreateTargetCommand command = getCommand(cmdLine);
		assertNotNull(command);
		assertFalse(command.execute(cmdLine));
	}

	private CreateTargetCommand getCommand(CommandLine cmdLine) throws ParseError {
		CreateTargetCommand command = spy((CreateTargetCommand) CommandFactory
				.createCommand(cmdLine));
		doReturn(manager).when(command).getTargetManager();
		return command;
	}

}
