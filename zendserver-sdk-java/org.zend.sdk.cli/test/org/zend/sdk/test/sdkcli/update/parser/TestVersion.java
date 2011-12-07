package org.zend.sdk.test.sdkcli.update.parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.zend.sdkcli.update.parser.Version;

public class TestVersion {

	@Test
	public void testValidVersion() {
		Version v = new Version("0.0.0");
		assertEquals(0, v.getValue());
		v = new Version("0.0.1");
		assertEquals(1, v.getValue());
		v = new Version("0.1.0");
		assertEquals(256, v.getValue());
		v = new Version("1.0.0");
		assertEquals(65536, v.getValue());
		v = new Version("1.1.1");
		assertEquals(65793, v.getValue());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testInvalidVersion() {
		new Version("0.1");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testNullVersion() {
		new Version("0.1");
	}

	@Test
	public void testEqual() {
		Version v1 = new Version("0.1.2");
		Version v2 = new Version("0.1.2");
		assertEquals(0, v1.compareTo(v2));
	}

	@Test
	public void testGreaterMajor() {
		Version v1 = new Version("2.0.0");
		Version v2 = new Version("1.4.3");
		assertTrue(v1.compareTo(v2) > 0);
	}

	@Test
	public void testGreaterMinor() {
		Version v1 = new Version("1.5.0");
		Version v2 = new Version("1.4.3");
		assertTrue(v1.compareTo(v2) > 0);
	}

	@Test
	public void testGreaterBuild() {
		Version v1 = new Version("1.4.7");
		Version v2 = new Version("1.4.2");
		assertTrue(v1.compareTo(v2) > 0);
	}

	@Test
	public void testLessMajor() {
		Version v1 = new Version("0.4.3");
		Version v2 = new Version("1.0.0");
		assertTrue(v1.compareTo(v2) < 0);
	}

	@Test
	public void testLessMinor() {
		Version v1 = new Version("1.4.3");
		Version v2 = new Version("1.5.0");
		assertTrue(v1.compareTo(v2) < 0);
	}

	@Test
	public void testLessBuild() {
		Version v1 = new Version("1.4.2");
		Version v2 = new Version("1.4.7");
		assertTrue(v1.compareTo(v2) < 0);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testNegativePart() {
		new Version("1.-4.2");
	}

}
