package org.zend.sdk.test;

import java.io.File;

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

	protected boolean delete(File file) {
		if (file == null || !file.exists()) {
			return true;
		}
		if (file.isDirectory()) {
			String[] children = file.list();
			for (int i = 0; i < children.length; i++) {
				boolean result = delete(new File(file, children[i]));
				if (!result) {
					return false;
				}
			}
		}
		return file.delete();
	}

}
