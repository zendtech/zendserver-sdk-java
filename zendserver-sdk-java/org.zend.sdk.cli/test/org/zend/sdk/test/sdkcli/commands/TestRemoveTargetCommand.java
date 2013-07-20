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
import org.zend.sdkcli.internal.commands.RemoveTargetCommand;
import org.zend.sdklib.target.IZendTarget;
import org.zend.sdklib.target.LicenseExpiredException;
import org.zend.webapi.core.WebApiException;

public class TestRemoveTargetCommand extends AbstractTargetCommandTest {

	@Test
	public void testExecute() throws ParseError, WebApiException,
			LicenseExpiredException {
		CommandLine cmdLine = getLine("remove target -t 1");
		RemoveTargetCommand command = getCommand(cmdLine);
		doReturn(getTarget()).when(manager).getTargetById(Mockito.anyString());
		doReturn(getTarget()).when(manager).remove(
				(IZendTarget) Mockito.anyObject());
		assertTrue(command.execute(cmdLine));
	}

	@Test
	public void testExecuteAddFail() throws ParseError, WebApiException,
			LicenseExpiredException {
		CommandLine cmdLine = getLine("remove target -t 1");
		RemoveTargetCommand command = getCommand(cmdLine);
		doReturn(getTarget()).when(manager).remove(
				(IZendTarget) Mockito.anyObject());
		assertFalse(command.execute(cmdLine));
	}

	@Test
	public void testExecuteFail() throws ParseError, WebApiException,
			LicenseExpiredException {
		CommandLine cmdLine = getLine("remove target -t 1");
		RemoveTargetCommand command = getCommand(cmdLine);
		doReturn(getTarget()).when(manager).getTargetById(Mockito.anyString());
		doReturn(null).when(manager).remove((IZendTarget) Mockito.anyObject());
		assertFalse(command.execute(cmdLine));
	}

	private RemoveTargetCommand getCommand(CommandLine cmdLine)
			throws ParseError {
		RemoveTargetCommand command = spy((RemoveTargetCommand) CommandFactory
				.createCommand(cmdLine));
		doReturn(manager).when(command).getTargetManager();
		return command;
	}

}
