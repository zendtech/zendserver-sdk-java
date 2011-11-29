package org.zend.sdk.test.sdkcli.commands;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.zend.sdkcli.ICommand;
import org.zend.sdkcli.ParseError;
import org.zend.sdkcli.internal.commands.CommandLine;

public class TestListRepositoriesCommand extends AbstractRepositoryCommandTest {

	@Test
	public void testByCommandFactory() throws ParseError {
		CommandLine cmdLine = getLine("list repositories");
		ICommand command = getCommand(cmdLine);
		assertNotNull(command);
		assertTrue(command.execute(cmdLine));
	}

}
