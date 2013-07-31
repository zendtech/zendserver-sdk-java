package org.zend.sdk.test.sdkcli.commands;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.junit.Test;
import org.mockito.Mockito;
import org.zend.sdk.test.AbstractTest;
import org.zend.sdkcli.CommandFactory;
import org.zend.sdkcli.ICommand;
import org.zend.sdkcli.ParseError;
import org.zend.sdkcli.internal.commands.AbstractCommand;
import org.zend.sdkcli.internal.commands.CommandLine;

public class TestCreatePackageCommand extends AbstractTest {

	@Test
	public void testByCommandFactory() throws ParseError {
		CommandLine cmdLine = getLine("create package -p " + FOLDER
				+ "Project1");
		ICommand command = CommandFactory.createCommand(cmdLine);
		assertNotNull(command);
		assertTrue(command.execute(cmdLine));
		File pacakge = new File("Magento-1.4.1.1.zpk");
		if (pacakge.exists()) {
			delete(pacakge);
		}
	}

	@Test
	public void testByCommandFactoryInvalidPath() throws ParseError {
		CommandLine cmdLine = getLine("create package -p " + FOLDER
				+ "randomFile");
		ICommand command = CommandFactory.createCommand(cmdLine);
		assertNotNull(command);
		assertFalse(command.execute(cmdLine));
	}

	@Test
	public void testByCommandFactoryInvalidDestination() throws ParseError {
		CommandLine cmdLine = getLine("create package -p " + FOLDER
				+ "Project1 -d" + FOLDER + "Project1/deployment.xml");
		ICommand command = CommandFactory.createCommand(cmdLine);
		assertNotNull(command);
		assertFalse(command.execute(cmdLine));
	}

	@Test
	public void testByCommandFactoryDestination() throws ParseError,
			IOException {
		CommandLine cmdLine = getLine("create package -p" + FOLDER
				+ "Project1 -d " + file.getCanonicalPath());
		ICommand command = CommandFactory.createCommand(cmdLine);
		assertNotNull(command);
		assertTrue(command.execute(cmdLine));
		File result = new File(file, "Magento-1.4.1.1.zpk");
		assertTrue(result.exists());
	}

	@Test
	public void testNoArgsIncorrectProject() throws ParseError {
		CommandLine cmdLine = getLine("create package");
		AbstractCommand command = Mockito.spy((AbstractCommand) CommandFactory
				.createCommand(cmdLine));
		Mockito.doReturn(file.getAbsolutePath()).when(command)
				.getCurrentDirectory();
		assertNotNull(command);
		assertFalse(command.execute(cmdLine));
	}
}
