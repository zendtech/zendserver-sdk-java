package org.zend.sdk.test.sdkcli.commands;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.junit.Test;
import org.zend.sdkcli.CommandFactory;
import org.zend.sdkcli.ParseError;
import org.zend.sdkcli.internal.commands.CommandLine;
import org.zend.sdkcli.internal.commands.ListApplicationsCommand;
import org.zend.webapi.core.WebApiException;
import org.zend.webapi.core.connection.data.ApplicationsList;
import org.zend.webapi.core.connection.data.IResponseData;
import org.zend.webapi.internal.core.connection.auth.signature.SignatureException;

public class TestListApplicationsCommand extends AbstractAppCommandTest {

	private String[] validCommand = new String[] { "list", "applications",
			"-t", "0" };

	@Test
	public void testExecute() throws ParseError, WebApiException, IOException {
		ListApplicationsCommand command = getCommand(validCommand);
		assertNotNull(command);
		doReturn(application).when(command).getApplication();
		when(client.applicationGetStatus()).thenReturn(
				(ApplicationsList) getResponseData("applicationGetStatus",
						IResponseData.ResponseType.APPLICATIONS_LIST));
		assertTrue(command.execute());
	}

	@Test
	public void testExecuteTargetDisconnected() throws ParseError,
			WebApiException, IOException {
		ListApplicationsCommand command = getCommand(validCommand);
		assertNotNull(command);
		doReturn(application).when(command).getApplication();
		when(client.applicationGetStatus()).thenThrow(
				new SignatureException("testError"));
		assertFalse(command.execute());
	}

	private ListApplicationsCommand getCommand(String[] args) throws ParseError {
		CommandLine cmdLine = new CommandLine(args);
		ListApplicationsCommand command = spy((ListApplicationsCommand) CommandFactory
				.createCommand(cmdLine));
		return command;
	}

}
