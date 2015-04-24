package org.zend.sdk.test;

import org.junit.Before;
import org.zend.sdklib.logger.ILogger;
import org.zend.sdklib.logger.Log;

public class AbstractTest {

	@Before
	public void startUp() {
		Log.getInstance().registerLogger(new ILogger() {

			@Override
			public void warning(Object message) {
				System.out.println(message);
			}

			@Override
			public void info(Object message) {
				System.out.println(message);
			}

			@Override
			public ILogger getLogger(String creatorName, boolean verbose) {
				return this;
			}

			@Override
			public void error(Object message) {
				System.out.println(message);
			}

			@Override
			public void debug(Object message) {
				System.out.println(message);
			}
		});
	}
}
