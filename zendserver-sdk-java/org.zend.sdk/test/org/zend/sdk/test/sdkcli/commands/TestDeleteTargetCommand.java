package org.zend.sdk.test.sdkcli.commands;

import static org.junit.Assert.*;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

import java.net.MalformedURLException;

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
		DeleteTargetCommand command = getCommand(validCommand);
		doReturn(getTarget()).when(manager).getTargetById(Mockito.anyString());
		doReturn(getTarget()).when(manager).remove((IZendTarget) Mockito.anyObject());
		assertTrue(command.execute());
	}

	@Test
	public void testExecuteAddFail() throws ParseError, WebApiException {
		DeleteTargetCommand command = getCommand(validCommand);
		doReturn(getTarget()).when(manager).remove((IZendTarget) Mockito.anyObject());
		assertFalse(command.execute());
	}

	private DeleteTargetCommand getCommand(String[] args) throws ParseError {
		CommandLine cmdLine = new CommandLine(args);
		DeleteTargetCommand command = spy((DeleteTargetCommand) CommandFactory
				.createCommand(cmdLine));
		doReturn(manager).when(command).getTargetManager();
		return command;
	}

}
