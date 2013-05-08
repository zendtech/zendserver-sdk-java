/*******************************************************************************
 * Copyright (c) 2013 Zend Technologies.
 * All rights reserved. This program and the accompanying materials
 * are the copyright of Zend Technologies and is protected under
 * copyright laws of the United States.
 * You must not copy, adapt or redistribute this document for 
 * any use.
 *******************************************************************************/
package org.zend.php.library.core.tests;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.zend.php.library.core.LibraryVersion;
import org.zend.php.library.core.LibraryVersionRange;

/**
 * @author Wojciech Galanciak, 2013
 *
 */
public class LibraryVersionRangeTests {

	@Test
	public void testInRangeDownUp() {
		LibraryVersionRange range = LibraryVersionRange
				.getRange(">1.0.0,<3.0.0");
		assertTrue(range.isInRange(LibraryVersion.byName("1.2.3")));
		assertTrue(range.isInRange(LibraryVersion.byName("1.1.1")));
		assertTrue(range.isInRange(LibraryVersion.byName("1.2")));
		assertTrue(range.isInRange(LibraryVersion.byName("2")));
	}

	@Test
	public void testNotInRangeDownUp() {
		LibraryVersionRange range = LibraryVersionRange
				.getRange(">2.0.0,<4.0.0");
		assertFalse(range.isInRange(LibraryVersion.byName("1.2.3")));
		assertFalse(range.isInRange(LibraryVersion.byName("1.0.1")));
		assertFalse(range.isInRange(LibraryVersion.byName("2.0.0")));
		assertFalse(range.isInRange(LibraryVersion.byName("4.0.0")));
		assertFalse(range.isInRange(LibraryVersion.byName("7.0.0")));
		assertFalse(range.isInRange(LibraryVersion.byName("2.0")));
		assertFalse(range.isInRange(LibraryVersion.byName("2")));
	}

	@Test
	public void testInRangeAny() {
		LibraryVersionRange range = LibraryVersionRange.getRange("*");
		assertFalse(range.isInRange(LibraryVersion.byName("1.2.3")));
		assertFalse(range.isInRange(LibraryVersion.byName("1.0.1")));
		assertFalse(range.isInRange(LibraryVersion.byName("2.0.0")));
		assertFalse(range.isInRange(LibraryVersion.byName("4.0.0")));
		assertFalse(range.isInRange(LibraryVersion.byName("7.0.0")));
		assertFalse(range.isInRange(LibraryVersion.byName("2.0")));
		assertFalse(range.isInRange(LibraryVersion.byName("2")));
	}

	@Test
	public void testtestNotInRangeDownUp2() {
		LibraryVersionRange range = LibraryVersionRange
				.getRange(">2.0.*,<4.0.0");
		assertFalse(range.isInRange(LibraryVersion.byName("1.2.3")));
		assertFalse(range.isInRange(LibraryVersion.byName("1.0.1")));
		assertFalse(range.isInRange(LibraryVersion.byName("2.0.0")));
		assertFalse(range.isInRange(LibraryVersion.byName("4.0.0")));
		assertFalse(range.isInRange(LibraryVersion.byName("7.0.0")));
		assertFalse(range.isInRange(LibraryVersion.byName("2.0")));
		assertFalse(range.isInRange(LibraryVersion.byName("2")));
	}

	@Test
	public void testtestNotInRangeDownUp3() {
		LibraryVersionRange range = LibraryVersionRange.getRange(">2.0.*,<4.*");
		assertFalse(range.isInRange(LibraryVersion.byName("1.2.3")));
		assertFalse(range.isInRange(LibraryVersion.byName("1.0.1")));
		assertFalse(range.isInRange(LibraryVersion.byName("2.0.0")));
		assertFalse(range.isInRange(LibraryVersion.byName("4.0.0")));
		assertFalse(range.isInRange(LibraryVersion.byName("7.0.0")));
		assertFalse(range.isInRange(LibraryVersion.byName("2.0")));
		assertFalse(range.isInRange(LibraryVersion.byName("2")));
	}

	@Test
	public void testInRangeDown() {
		LibraryVersionRange range = LibraryVersionRange.getRange(">2.0.0");
		assertTrue(range.isInRange(LibraryVersion.byName("4.0.0")));
		assertTrue(range.isInRange(LibraryVersion.byName("7.0.0")));
		assertTrue(range.isInRange(LibraryVersion.byName("2.1")));
		assertTrue(range.isInRange(LibraryVersion.byName("3")));
	}

	@Test
	public void testInRangeDown2() {
		LibraryVersionRange range = LibraryVersionRange.getRange(">2.*");
		assertTrue(range.isInRange(LibraryVersion.byName("4.0.0")));
		assertTrue(range.isInRange(LibraryVersion.byName("7.0.0")));
		assertTrue(range.isInRange(LibraryVersion.byName("3")));
	}

	@Test
	public void testNotInRangeDown() {
		LibraryVersionRange range = LibraryVersionRange.getRange(">2.0.7");
		assertFalse(range.isInRange(LibraryVersion.byName("1.0.0")));
		assertFalse(range.isInRange(LibraryVersion.byName("2.0.0")));
		assertFalse(range.isInRange(LibraryVersion.byName("2.0")));
		assertFalse(range.isInRange(LibraryVersion.byName("2")));
	}

	@Test
	public void testInRangeUp() {
		LibraryVersionRange range = LibraryVersionRange.getRange("<5.0.0");
		assertTrue(range.isInRange(LibraryVersion.byName("1.0.0")));
		assertTrue(range.isInRange(LibraryVersion.byName("4.0.0")));
		assertTrue(range.isInRange(LibraryVersion.byName("2.0")));
		assertTrue(range.isInRange(LibraryVersion.byName("2")));
	}

	@Test
	public void testNotInRangeUp() {
		LibraryVersionRange range = LibraryVersionRange.getRange("<2.0.0");
		assertFalse(range.isInRange(LibraryVersion.byName("2.0.0")));
		assertFalse(range.isInRange(LibraryVersion.byName("5.0.0")));
		assertFalse(range.isInRange(LibraryVersion.byName("2.0")));
		assertFalse(range.isInRange(LibraryVersion.byName("2")));
	}

}
