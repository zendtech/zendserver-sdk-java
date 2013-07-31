/*******************************************************************************
 * Copyright (c) May 18, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
/*******************************************************************************
 * Copyright (c) May 18, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/

package org.zend.sdk.test.sdklib.application;

import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.xml.bind.JAXBException;
import javax.xml.bind.PropertyException;

import org.junit.Test;
import org.zend.sdk.test.AbstractTest;
import org.zend.sdklib.descriptor.pkg.Package;
import org.zend.sdklib.internal.project.DescriptorWriter;
import org.zend.sdklib.internal.utils.JaxbHelper;

/**
 *
 */
public class TestDescriptorWriter extends AbstractTest {

	@Test
	public void testDescriptorWriterReader() throws PropertyException, JAXBException,
			IOException {
		DescriptorWriter w = new DescriptorWriter("quickstart", "data", null, "1.0.0");
		w.setDocroot("public");
		w.setZfMinMax("1.11.0", "1.13.0");
		w.setSummary("This QuickStart will introduce you to some of Zend Framework's "
				+ "most commonly used components, including Zend_Controller, Zend_Layout, "
				+ "Zend_Config, Zend_Db, Zend_Db_Table, Zend_Registry, along with a "
				+ "few view helpers.");

		final ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		w.write(outStream);
		outStream.close();

		final String string = outStream.toString();
		assertTrue(string.length() > 0);
		
		ByteArrayInputStream is = new ByteArrayInputStream(string.getBytes());
		final Package pkg = JaxbHelper.unmarshalPackage(is);
		
		assertTrue(pkg != null);
	}
}
