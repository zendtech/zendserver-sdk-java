package org.zend.sdk.test.sdkcli.commands;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.junit.Test;
import org.zend.sdk.test.AbstractTest;
import org.zend.sdkcli.ParseError;
import org.zend.sdkcli.internal.commands.CommandLine;
import org.zend.sdklib.logger.Log;

public class TestCommandLine extends AbstractTest {

	@Test
	public void testValidCommandLine() throws ParseError {
		CommandLine cmdLine = getLine("create project -n testName");
		Options options = new Options();
		Option listOption = new Option("n", true, "");
		options.addOption(listOption);
		cmdLine.parse(options);
		assertEquals("project", cmdLine.getDirectObject());
		assertEquals("create", cmdLine.getVerb());
		assertEquals("testName", cmdLine.getParameterValue("n"));
	}

	@Test
	public void testValidCommandLineMultipleValues() throws ParseError {
		CommandLine cmdLine = getLine("add files -list aaa bbb");
		Options options = new Options();
		Option listOption = new Option("list", true, "");
		options.addOption(listOption);
		cmdLine.parse(options);
		assertEquals("files", cmdLine.getDirectObject());
		assertEquals("add", cmdLine.getVerb());
		String[] expected = new String[] { "aaa", "bbb" };
		String[] actual = cmdLine.getParameterValues("list");
		for (int i = 0; i < actual.length; i++) {
			assertEquals(expected[i], actual[i]);
		}
	}

	@Test(expected = ParseError.class)
	public void testInvalidCommandLine() throws ParseError {
		CommandLine cmdLine = getLine("create project -incorrectParam");
		Options options = new Options();
		cmdLine.parse(options);
	}

	@Test(expected = ParseError.class)
	public void testInvalidCommandLine1() throws ParseError {
		CommandLine cmdLine = getLine("create project");
		Options options = new Options();
		final Option option = new Option("n", true, "");
		option.setRequired(true);
		options.addOption(option);
		cmdLine.parse(options);
	}

	@Test
	public void testCommandLogger() throws ParseError {
		CommandLine cmdLine = getLine("create project -n testName");
		assertNotNull(cmdLine.getLog());
		cmdLine = new CommandLine(new String[] { "test" }, Log.getInstance()
				.getLogger(TestCommandLine.class.getName()));
		assertNotNull(cmdLine.getLog());
	}

	@Test
	public void testGetParameter() throws ParseError {
		CommandLine cmdLine = getLine("create -a asd");
		Options options = new Options();
		final Option option = new Option("a", true, "");
		option.setRequired(true);
		options.addOption(option);
		cmdLine.parse(options);
		assertTrue(cmdLine.hasOption("a"));
		assertFalse(cmdLine.hasOption("b"));
		assertEquals("asd", cmdLine.getParameterValue("a"));
		assertNull(cmdLine.getParameterValue("b"));
		assertEquals("create", cmdLine.getArgument(0));
		assertNull(cmdLine.getArgument(22));
		assertNull(cmdLine.getArgument(-1));
	}

}
