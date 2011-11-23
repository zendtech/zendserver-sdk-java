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
import org.zend.sdkcli.internal.commands.UpdateProjectCommand;

public class TestUpdateProjectCommand extends AbstractTest {

	@Test
	public void testByCommandFactory() throws ParseError, IOException {
		CommandLine cmdLine = new CommandLine(new String[] { "update",
				"project", "-d", getTempFileName() });
		ICommand command = CommandFactory.createCommand(cmdLine);
		assertNotNull(command);
		assertTrue(command.execute(cmdLine));
	}

	@Test
	public void testByConstructor1() throws ParseError, IOException {
		CommandLine cmdLine = new CommandLine(new String[] { "update",
				"project", "-d", getTempFileName() });
		ICommand command = new UpdateProjectCommand();
		assertNotNull(command);
		assertTrue(command.execute(cmdLine));
	}

	@Test
	public void testByConstructor3() throws ParseError, IOException {
		CommandLine cmdLine = new CommandLine(new String[] { "update",
				"project", "-d", getTempFileName() });
		ICommand command = new UpdateProjectCommand();
		assertNotNull(command);

		assertTrue(command.execute(cmdLine));
	}

	public static String getTempFileName() throws IOException {
		File temp = File.createTempFile("temp", "tst");
		temp.delete();
		temp.mkdir();
		return temp.getAbsolutePath();
	}

}
