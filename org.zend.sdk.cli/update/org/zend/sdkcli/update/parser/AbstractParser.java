/*******************************************************************************
 * Copyright (c) Dec 7, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.sdkcli.update.parser;

import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.zend.sdkcli.update.UpdateException;

/**
 * 
 * Represents abstract parser of XML files.
 * 
 * @author Wojciech Galanciak, 2011
 * 
 */
public abstract class AbstractParser {
	
	protected Document parseXML(String path) throws UpdateException {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder db = dbf.newDocumentBuilder();
			return db.parse(path);
		} catch (Exception e) {
			throw new UpdateException(e);
		}
	}

	protected Document parseXML(InputStream input) throws UpdateException {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder db = dbf.newDocumentBuilder();
			return db.parse(input);
		} catch (Exception e) {
			throw new UpdateException(e);
		}
	}

}
