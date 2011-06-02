/*******************************************************************************
 * Copyright (c) May 18, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.sdklib.internal.utils;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.xml.sax.SAXException;

/**
 * Helps validating xmls
 * 
 * @author Roy, 2011
 */
public class XmlValidator {

	/**
	 * Validates an xml using a schema xsd file
	 * 
	 * {@link http
	 * ://www.ibm.com/developerworks/xml/library/x-javaxmlvalidapi/index.html}
	 * 
	 * @param xsd
	 *            stream
	 * @param xml
	 *            stream
	 * @return true if xml is valid
	 * @throws SAXException
	 * @throws IOException
	 */
	public static boolean validateXsd(InputStream xsd, InputStream xml)
			throws SAXException, IOException {
		// 1. Lookup a factory for the W3C XML Schema language
		SchemaFactory factory = SchemaFactory
				.newInstance("http://www.w3.org/2001/XMLSchema");

		// 2. Compile the schema.
		StreamSource source = new StreamSource(xsd);
		Schema schema = factory.newSchema(source);

		// 3. Get a validator from the schema.
		Validator validator = schema.newValidator();

		// 4. Parse the document you want to check.
		Source source1 = new StreamSource(xml);

		// 5. Check the document
		validator.validate(source1);

		return true;
	}
}
