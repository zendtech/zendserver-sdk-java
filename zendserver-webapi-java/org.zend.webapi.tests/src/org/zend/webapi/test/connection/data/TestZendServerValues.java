/*******************************************************************************
 * Copyright (c) 2013 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.webapi.test.connection.data;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.zend.webapi.core.connection.data.values.ZendServerVersion;

/**
 * @author Wojciech Galanciak, 2013
 *
 */
public class TestZendServerValues {

	@Test
	public void testEqualFull() {
		ZendServerVersion v = ZendServerVersion.byName("6.0.0");
		assertEquals(0, v.compareTo(ZendServerVersion.v6_0_0));
		assertEquals(0, v.compareTo(ZendServerVersion.v6_0_X));
		assertEquals(0, v.compareTo(ZendServerVersion.v6_X_X));
	}
	
	@Test
	public void testEqualMajor() {
		ZendServerVersion v = ZendServerVersion.byName("6");
		assertEquals(0, v.compareTo(ZendServerVersion.v6_0_0));
		assertEquals(0, v.compareTo(ZendServerVersion.v6_0_X));
		assertEquals(0, v.compareTo(ZendServerVersion.v6_X_X));
	}
	
	@Test
	public void testEqualMajorMinor() {
		ZendServerVersion v = ZendServerVersion.byName("6.0");
		assertEquals(0, v.compareTo(ZendServerVersion.v6_0_0));
		assertEquals(0, v.compareTo(ZendServerVersion.v6_0_X));
		assertEquals(0, v.compareTo(ZendServerVersion.v6_X_X));
	}
	
	@Test
	public void testEqualMajorBuild() {
		ZendServerVersion v = ZendServerVersion.byName("6.0.7");
		assertEquals(1, v.compareTo(ZendServerVersion.v6_0_0));
		assertEquals(0, v.compareTo(ZendServerVersion.v6_0_X));
		assertEquals(0, v.compareTo(ZendServerVersion.v6_X_X));
	}
	
	@Test
	public void testLessFull() {
		ZendServerVersion v = ZendServerVersion.byName("5.0.0");
		assertEquals(-1, v.compareTo(ZendServerVersion.v6_0_0));
		assertEquals(-1, v.compareTo(ZendServerVersion.v6_0_X));
		assertEquals(-1, v.compareTo(ZendServerVersion.v6_X_X));
	}
	
	@Test
	public void testLessMajor() {
		ZendServerVersion v = ZendServerVersion.byName("5");
		assertEquals(-1, v.compareTo(ZendServerVersion.v6_0_0));
		assertEquals(-1, v.compareTo(ZendServerVersion.v6_0_X));
		assertEquals(-1, v.compareTo(ZendServerVersion.v6_X_X));
	}
	
	@Test
	public void testLessMajorMinor() {
		ZendServerVersion v = ZendServerVersion.byName("5.0");
		assertEquals(-1, v.compareTo(ZendServerVersion.v6_0_0));
		assertEquals(-1, v.compareTo(ZendServerVersion.v6_0_X));
		assertEquals(-1, v.compareTo(ZendServerVersion.v6_X_X));
	}
	
	
	@Test
	public void testGreaterFull() {
		ZendServerVersion v = ZendServerVersion.byName("7.0.0");
		assertEquals(1, v.compareTo(ZendServerVersion.v6_0_0));
		assertEquals(1, v.compareTo(ZendServerVersion.v6_0_X));
		assertEquals(1, v.compareTo(ZendServerVersion.v6_X_X));
	}
	
	@Test
	public void testGreaterMajor() {
		ZendServerVersion v = ZendServerVersion.byName("7");
		assertEquals(1, v.compareTo(ZendServerVersion.v6_0_0));
		assertEquals(1, v.compareTo(ZendServerVersion.v6_0_X));
		assertEquals(1, v.compareTo(ZendServerVersion.v6_X_X));
	}
	
	@Test
	public void testGreaterMajorMinor() {
		ZendServerVersion v = ZendServerVersion.byName("7.0");
		assertEquals(1, v.compareTo(ZendServerVersion.v6_0_0));
		assertEquals(1, v.compareTo(ZendServerVersion.v6_0_X));
		assertEquals(1, v.compareTo(ZendServerVersion.v6_X_X));
	}
}
