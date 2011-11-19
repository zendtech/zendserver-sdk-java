package org.zend.sdk.test.sdkcli.commands;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.zend.sdkcli.ICommand;
import org.zend.sdkcli.ParseError;
import org.zend.sdkcli.internal.commands.CommandLine;
import org.zend.sdklib.logger.Log;

public class TestListRepositoriesCommand extends AbstractRepositoryCommandTest {

	@Test
	public void testByCommandFactory() throws ParseError {
		CommandLine cmdLine = new CommandLine(new String[] { "list",
				"repositories" }, Log.getInstance().getLogger("test"));
		ICommand command = getCommand(cmdLine);
		assertNotNull(command);
		assertTrue(command.execute(cmdLine));
	}

}
