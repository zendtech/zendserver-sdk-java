package org.zend.sdk.test.sdkcli.commands;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

import org.junit.Test;
import org.mockito.Mockito;
import org.zend.sdkcli.CommandFactory;
import org.zend.sdkcli.ParseError;
import org.zend.sdkcli.internal.commands.CommandLine;
import org.zend.sdkcli.internal.commands.DeleteTargetCommand;
import org.zend.sdklib.target.IZendTarget;
import org.zend.webapi.core.WebApiException;

public class TestDeleteTargetCommand extends AbstractTargetCommandTest {

	private String[] validCommand = new String[] { "delete", "target", "-t",
			"1" };

	@Test
	public void testExecute() throws ParseError, WebApiException {
		CommandLine cmdLine = new CommandLine(validCommand);
		DeleteTargetCommand command = getCommand(cmdLine);
		doReturn(getTarget()).when(manager).getTargetById(Mockito.anyString());
		doReturn(getTarget()).when(manager).remove((IZendTarget) Mockito.anyObject());
		assertTrue(command.execute(cmdLine));
	}

	@Test
	public void testExecuteAddFail() throws ParseError, WebApiException {
		CommandLine cmdLine = new CommandLine(validCommand);
		DeleteTargetCommand command = getCommand(cmdLine);
		doReturn(getTarget()).when(manager).remove((IZendTarget) Mockito.anyObject());
		assertFalse(command.execute(cmdLine));
	}

	private DeleteTargetCommand getCommand(CommandLine cmdLine) throws ParseError {
		DeleteTargetCommand command = spy((DeleteTargetCommand) CommandFactory
				.createCommand(cmdLine));
		doReturn(manager).when(command).getTargetManager();
		return command;
	}

}
