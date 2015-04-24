/*******************************************************************************
 * Copyright (c) May 18, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.sdk.test.sdklib.repository;

import java.io.IOException;
import java.io.InputStream;

import org.junit.Test;
import org.xml.sax.SAXException;
import org.zend.sdklib.internal.utils.XmlValidator;

public class TestRepositoryXsd {

	@Test
	public void test() throws SAXException, IOException {
		final InputStream xsd = this.getClass().getResourceAsStream("test.xsd");
		final InputStream xml = this.getClass().getResourceAsStream("test.xml");

		XmlValidator.validateXsd(xsd, xml);
	}
}
