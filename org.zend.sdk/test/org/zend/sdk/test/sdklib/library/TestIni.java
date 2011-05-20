/*******************************************************************************
 * Copyright (c) May 18, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.sdk.test.sdklib.library;

import static org.junit.Assert.assertNotNull;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Properties;

import org.junit.Test;
import org.zend.sdklib.internal.target.ZendTargetAutoDetect;

public class TestIni {

	@Test
	public void testINI() throws IOException {
		InputStream is = this.getClass().getResourceAsStream(
				"zend-server-user.ini");

		final Properties prop = ZendTargetAutoDetect
				.readApiKeysSection(new BufferedReader(
						new InputStreamReader(is)));
		final String hash = prop.getProperty("roy" + ":hash");
		assertNotNull(hash);
	}

	@Test
	public void testWrite() throws IOException {
		InputStream is = this.getClass().getResourceAsStream(
				"zend-server-user.ini");
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		final String copyWithEdits = ZendTargetAutoDetect.copyWithEdits(reader, System.out, "sdk");
		assertNotNull(copyWithEdits);
	}

}
