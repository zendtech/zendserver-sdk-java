/*******************************************************************************
 * Copyright (c) May 18, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.sdk.test.sdklib.repository;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.zend.sdklib.internal.utils.VersionsUtils;

public class TestVersionCompare {

	@Test
	public void testSmaller() {
		assertTrue(VersionsUtils.versionCompare("5.1", "6.4") < 0);
	}

	@Test
	public void testEquals() {
		assertTrue(VersionsUtils.versionCompare("5.1", "5.1") == 0);
	}

	@Test
	public void testGreater1() {
		assertTrue(VersionsUtils.versionCompare("5.1", "4.4") > 0);
	}

	@Test
	public void testGreater2() {
		assertTrue(VersionsUtils.versionCompare("5.1.5.6", "5.1") > 0);
	}
}
