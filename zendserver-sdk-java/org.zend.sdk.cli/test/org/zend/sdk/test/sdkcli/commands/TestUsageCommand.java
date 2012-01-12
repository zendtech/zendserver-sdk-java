package org.zend.sdk.test.sdkcli.commands;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.zend.sdk.test.AbstractTest;
import org.zend.sdkcli.CommandFactory;
import org.zend.sdkcli.ICommand;
import org.zend.sdkcli.ParseError;
import org.zend.sdkcli.internal.commands.CommandLine;
import org.zend.sdkcli.internal.commands.UsageCommand;

public class TestUsageCommand extends AbstractTest {

	@Test
	public void testByCommandFactory() throws ParseError {
		CommandLine cmdLine = getLine("help");
		ICommand command = CommandFactory.createCommand(cmdLine);
		assertNotNull(command);
		assertTrue(command.execute(cmdLine));
	}

	@Test
	public void testByCommandFactory1() throws ParseError {
		CommandLine cmdLine = getLine("help create project");
		ICommand command = CommandFactory.createCommand(cmdLine);
		assertNotNull(command);
		assertTrue(command.execute(cmdLine));
	}
	
	@Test
	public void testByConstructor1() throws ParseError {
		CommandLine cmdLine = getLine("help");
		ICommand command = new UsageCommand();
		assertNotNull(command);
		assertTrue(command.execute(cmdLine));
	}
}
