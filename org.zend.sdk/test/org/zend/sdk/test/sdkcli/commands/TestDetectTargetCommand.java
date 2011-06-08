package org.zend.sdk.test.sdkcli.commands;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.spy;

import java.io.IOException;

import org.junit.Test;
import org.zend.sdkcli.CommandFactory;
import org.zend.sdkcli.ParseError;
import org.zend.sdkcli.internal.commands.CommandLine;
import org.zend.sdkcli.internal.commands.DetectTargetCommand;
import org.zend.sdklib.target.IZendTarget;
import org.zend.webapi.core.WebApiException;

public class TestDetectTargetCommand extends AbstractTargetCommandTest {

	private String[] validCommand = new String[] { "detect", "target" };
	private String[] validCommandOnlyAdd = new String[] { "detect", "target", "-a" };

	@Test
	public void testExecute() throws ParseError, WebApiException, IOException {
		CommandLine cmdLine = new CommandLine(validCommand);
		DetectTargetCommand command = getCommand(cmdLine);
		assertNotNull(command);
		manager.add(getTarget());
		assertTrue(command.execute(cmdLine));
	}

	@Test
	public void testExecuteOnlyAdd() throws ParseError, WebApiException, IOException {
		CommandLine cmdLine = new CommandLine(validCommandOnlyAdd);
		DetectTargetCommand command = getCommand(cmdLine);
		assertNotNull(command);
		manager.add(getTarget());
		assertTrue(command.execute(cmdLine));
	}

	@Test
	public void testDetectLocalhostIOExceptionWindows() throws WebApiException,
			IOException, ParseError {
		CommandLine cmdLine = new CommandLine(validCommand);
		DetectTargetCommand command = getCommand(cmdLine);
		IZendTarget target = getTarget();
		manager.add(target);
		assertTrue(manager.getTargets().length == 1);
		doThrow(new IOException("testException")).when(manager)
				.detectLocalhostTarget(anyString(), anyString(), anyBoolean());
		String backupValue = System.getProperty("os.name");
		System.setProperty("os.name", "windows");
		assertFalse(command.execute(cmdLine));
		System.setProperty("os.name", backupValue);
	}

	@Test
	public void testDetectLocalhostIOExceptionLinux() throws WebApiException,
			IOException, ParseError {
		CommandLine cmdLine = new CommandLine(validCommand);
		DetectTargetCommand command = getCommand(cmdLine);
		IZendTarget target = getTarget();
		manager.add(target);
		assertTrue(manager.getTargets().length == 1);
		doThrow(new IOException("testException")).when(manager)
				.detectLocalhostTarget(anyString(), anyString(), anyBoolean());
		String backupValue = System.getProperty("os.name");
		System.setProperty("os.name", "linux");
		assertFalse(command.execute(cmdLine));
		System.setProperty("os.name", backupValue);
	}

	private DetectTargetCommand getCommand(CommandLine cmdLine)
			throws ParseError {
		DetectTargetCommand command = spy((DetectTargetCommand) CommandFactory
				.createCommand(cmdLine));
		doReturn(manager).when(command).getTargetManager();
		return command;
	}

}
