package org.zend.sdk.test.sdkcli;

import static org.junit.Assert.*;

import org.junit.Test;
import org.zend.sdkcli.Main;

public class TestMain {

	@Test
	public void test() {
		Main.main(new String[] { "create", "project" } );
	}

}
