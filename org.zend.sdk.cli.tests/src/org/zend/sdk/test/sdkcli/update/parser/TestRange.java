package org.zend.sdk.test.sdkcli.update.parser;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.zend.sdkcli.update.parser.Range;
import org.zend.sdkcli.update.parser.Version;

public class TestRange {

	@Test
	public void testInRangeIncludeDownUp1() {
		Range range = new Range("[1.0.0,2.0.0]");
		Version version = new Version("2.0.0");
		assertTrue(range.isAllowed(version));
	}

	@Test
	public void testInRangeIncludeDownUp2() {
		Range range = new Range("[1.0.0,2.0.0]");
		Version version = new Version("1.0.0");
		assertTrue(range.isAllowed(version));
	}

	@Test
	public void testUnderRangeIncludeDownUp() {
		Range range = new Range("[1.0.0,2.0.0]");
		Version version = new Version("0.1.1");
		assertFalse(range.isAllowed(version));
	}

	@Test
	public void testOverRangeIncludeDownUp() {
		Range range = new Range("[1.0.0,2.0.0]");
		Version version = new Version("2.0.1");
		assertFalse(range.isAllowed(version));
	}

	@Test
	public void testInRangeIncludeDown() {
		Range range = new Range("[1.0.0,2.0.0)");
		Version version = new Version("1.0.0");
		assertTrue(range.isAllowed(version));
	}

	@Test
	public void testUnderRangeIncludeDown() {
		Range range = new Range("[1.0.0,2.0.0)");
		Version version = new Version("0.1.1");
		assertFalse(range.isAllowed(version));
	}

	@Test
	public void testOverRangeIncludeDown() {
		Range range = new Range("[1.0.0,2.0.0)");
		Version version = new Version("2.0.0");
		assertFalse(range.isAllowed(version));
	}

	@Test
	public void testInRangeIncludeUp() {
		Range range = new Range("(1.0.0,2.0.0]");
		Version version = new Version("1.0.1");
		assertTrue(range.isAllowed(version));
	}

	@Test
	public void testUnderRangeIncludeUp() {
		Range range = new Range("(1.0.0,2.0.0]");
		Version version = new Version("1.0.0");
		assertFalse(range.isAllowed(version));
	}

	@Test
	public void testOverRangeIncludeUp() {
		Range range = new Range("[1.0.0,2.0.0]");
		Version version = new Version("2.0.1");
		assertFalse(range.isAllowed(version));
	}

	@Test
	public void testNoDown1() {
		Range range = new Range("[,2.0.0]");
		Version version = new Version("0.0.1");
		assertTrue(range.isAllowed(version));
	}

	@Test
	public void testNoDown2() {
		Range range = new Range("[,2.0.0]");
		Version version = new Version("5.0.1");
		assertFalse(range.isAllowed(version));
	}

	@Test
	public void testNoUp1() {
		Range range = new Range("[1.0.0,]");
		Version version = new Version("7.0.0");
		assertTrue(range.isAllowed(version));
	}

	@Test
	public void testNoUp2() {
		Range range = new Range("[1.0.0,]");
		Version version = new Version("0.0.0");
		assertFalse(range.isAllowed(version));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testIncorrectVersions() {
		new Range("[1.0.0,2.0.0,3.0.0]");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testIncorrectEnd() {
		new Range("[1.0.0,2.0.0");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testIncorrecStart() {
		new Range("1.0.0,2.0.0]");
	}

}
