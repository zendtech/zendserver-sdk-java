package org.zend.sdk.test.sdkcli.commands;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Test;
import org.mockito.Mockito;
import org.zend.sdk.test.AbstractTest;
import org.zend.sdkcli.CommandFactory;
import org.zend.sdkcli.ICommand;
import org.zend.sdkcli.ParseError;
import org.zend.sdkcli.internal.commands.AbstractCommand;
import org.zend.sdkcli.internal.commands.CommandLine;
import org.zend.sdkcli.internal.commands.CreateProjectCommand;

public class TestCreateProjectCommand extends AbstractTest {

	@Test
	public void testByCommandFactory() throws ParseError, IOException {
		CommandLine cmdLine = getLine("create project -n testName -d "
				+ getTempFileName() + " -t simple");
		ICommand command = CommandFactory.createCommand(cmdLine);
		assertNotNull(command);
		assertTrue(command.execute(cmdLine));
	}

	@Test
	public void testByConstructor1() throws ParseError, IOException {
		CommandLine cmdLine = getLine("create project -n testName -d "
				+ getTempFileName() + " -t simple -t simple");
		ICommand command = new CreateProjectCommand();
		assertNotNull(command);
		assertTrue(command.execute(cmdLine));
	}
	
	@Test
	public void testByConstructor2() throws ParseError, IOException {
		CommandLine cmdLine = getLine("create project -n testName -d "
				+ getTempFileName() + " -s all -t simple");
		ICommand command = new CreateProjectCommand();
		assertNotNull(command);
		assertTrue(command.execute(cmdLine));
	}
	
	@Test
	public void testNoDestination() throws ParseError, IOException {
		CommandLine cmdLine = getLine("create project -n testName");
		AbstractCommand command = Mockito.spy((AbstractCommand) CommandFactory
				.createCommand(cmdLine));
		Mockito.doReturn(file.getAbsolutePath()).when(command)
				.getCurrentDirectory();
		assertNotNull(command);
		assertTrue(command.execute(cmdLine));
	}

	@Test
	public void testInvalidTemplate() throws ParseError, IOException {
		CommandLine cmdLine = getLine("create project -n testName -d "
				+ getTempFileName() + " -t incorrect");
		ICommand command = new CreateProjectCommand();
		assertNotNull(command);
		assertFalse(command.execute(cmdLine));
	}

	private String getTempFileName() throws IOException {
		file.delete();
		return file.getAbsolutePath();
	}
}
