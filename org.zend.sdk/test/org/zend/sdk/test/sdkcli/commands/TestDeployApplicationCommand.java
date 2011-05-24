package org.zend.sdk.test.sdkcli.commands;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.spy;

import java.io.IOException;

import org.junit.Test;
import org.zend.sdk.test.AbstractTest;
import org.zend.sdkcli.CommandFactory;
import org.zend.sdkcli.ICommand;
import org.zend.sdkcli.ParseError;
import org.zend.sdkcli.internal.commands.CommandLine;
import org.zend.sdkcli.internal.commands.DeployApplicationCommand;
import org.zend.webapi.core.WebApiException;

public class TestDeployApplicationCommand extends AbstractTest {

	private String[] validCommand = new String[] { "deploy", "application",
			"-project", "c:/myProject", "-baseUrl", "http://myhost.com/aaa",
			"-target", "11" };

	@Test
	public void testByCommandFactory() throws ParseError {
		CommandLine cmdLine = new CommandLine(validCommand);
		ICommand command = CommandFactory.createCommand(cmdLine);
		assertNotNull(command);
		assertTrue(command instanceof DeployApplicationCommand);
		assertTrue(command.execute());
	}

	@Test
	public void testExecute() throws ParseError, WebApiException, IOException {
		DeployApplicationCommand command = getCommand(validCommand);
		assertNotNull(command);
		assertTrue(command.execute());
	}

	private DeployApplicationCommand getCommand(String[] args)
			throws ParseError {
		CommandLine cmdLine = new CommandLine(args);
		DeployApplicationCommand command = spy((DeployApplicationCommand) CommandFactory
				.createCommand(cmdLine));
		return command;
	}

}
