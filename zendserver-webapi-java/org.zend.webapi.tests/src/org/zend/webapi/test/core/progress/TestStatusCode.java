package org.zend.webapi.test.core.progress;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;

import org.junit.Test;
import org.zend.webapi.core.progress.StatusCode;

public class TestStatusCode {

	@Test
	public void testValidStatusCode() {
		StatusCode code = StatusCode.byName("error");
		assertNotNull(code);
		assertNotSame(StatusCode.UNKNOWN, code);
		assertSame(StatusCode.ERROR, code);
	}

	@Test
	public void testUnknownStatusCode() {
		StatusCode code = StatusCode.byName("1234");
		assertNotNull(code);
		assertSame(StatusCode.UNKNOWN, code);
	}

	@Test
	public void testNullStatusCode() {
		StatusCode code = StatusCode.byName(null);
		assertNotNull(code);
		assertSame(StatusCode.UNKNOWN, code);
	}

}
