package org.zend.sdk.test.sdkcli.commands;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Test;
import org.zend.sdk.test.AbstractTest;
import org.zend.sdkcli.CommandFactory;
import org.zend.sdkcli.ICommand;
import org.zend.sdkcli.ParseError;
import org.zend.sdkcli.internal.commands.CommandLine;
import org.zend.sdkcli.internal.commands.UpdateProjectCommand;

public class TestUpdateProjectCommand extends AbstractTest {

	@Test
	public void testByCommandFactory() throws ParseError, IOException {
		CommandLine cmdLine = getLine("update project -d "
				+ file.getAbsolutePath());
		ICommand command = CommandFactory.createCommand(cmdLine);
		assertNotNull(command);
		assertTrue(command.execute(cmdLine));
	}

	@Test
	public void testByConstructor1() throws ParseError, IOException {
		CommandLine cmdLine = getLine("update project -d "
				+ file.getAbsolutePath());
		ICommand command = new UpdateProjectCommand();
		assertNotNull(command);
		assertTrue(command.execute(cmdLine));
	}

	@Test
	public void testByConstructor3() throws ParseError, IOException {
		CommandLine cmdLine = getLine("update project -d "
				+ file.getAbsolutePath());
		ICommand command = new UpdateProjectCommand();
		assertNotNull(command);
		assertTrue(command.execute(cmdLine));
	}

}
