/*******************************************************************************
 * Copyright (c) May 18, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.sdk.test.sdklib.repository.site1;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.zend.sdklib.SdkException;
import org.zend.sdklib.internal.repository.local.JarBasedRepository;
import org.zend.sdklib.repository.site.Site;

public class TestJarBasedRepositor {

	@Test
	public void test() throws SdkException {
		final Class class1 = this.getClass();
		final JarBasedRepository repo = new JarBasedRepository(
				class1.getName(), class1);
		final Site s = repo.getSite();

		assertEquals("number of application in this site is 1", 1,
				s.getApplication().size());
	}

}
