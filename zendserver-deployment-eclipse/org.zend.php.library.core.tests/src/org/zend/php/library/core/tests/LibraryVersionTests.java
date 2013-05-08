/*******************************************************************************
 * Copyright (c) 2013 Zend Technologies.
 * All rights reserved. This program and the accompanying materials
 * are the copyright of Zend Technologies and is protected under
 * copyright laws of the United States.
 * You must not copy, adapt or redistribute this document for 
 * any use.
 *******************************************************************************/
package org.zend.php.library.core.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.Test;
import org.zend.php.library.core.LibraryVersion;
import org.zend.php.library.core.LibraryVersion.Suffix;

/**
 * @author Wojciech Galanciak, 2013
 *
 */
public class LibraryVersionTests {

	@Test
	public void testParse() {
		assertFalse(LibraryVersion.byName("1.0.0").equals(
				LibraryVersion.UNKNOWN));
		assertFalse(LibraryVersion.byName("v1.0.0").equals(
				LibraryVersion.UNKNOWN));
		assertFalse(LibraryVersion.byName("V1.0.0").equals(
				LibraryVersion.UNKNOWN));
		assertFalse(LibraryVersion.byName("1.0").equals(LibraryVersion.UNKNOWN));
		assertFalse(LibraryVersion.byName("v1.0")
				.equals(LibraryVersion.UNKNOWN));
		assertFalse(LibraryVersion.byName("V1.0")
				.equals(LibraryVersion.UNKNOWN));
		assertFalse(LibraryVersion.byName("1.0.0-beta").equals(
				LibraryVersion.UNKNOWN));
		assertFalse(LibraryVersion.byName("1.0.0-BETA").equals(
				LibraryVersion.UNKNOWN));
		assertFalse(LibraryVersion.byName("v1.0.0-beta").equals(
				LibraryVersion.UNKNOWN));
		assertFalse(LibraryVersion.byName("v1.0.0-BETA").equals(
				LibraryVersion.UNKNOWN));
		assertFalse(LibraryVersion.byName("1.0-beta").equals(
				LibraryVersion.UNKNOWN));
		assertFalse(LibraryVersion.byName("1.0-BETA").equals(
				LibraryVersion.UNKNOWN));
		assertFalse(LibraryVersion.byName("v1.0-beta").equals(
				LibraryVersion.UNKNOWN));
		assertFalse(LibraryVersion.byName("v1.0-BETA").equals(
				LibraryVersion.UNKNOWN));
	}

	@Test
	public void testParse1() {
		LibraryVersion v = LibraryVersion.byName("1.2.3");
		assertEquals(1, v.getMajor());
		assertEquals(2, v.getMinor());
		assertEquals(3, v.getBuild());
		assertEquals(Suffix.NONE, v.getSuffix());
		assertEquals(-1, v.getSuffixVersion());
	}

	@Test
	public void testParse2() {
		LibraryVersion v = LibraryVersion.byName("v1.2.3");
		assertEquals(1, v.getMajor());
		assertEquals(2, v.getMinor());
		assertEquals(3, v.getBuild());
		assertEquals(Suffix.NONE, v.getSuffix());
		assertEquals(-1, v.getSuffixVersion());
	}

	@Test
	public void testParse3() {
		LibraryVersion v = LibraryVersion.byName("1.2");
		assertEquals(1, v.getMajor());
		assertEquals(2, v.getMinor());
		assertEquals(-1, v.getBuild());
		assertEquals(Suffix.NONE, v.getSuffix());
		assertEquals(-1, v.getSuffixVersion());
	}

	@Test
	public void testParse4() {
		LibraryVersion v = LibraryVersion.byName("v1.2");
		assertEquals(1, v.getMajor());
		assertEquals(2, v.getMinor());
		assertEquals(-1, v.getBuild());
		assertEquals(Suffix.NONE, v.getSuffix());
		assertEquals(-1, v.getSuffixVersion());
	}

	@Test
	public void testParse5() {
		LibraryVersion v = LibraryVersion.byName("1.2.3-BETA");
		assertEquals(1, v.getMajor());
		assertEquals(2, v.getMinor());
		assertEquals(3, v.getBuild());
		assertEquals(Suffix.BETA, v.getSuffix());
		assertEquals(-1, v.getSuffixVersion());
	}

	@Test
	public void testParse6() {
		LibraryVersion v = LibraryVersion.byName("1.2.3-BETA1");
		assertEquals(1, v.getMajor());
		assertEquals(2, v.getMinor());
		assertEquals(3, v.getBuild());
		assertEquals(Suffix.BETA, v.getSuffix());
		assertEquals(1, v.getSuffixVersion());
	}

	@Test
	public void testParse7() {
		LibraryVersion v = LibraryVersion.byName("1.2.3-RC7");
		assertEquals(1, v.getMajor());
		assertEquals(2, v.getMinor());
		assertEquals(3, v.getBuild());
		assertEquals(Suffix.RC, v.getSuffix());
		assertEquals(7, v.getSuffixVersion());
	}

	@Test
	public void testParse8() {
		LibraryVersion v = LibraryVersion.byName("1.2.3-something2");
		assertEquals(1, v.getMajor());
		assertEquals(2, v.getMinor());
		assertEquals(3, v.getBuild());
		assertEquals(Suffix.UNKNOWN, v.getSuffix());
		assertEquals(2, v.getSuffixVersion());
	}

}
