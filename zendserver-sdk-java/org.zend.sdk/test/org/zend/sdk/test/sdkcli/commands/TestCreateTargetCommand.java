package org.zend.sdk.test.sdkcli.commands;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

import java.net.MalformedURLException;

import org.junit.Test;
import org.zend.sdkcli.CommandFactory;
import org.zend.sdkcli.ParseError;
import org.zend.sdkcli.internal.commands.CommandLine;
import org.zend.sdkcli.internal.commands.CreateTargetCommand;
import org.zend.sdklib.target.IZendTarget;
import org.zend.webapi.core.WebApiException;

public class TestCreateTargetCommand extends AbstractTargetCommandTest {

	private String[] validCommand = new String[] { "create", "target", "-t",
			"1", "-k", "mykey", "-s", "123456", "-h", "http://test1test" };

	@Test
	public void testExecute() throws ParseError, WebApiException {
		CommandLine cmdLine = new CommandLine(validCommand);
		CreateTargetCommand command = getCommand(cmdLine);
		assertNotNull(command);
		doReturn(getTarget()).when(manager).add(any(IZendTarget.class));
		assertTrue(command.execute(cmdLine));
	}

	@Test
	public void testExecuteNoId() throws ParseError, WebApiException {
		CommandLine cmdLine = new CommandLine(new String[] { "create",
				"target", "-k", "mykey", "-s", "123456", "-h",
				"http://test1test" });
		CreateTargetCommand command = getCommand(cmdLine);
		assertNotNull(command);
		doReturn(getTarget()).when(manager).add(any(IZendTarget.class));
		assertTrue(command.execute(cmdLine));
	}

	@Test
	public void testExecuteAddFail() throws ParseError, WebApiException {
		CommandLine cmdLine = new CommandLine(validCommand);
		CreateTargetCommand command = getCommand(cmdLine);
		assertNotNull(command);
		doReturn(null).when(manager).add(any(IZendTarget.class));
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

	@Test
	public void testExecuteProperties() throws ParseError, WebApiException,
			MalformedURLException {
		CommandLine cmdLine = new CommandLine(new String[] { "create",
				"target", "-t", "1", "-h", "http://test1test", "-p",
				this.getClass().getResource("target.properties").getPath() });
		CreateTargetCommand command = getCommand(cmdLine);
		assertNotNull(command);
		doReturn(getTarget()).when(manager).add(any(IZendTarget.class));
		assertTrue(command.execute(cmdLine));
	}

	@Test
	public void testExecuteInvalidPropertiesFile() throws ParseError,
			WebApiException, MalformedURLException {
		CommandLine cmdLine = new CommandLine(new String[] { "create",
				"target", "-t", "1", "-h", "http://test1test", "-p",
				"nofilename" });
		CreateTargetCommand command = getCommand(cmdLine);
		assertNotNull(command);
		doReturn(getTarget()).when(manager).add(any(IZendTarget.class));
		assertFalse(command.execute(cmdLine));
	}

	private CreateTargetCommand getCommand(CommandLine cmdLine)
			throws ParseError {
		CreateTargetCommand command = spy((CreateTargetCommand) CommandFactory
				.createCommand(cmdLine));
		doReturn(manager).when(command).getTargetManager();
		return command;
	}

}
