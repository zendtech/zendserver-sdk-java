package org.zend.sdk.test.sdklib.library;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;

import org.junit.Test;
import org.zend.sdkcli.ParseError;
import org.zend.sdklib.library.StatusCode;

public class TestStatusCode {

	@Test
	public void testValidStatusCode() throws ParseError {
		StatusCode code = StatusCode.byName("error");
		assertNotNull(code);
		assertNotSame(StatusCode.UNKNOWN, code);
		assertSame(StatusCode.ERROR, code);
	}

	@Test
	public void testUnknownStatusCode() throws ParseError {
		StatusCode code = StatusCode.byName("1234");
		assertNotNull(code);
		assertSame(StatusCode.UNKNOWN, code);
	}

	@Test
	public void testNullStatusCode() throws ParseError {
		StatusCode code = StatusCode.byName(null);
		assertNotNull(code);
		assertSame(StatusCode.UNKNOWN, code);
	}

}
