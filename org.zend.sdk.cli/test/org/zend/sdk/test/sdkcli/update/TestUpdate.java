package org.zend.sdk.test.sdkcli.update;

import org.junit.Test;
import org.zend.sdkcli.update.Update;

public class TestUpdate {

	@Test
	public void testUpdate() {
		Update.main(new String[] { "/Users/galAnonim/zend/workspaces/devenv/org.zend.sdk.cli/build/distributions/zend-sdk-cli-0.0.22" });
	}

}
