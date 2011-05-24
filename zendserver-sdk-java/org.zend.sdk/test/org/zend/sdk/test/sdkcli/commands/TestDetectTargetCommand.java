package org.zend.sdk.test.sdkcli.commands;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
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

	private String[] validCommand = new String[] { "detect", "target", "-t",
			"1" };

	@Test
	public void testExecute() throws ParseError, WebApiException, IOException {
		DetectTargetCommand command = getCommand(validCommand);
		assertNotNull(command);
		IZendTarget target = getTarget();
		doReturn(target).when(manager).add(any(IZendTarget.class));
		assertTrue(command.execute());
	}

	private DetectTargetCommand getCommand(String[] args) throws ParseError {
		CommandLine cmdLine = new CommandLine(args);
		DetectTargetCommand command = spy((DetectTargetCommand) CommandFactory
				.createCommand(cmdLine));
		doReturn(manager).when(command).getTargetManager();
		return command;
	}

}
