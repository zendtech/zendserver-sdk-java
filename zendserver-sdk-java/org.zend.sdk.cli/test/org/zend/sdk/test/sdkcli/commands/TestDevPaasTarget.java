package org.zend.sdk.test.sdkcli.commands;

import java.io.IOException;

import org.junit.Test;
import org.zend.sdk.test.AbstractTest;
import org.zend.sdkcli.ParseError;
import org.zend.sdkcli.internal.commands.AddTargetCommand;
import org.zend.sdkcli.internal.commands.CommandLine;
import org.zend.sdkcli.internal.commands.DetectTargetCommand;
import org.zend.sdklib.SdkException;

public class TestDevPaasTarget extends AbstractTest {

	@Test
	public void testCreateTargetHostWithPort() throws SdkException,
			IOException, ParseError {
		CommandLine cl = getLine("add target -d ganoro:ganoro");
		AddTargetCommand add = new AddTargetCommand();
		final boolean execute = add.execute(cl);
	}
	
	@Test
	public void testAddTarget() throws SdkException, ParseError {
		CommandLine cl = getLine("detect target");
		DetectTargetCommand add = new DetectTargetCommand();
		final boolean execute = add.execute(cl);
	}
}
