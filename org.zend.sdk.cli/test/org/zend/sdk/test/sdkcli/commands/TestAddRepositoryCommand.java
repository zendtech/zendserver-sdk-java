package org.zend.sdk.test.sdkcli.commands;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Test;
import org.zend.sdkcli.ICommand;
import org.zend.sdkcli.ParseError;
import org.zend.sdkcli.internal.commands.CommandLine;

public class TestAddRepositoryCommand extends AbstractRepositoryCommandTest {

	@Test
	public void testByCommandFactory() throws ParseError {
		File repositoryFolder = new File(FOLDER + "repository");
		String reposiotryUrl = "file:/" + repositoryFolder.getAbsolutePath();
		CommandLine line = getLine("add repository -u " + reposiotryUrl
				+ " -n testRepo");
		ICommand command = getCommand(line);
		assertNotNull(command);
		assertTrue(command.execute(line));
	}

}
