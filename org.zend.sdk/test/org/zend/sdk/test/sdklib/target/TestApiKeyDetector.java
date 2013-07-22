package org.zend.sdk.test.sdklib.target;

import static org.junit.Assert.assertEquals;

import java.net.MalformedURLException;

import org.junit.Test;
import org.zend.sdklib.internal.target.ApiKeyDetector;

public class TestApiKeyDetector {

	@Test
	public void testCreateValidTarget() throws MalformedURLException {
		ApiKeyDetector detector = new ApiKeyDetector("http://test.com") {

			@Override
			public String[] getServerCredentials(String message) {
				return new String[] { "admin", "password" };
			}
		};
		detector.setKey("test");
		assertEquals("test", detector.getKey());
	}

}
