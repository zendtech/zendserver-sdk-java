package org.zend.webapi.test.core;

import java.net.URL;

import org.junit.Test;
import org.zend.webapi.core.configuration.ClientConfiguration;

public class TestClientConfiguration {

	@Test(expected = IllegalArgumentException.class)
	public void testNullUserAgent() {
		ClientConfiguration client = new ClientConfiguration();
		client.setUserAgent(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testNullHost() {
		new ClientConfiguration((URL) null);
	}

}
