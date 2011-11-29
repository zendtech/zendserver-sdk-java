package org.zend.sdk.test.sdkcli.commands;

import static org.junit.Assert.assertEquals;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.junit.Test;
import org.zend.sdk.test.AbstractTest;
import org.zend.sdkcli.ParseError;
import org.zend.sdkcli.internal.commands.CommandLine;

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

}
