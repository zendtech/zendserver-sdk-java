/*******************************************************************************
 * Copyright (c) May 18, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.sdk.test.sdklib.repository.site1;

import static org.junit.Assert.*;

import org.junit.Test;
import org.zend.sdklib.internal.repository.local.JarBasedRepository;
import org.zend.sdklib.repository.site.Application;

public class TestJarBasedRepositor {

	@Test
	public void test() {
		final JarBasedRepository repo = new JarBasedRepository(this.getClass());
		final Application[] availableApplications = repo
				.getAvailableApplications();
		
		assertEquals("number of application in this site is 1", 1,
				availableApplications.length);
		System.out.println(availableApplications[0].getId());
	}

}
