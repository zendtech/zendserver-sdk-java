package org.zend.sdk.test.sdklib.logger;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.zend.sdklib.logger.ILogger;
import org.zend.sdklib.logger.Log;

public class TestLogger {

	private ExampleLogger logger;

	private class ExampleLogger implements ILogger {

		private Object lastLog;

		public Object getLastLog() {
			return lastLog;
		}

		@Override
		public void debug(Object message) {
			lastLog = message;
		}

		@Override
		public void info(Object message) {
			lastLog = message;
		}

		@Override
		public void warning(Object message) {
			lastLog = message;
		}

		@Override
		public void error(Object message) {
			lastLog = message;
		}

		@Override
		public ILogger getLogger(String creatorName, boolean verbose) {
			return this;
		}
	}

	@Before
	public void initLogger() {
		logger = new ExampleLogger();
		Log.getInstance().registerLogger(logger);
	}

	@Test
	public void testDebug() {
		ILogger log = Log.getInstance().getLogger("");
		Exception error = new Exception("debug");
		log.debug(error);
		Object result = logger.getLastLog();
		checkValidResultMessage(error, result);
	}

	@Test
	public void testInfo() {
		ILogger log = Log.getInstance().getLogger("");
		Exception error = new Exception("info");
		log.info(error);
		Object result = logger.getLastLog();
		checkValidResultMessage(error, result);
	}

	@Test
	public void testWarning() {
		ILogger log = Log.getInstance().getLogger("");
		Exception error = new Exception("warning");
		log.warning(error);
		Object result = logger.getLastLog();
		checkValidResultMessage(error, result);
	}

	@Test
	public void testError() {
		ILogger log = Log.getInstance().getLogger("");
		Exception error = new Exception("error");
		log.error(error);
		Object result = logger.getLastLog();
		checkValidResultMessage(error, result);
	}

	@Test(expected = IllegalStateException.class)
	public void testNullLogger() {
		Log.getInstance().registerLogger(null);
		Log.getInstance().getLogger("");
	}

	private void checkValidResultMessage(Exception expected, Object actual) {
		assertTrue(actual instanceof Exception);
		Exception actualError = (Exception) actual;
		assertSame(expected.getMessage(), actualError.getMessage());
	}

}
