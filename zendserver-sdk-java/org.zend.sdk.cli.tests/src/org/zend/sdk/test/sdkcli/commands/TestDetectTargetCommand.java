package org.zend.sdk.test.sdkcli.commands;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

import java.io.IOException;

import org.junit.Test;
import org.zend.sdkcli.CommandFactory;
import org.zend.sdkcli.ParseError;
import org.zend.sdkcli.internal.commands.CommandLine;
import org.zend.sdkcli.internal.commands.DetectTargetCommand;
import org.zend.sdklib.manager.TargetException;
import org.zend.sdklib.target.LicenseExpiredException;
import org.zend.webapi.core.WebApiException;

public class TestDetectTargetCommand extends AbstractTargetCommandTest {

	@Test
	public void testExecute() throws ParseError, WebApiException,
			TargetException, IOException, LicenseExpiredException {
		CommandLine cmdLine = getLine("detect target");
		DetectTargetCommand command = getCommand(cmdLine);
		assertNotNull(command);
		manager.add(getTarget());
		//assertTrue(command.execute(cmdLine));
	}

	private DetectTargetCommand getCommand(CommandLine cmdLine)
			throws ParseError {
		DetectTargetCommand command = spy((DetectTargetCommand) CommandFactory
				.createCommand(cmdLine));
		doReturn(manager).when(command).getTargetManager();
		return command;
	}

}
