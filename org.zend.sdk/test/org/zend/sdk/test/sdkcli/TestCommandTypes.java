package org.zend.sdk.test.sdkcli;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;

import org.junit.Test;
import org.zend.sdkcli.CommandTypes;
import org.zend.sdkcli.ParseError;
import org.zend.sdkcli.internal.commands.CommandLine;

public class TestCommandTypes {

	@Test
	public void testValidCommandType() throws ParseError {
		CommandLine line = new CommandLine(new String[] { "create", "project" });
		CommandTypes type = CommandTypes.byCommandLine(line);
		assertNotNull(type);
		assertNotSame(CommandTypes.UNKNOWN, type);
		assertSame("create", type.getVerb());
		assertSame("project", type.getDirectObject());
	}

	@Test
	public void testInvalidCommandType() throws ParseError {
		CommandLine line = new CommandLine(new String[] { "random", "123" });
		CommandTypes type = CommandTypes.byCommandLine(line);
		assertNotNull(type);
		assertSame(CommandTypes.UNKNOWN, type);
	}

	@Test
	public void testInvalidCommandType2() throws ParseError {
		CommandLine line = new CommandLine(new String[] { "random" });
		CommandTypes type = CommandTypes.byCommandLine(line);
		assertNotNull(type);
		assertSame(CommandTypes.UNKNOWN, type);
	}

	@Test
	public void testInvalidCommandType3() throws ParseError {
		CommandLine line = new CommandLine(new String[] {});
		CommandTypes type = CommandTypes.byCommandLine(line);
		assertNotNull(type);
		assertSame(CommandTypes.UNKNOWN, type);
	}

}
