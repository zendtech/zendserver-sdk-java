/*******************************************************************************
 * Copyright (c) May 18, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.sdk.test.sdklib.repository;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.zend.sdklib.internal.utils.VersionsUtils;

@RunWith(Parameterized.class)
public class TestVersionPattern {

	private final String ver;
	private final String range;
	private final boolean inBetween;

	@Parameters
	public static Collection<Object[]> generateData() {
		return Arrays
				.asList(new Object[][] { { "5.1", "[5.0, 6.4]", true },
						{ "5.0", "[5.0, 6.4]", true },
						{ "5.0", "(5.0, 6.4]", false },
						{ "4.9", "[5.0, 6.4]", false },
						{ "4.9", "(5.0, 6.4]", false },
						{ "4.9.567.3", "(5.0, 6.4]", false },
						{ "6.4", "[5.0, 6.4]", true },
						{ "6.4", "[5.0, 6.4)", false },
						{ "7.0", "[5.0, 6.4]", false } });
	}

	public TestVersionPattern(String ver, String range, boolean inBetween) {
		this.ver = ver;
		this.range = range;
		this.inBetween = inBetween;
	}

	@Test
	public void test() {
		assertEquals(inBetween, VersionsUtils.inBetween(ver, range));
	}
}
