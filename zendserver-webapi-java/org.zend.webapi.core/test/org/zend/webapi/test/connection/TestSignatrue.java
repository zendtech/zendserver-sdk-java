package org.zend.webapi.test.connection;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.zend.webapi.core.WebApiException;
import org.zend.webapi.core.connection.signature.ISignature;
import org.zend.webapi.internal.core.connection.auth.signature.Signature;
import org.zend.webapi.internal.core.connection.auth.signature.SignatureException;

public class TestSignatrue {
	// static
	String host = "http://zscm.local:10081";
	String userAgent = "Zend_Http_Client/1.10";
	String key = "9dc7f8c5ac43bb2ab36120861b4aeda8f9bb6c521e124360fd5821ef279fd9c7";

	// dynamic params
	String requestUri = "/ZendServer/Api/findTheFish";
	final String date = "Sun, 11 Jul 2010 13:16:10 GMT";

	private ISignature s;

	@Before
	public void setup() {
		s = new Signature(host, userAgent, key);
	}

	@Test
	public void testCreate() throws WebApiException {
		final String encode = s.encode(requestUri, date);
		Assert.assertNotNull(encode);
	}

	@Test
	public void testNotZeroLength() throws WebApiException {
		final String encode = s.encode(requestUri, date);
		Assert.assertTrue(encode.length() > 0);
	}

	/**
	 * Equivalent to: <? echo hash_hmac('sha256',
	 * 'zscm.local:10081:/ZendServer/Api/findTheFish:Zend_H ttp_Client/1.10:Sun,
	 * 11 Jul 2010 13:16:10 GMT', '9dc7f8c5ac43bb2ab36120861b4aeda
	 * 8f9bb6c521e124360fd5821ef279fd9c7', false);
	 * 
	 * 785be59b7728b1bfd6495d610271c5d47ff0737775b09191daeb5a728c2d97c0
	 * 
	 * @throws WebApiException
	 */
	@Test
	public void testEquals() throws WebApiException {
		final String encode = s.encode(requestUri, date);
		Assert.assertEquals(
				"785be59b7728b1bfd6495d610271c5d47ff0737775b09191daeb5a728c2d97c0",
				encode);
	}

	@Test(expected = SignatureException.class)
	public void testMalformedURL() throws WebApiException {
		Signature signature = new Signature("", userAgent, key);
		signature.encode(requestUri, date);
	}

}
