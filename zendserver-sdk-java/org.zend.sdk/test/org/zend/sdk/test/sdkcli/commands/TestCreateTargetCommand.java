package org.zend.sdk.test.sdkcli.commands;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.spy;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Random;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.zend.sdk.test.AbstractTest;
import org.zend.sdkcli.CommandFactory;
import org.zend.sdkcli.ParseError;
import org.zend.sdkcli.internal.commands.CommandLine;
import org.zend.sdkcli.internal.commands.CreateTargetCommand;
import org.zend.sdklib.internal.target.UserBasedTargetLoader;
import org.zend.sdklib.internal.target.ZendTarget;
import org.zend.sdklib.manager.TargetsManager;
import org.zend.sdklib.target.ITargetLoader;
import org.zend.sdklib.target.IZendTarget;
import org.zend.webapi.core.WebApiException;
import org.zend.webapi.internal.core.connection.auth.signature.SignatureException;

public class TestCreateTargetCommand extends AbstractTest {

	private String[] validCommand = new String[] { "create", "target", "-t",
			"1", "-key", "mykey", "-secret", "123456", "-host",
			"http://test1test" };

	private ITargetLoader loader;
	private TargetsManager manager;
	private File file;

	@Before
	public void startup() {
		final String tempDir = System.getProperty("java.io.tmpdir");
		file = new File(tempDir + File.separator + new Random().nextInt());
		file.mkdir();
		loader = new UserBasedTargetLoader(file);
		manager = spy(new TargetsManager(loader));
	}

	@After
	public void shutdown() {
		file.deleteOnExit();
	}

	@Test
	public void testExecute() throws ParseError, WebApiException {
		CreateTargetCommand command = getCommand(validCommand);
		assertNotNull(command);
		doReturn(getTarget()).when(manager).add(Mockito.any(IZendTarget.class));
		assertTrue(command.execute());
	}

	@Test
	public void testExecuteAddFail() throws ParseError, WebApiException {
		CreateTargetCommand command = getCommand(validCommand);
		assertNotNull(command);
		doReturn(null).when(manager).add(Mockito.any(IZendTarget.class));
		assertFalse(command.execute());
	}

	@Test
	public void testExecuteInvalidUrl() throws ParseError, WebApiException,
			MalformedURLException {
		CreateTargetCommand command = getCommand(new String[] { "create",
				"target", "-t", "1", "-key", "mykey", "-secret", "123456",
				"-host", "a111:/\test1test" });
		assertNotNull(command);
		assertFalse(command.execute());
	}

	@Test
	public void testExecuteManagerThrowException() throws ParseError,
			WebApiException {
		CreateTargetCommand command = getCommand(validCommand);
		assertNotNull(command);
		doThrow(new SignatureException("testError")).when(manager).add(
				Mockito.any(IZendTarget.class));
		assertFalse(command.execute());
	}

	private CreateTargetCommand getCommand(String[] args) throws ParseError {
		CommandLine cmdLine = new CommandLine(args);
		CreateTargetCommand command = spy((CreateTargetCommand) CommandFactory
				.createCommand(cmdLine));
		doReturn(manager).when(command).getTargetManager();
		return command;
	}

	private IZendTarget getTarget() throws WebApiException {
		IZendTarget target = null;
		try {
			target = spy(new ZendTarget("dev4", new URL("http://test1test"),
					"mykey", "123456"));
			doReturn(true).when(target).connect();
		} catch (MalformedURLException e) {
			// ignore
		}
		return target;
	}
}
