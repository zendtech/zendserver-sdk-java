package org.zend.sdk.test.sdkcli.commands;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

import java.net.MalformedURLException;

import org.junit.Test;
import org.zend.sdkcli.CommandFactory;
import org.zend.sdkcli.ParseError;
import org.zend.sdkcli.internal.commands.AddTargetCommand;
import org.zend.sdkcli.internal.commands.CommandLine;
import org.zend.sdklib.manager.TargetException;
import org.zend.sdklib.target.IZendTarget;
import org.zend.sdklib.target.LicenseExpiredException;
import org.zend.webapi.core.WebApiException;

public class TestAddTargetCommand extends AbstractTargetCommandTest {

	@Test
	public void testExecute() throws ParseError, WebApiException, TargetException, LicenseExpiredException {
		CommandLine cmdLine = getLine("add target -t 1 -k mykey -s 123456 -h http://test1test");
		AddTargetCommand command = getCommand(cmdLine);
		assertNotNull(command);
		doReturn(getTarget()).when(manager).add(any(IZendTarget.class));
		assertTrue(command.execute(cmdLine));
	}

	@Test
	public void testExecuteNoId() throws ParseError, WebApiException,
			TargetException, LicenseExpiredException {
		CommandLine cmdLine = getLine("add target -k mykey -s 123456 -h http://test1test");
		AddTargetCommand command = getCommand(cmdLine);
		assertNotNull(command);
		doReturn(getTarget()).when(command).testTargetConnection(
				any(IZendTarget.class));
		assertTrue(command.execute(cmdLine));
	}

	@Test
	public void testExecuteAddFail() throws ParseError, TargetException,
			LicenseExpiredException, WebApiException {
		CommandLine cmdLine = getLine("add target -t 1 -k mykey -s 123456 -h http://test1test");
		AddTargetCommand command = getCommand(cmdLine);
		assertNotNull(command);
		doReturn(null).when(command).testTargetConnection(
				any(IZendTarget.class));
		assertFalse(command.execute(cmdLine));
	}

	@Test
	public void testExecuteInvalidUrl() throws ParseError, WebApiException,
			MalformedURLException, LicenseExpiredException {
		CommandLine cmdLine = getLine("add target -t 1 -key mykey -secretKey 123456 -host a111:/\test1test");
		AddTargetCommand command = getCommand(cmdLine);
		assertNotNull(command);
		assertFalse(command.execute(cmdLine));
	}

	@Test
	public void testExecuteProperties() throws ParseError, WebApiException,
			TargetException, MalformedURLException, LicenseExpiredException {
		CommandLine cmdLine = getLine("add target -t 1 -h http://test1test -p "
				+ "src" + this.getClass().getResource("target.properties").getPath());
		AddTargetCommand command = getCommand(cmdLine);
		assertNotNull(command);
		doReturn(getTarget()).when(command).testTargetConnection(
				any(IZendTarget.class));
		assertTrue(command.execute(cmdLine));
	}

	@Test
	public void testExecuteInvalidPropertiesFile() throws ParseError,
			WebApiException, TargetException, MalformedURLException,
			LicenseExpiredException {
		CommandLine cmdLine = getLine("add target -t 1 -h http://test1test -p nofilename");
		AddTargetCommand command = getCommand(cmdLine);
		assertNotNull(command);
		assertFalse(command.execute(cmdLine));
	}

	private AddTargetCommand getCommand(CommandLine cmdLine) throws ParseError {
		AddTargetCommand command = spy((AddTargetCommand) CommandFactory
				.createCommand(cmdLine));
		doReturn(manager).when(command).getTargetManager();
		return command;
	}

}
