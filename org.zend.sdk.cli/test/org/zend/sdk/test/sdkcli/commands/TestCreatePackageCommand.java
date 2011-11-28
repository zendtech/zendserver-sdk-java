package org.zend.sdk.test.sdkcli.commands;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.Random;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.zend.sdk.test.AbstractTest;
import org.zend.sdkcli.CommandFactory;
import org.zend.sdkcli.ICommand;
import org.zend.sdkcli.ParseError;
import org.zend.sdkcli.internal.commands.CommandLine;

public class TestCreatePackageCommand extends AbstractTest {

	public static final String FOLDER = "test/config/apps/";
	private File file;

	@Before
	public void startup() {
		final String tempDir = System.getProperty("java.io.tmpdir");
		file = new File(tempDir + File.separator + new Random().nextInt());
		file.mkdir();
	}

	@After
	public void shutdown() {
		file.deleteOnExit();
	}

	@Test
	public void testByCommandFactory() throws ParseError {
		CommandLine cmdLine = new CommandLine(new String[] { "create",
				"package", "-p", FOLDER + "Project1" });
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
		CommandLine cmdLine = new CommandLine(new String[] { "create",
				"package", "-p", FOLDER + "randomFile" });
		ICommand command = CommandFactory.createCommand(cmdLine);
		assertNotNull(command);
		assertFalse(command.execute(cmdLine));
	}

	@Test
	public void testByCommandFactoryInvalidDestination() throws ParseError {
		CommandLine cmdLine = new CommandLine(new String[] { "create",
				"package", "-p", FOLDER + "Project1", "-d",
				FOLDER + "Project1/deployment.xml" });
		ICommand command = CommandFactory.createCommand(cmdLine);
		assertNotNull(command);
		assertFalse(command.execute(cmdLine));
	}

	@Test
	public void testByCommandFactoryDestination() throws ParseError,
			IOException {
		CommandLine cmdLine = new CommandLine(new String[] { "create",
				"package", "-p", FOLDER + "Project1", "-d",
				file.getCanonicalPath() });
		ICommand command = CommandFactory.createCommand(cmdLine);
		assertNotNull(command);
		assertTrue(command.execute(cmdLine));
		File result = new File(file, "Magento-1.4.1.1.zpk");
		assertTrue(result.exists());
	}
}
