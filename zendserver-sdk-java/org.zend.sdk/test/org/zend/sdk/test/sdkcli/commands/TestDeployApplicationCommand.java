package org.zend.sdk.test.sdkcli.commands;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.junit.Test;
import org.zend.sdkcli.CommandFactory;
import org.zend.sdkcli.ParseError;
import org.zend.sdkcli.internal.commands.CommandLine;
import org.zend.sdkcli.internal.commands.DeployApplicationCommand;
import org.zend.webapi.core.WebApiException;
import org.zend.webapi.core.connection.data.ApplicationInfo;
import org.zend.webapi.core.connection.data.IResponseData;
import org.zend.webapi.internal.core.connection.auth.signature.SignatureException;

public class TestDeployApplicationCommand extends AbstractAppCommandTest {

	private String[] validCommand = new String[] { "deploy", "application",
			"-path", FOLDER + "test-1.0.0.zpk", "-baseUrl",
			"http://myhost.com/aaa", "-target", "0", "-name", "myApp" };

	@Test
	public void testExecute() throws WebApiException, IOException, ParseError {
		DeployApplicationCommand command = getCommand(validCommand);
		assertNotNull(command);
		doReturn(application).when(command).getApplication();
		when(
				client.applicationDeploy(any(File.class), anyString(),
						anyBoolean(), any(Map.class), anyString(),
						anyBoolean(), anyBoolean())).thenReturn(
				(ApplicationInfo) getResponseData("applicationDeploy",
						IResponseData.ResponseType.APPLICATION_INFO));
		assertTrue(command.execute());
	}

	@Test
	public void testExecuteTargetDisconnected() throws ParseError,
			WebApiException, IOException {
		DeployApplicationCommand command = getCommand(validCommand);
		assertNotNull(command);
		doReturn(application).when(command).getApplication();
		when(client.applicationGetStatus()).thenThrow(
				new SignatureException("testError"));
		assertFalse(command.execute());
	}

	private DeployApplicationCommand getCommand(String[] args)
			throws ParseError {
		CommandLine cmdLine = new CommandLine(args);
		DeployApplicationCommand command = spy((DeployApplicationCommand) CommandFactory
				.createCommand(cmdLine));
		return command;
	}

}
