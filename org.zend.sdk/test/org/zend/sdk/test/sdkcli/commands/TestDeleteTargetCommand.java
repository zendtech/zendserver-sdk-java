package org.zend.sdk.test.sdkcli.commands;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

import org.junit.Test;
import org.zend.sdkcli.CommandFactory;
import org.zend.sdkcli.ICommand;
import org.zend.sdkcli.ParseError;
import org.zend.sdkcli.internal.commands.CommandLine;
import org.zend.sdkcli.internal.commands.CreateTargetCommand;
import org.zend.sdkcli.internal.commands.DeleteTargetCommand;
import org.zend.webapi.core.WebApiException;

public class TestDeleteTargetCommand extends AbstractTargetCommandTest {

	private String[] createTargetCommand = new String[] { "create", "target", "-t",
			"1", "-key", "mykey", "-secretKey", "123456", "-host",
			"http://test1test" };
	
	private String[] deleteTargetCommand = new String[] { "delete", "target", "-t",
		"1" };

	@Test
	public void testExecute() throws ParseError, WebApiException {
		CreateTargetCommand createTargetCmd = (CreateTargetCommand) getCommand(createTargetCommand);
		doReturn(manager).when(createTargetCmd).getTargetManager();
		assertNotNull(createTargetCmd);
		doReturn(getTarget()).when(manager).createTarget(anyString(),
			anyString(), anyString(), anyString());
		assertTrue(createTargetCmd.execute());
		
		DeleteTargetCommand deleteTargetCmd = (DeleteTargetCommand) getCommand(deleteTargetCommand);
		assertNotNull(deleteTargetCmd);
		assertTrue(deleteTargetCmd.execute());
		
		
	}

	private ICommand getCommand(String[] args) throws ParseError {
		CommandLine cmdLine = new CommandLine(args);
		ICommand command = spy(CommandFactory
				.createCommand(cmdLine));
		return command;
	}

}
