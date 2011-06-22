package org.zend.sdk.test.sdkcli;

import org.junit.Test;
import org.zend.sdkcli.Main;

public class TestMain {

	@Test
	public void testValidCommandLine() {
		// Main.main(new String[] { "help" });
		Main.main(new String[] { "list", "targets", "-s" });
	}



}
