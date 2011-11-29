package org.zend.sdk.test.sdkcli.commands;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyVararg;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.junit.Test;
import org.mockito.Mockito;
import org.zend.sdk.test.AbstractWebApiTest;
import org.zend.sdkcli.CommandFactory;
import org.zend.sdkcli.ParseError;
import org.zend.sdkcli.internal.commands.CommandLine;
import org.zend.sdkcli.internal.commands.ListApplicationsCommand;
import org.zend.webapi.core.WebApiException;
import org.zend.webapi.core.connection.data.ApplicationsList;
import org.zend.webapi.core.connection.data.IResponseData;
import org.zend.webapi.internal.core.connection.auth.signature.SignatureException;

public class TestListApplicationsCommand extends AbstractWebApiTest {

	@Test
	public void testExecute() throws ParseError, WebApiException, IOException {
		CommandLine cmdLine = getLine("list applications -t 0");
		ListApplicationsCommand command = getCommand(cmdLine);
		when(client.applicationGetStatus((String[]) anyVararg())).thenReturn(
				(ApplicationsList) getResponseData("applicationGetStatus",
						IResponseData.ResponseType.APPLICATIONS_LIST));
		assertTrue(command.execute(cmdLine));
	}

	@Test
	public void testExecuteNoTargetAvailable() throws ParseError,
			WebApiException,
			IOException {
		CommandLine cmdLine = getLine("list applications");
		ListApplicationsCommand command = getCommand(cmdLine);
		doReturn(null).when(command).getDefaultTargetId();
		assertFalse(command.execute(cmdLine));
	}

	@Test
	public void testExecuteIncorrectTargetId() throws ParseError,
			WebApiException,
			IOException {
		CommandLine cmdLine = getLine("list applications -t nonexisitngId");
		ListApplicationsCommand command = getCommand(cmdLine);
		assertFalse(command.execute(cmdLine));
	}

	@Test
	public void testExecuteNoApps() throws ParseError, WebApiException,
			IOException {
		CommandLine cmdLine = getLine("list applications -t 0");
		ListApplicationsCommand command = getCommand(cmdLine);
		ApplicationsList mockList = Mockito.mock(ApplicationsList.class);
		when(mockList.getApplicationsInfo()).thenReturn(null);
		when(client.applicationGetStatus((String[]) anyVararg())).thenReturn(
				mockList);
		assertTrue(command.execute(cmdLine));
	}

	@Test
	public void testExecuteTargetDisconnected() throws ParseError,
			WebApiException, IOException {
		CommandLine cmdLine = getLine("list applications -t 0");
		ListApplicationsCommand command = getCommand(cmdLine);
		when(client.applicationGetStatus()).thenThrow(
				new SignatureException("testError"));
		assertFalse(command.execute(cmdLine));
	}

	private ListApplicationsCommand getCommand(CommandLine cmdLine)
			throws ParseError {
		ListApplicationsCommand command = spy((ListApplicationsCommand) CommandFactory
				.createCommand(cmdLine));
		assertNotNull(command);
		doReturn(application).when(command).getApplication();
		return command;
	}

}
