package org.zend.sdk.test.sdkcli.commands;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.junit.Test;
import org.zend.sdk.test.AbstractTest;
import org.zend.sdkcli.CommandFactory;
import org.zend.sdkcli.ICommand;
import org.zend.sdkcli.ParseError;
import org.zend.sdkcli.internal.commands.CommandLine;
import org.zend.sdkcli.internal.commands.CreateProjectCommand;

public class TestCreateProjectCommand extends AbstractTest {

	@Test
	public void testByCommandFactory() throws ParseError, IOException {
		CommandLine cmdLine = new CommandLine(new String[] { "create",
				"project", "-n", "testName", "-d", getTempFileName(), "-t",
				"simple" });
		ICommand command = CommandFactory.createCommand(cmdLine);
		assertNotNull(command);
		assertTrue(command.execute(cmdLine));
	}

	@Test
	public void testByConstructor1() throws ParseError, IOException {
		CommandLine cmdLine = new CommandLine(new String[] { "create",
				"project", "-n", "testName", "-d", getTempFileName(), "-t",
				"simple", "-t", "simple" });
		ICommand command = new CreateProjectCommand();
		assertNotNull(command);
		assertTrue(command.execute(cmdLine));
	}
	
	@Test
	public void testByConstructor2() throws ParseError, IOException {
		CommandLine cmdLine = new CommandLine(new String[] { "create",
				"project", "-n", "testName", "-d", getTempFileName(),
				"-s", "all", "-t", "simple" });
		ICommand command = new CreateProjectCommand();
		assertNotNull(command);
		assertTrue(command.execute(cmdLine));
	}
	
	public static String getTempFileName() throws IOException {
		File temp = File.createTempFile("temp", "tst");
		temp.delete();
		return temp.getAbsolutePath();
	}
}
