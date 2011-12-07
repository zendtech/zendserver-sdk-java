/*******************************************************************************
 * Copyright (c) Dec 7, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.sdkcli.update.parser;

import java.io.InputStream;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.zend.sdkcli.update.UpdateException;

/**
 * 
 * Parser for delta.xml files.
 * 
 * @author Wojciech Galanciak, 2011
 * 
 */
public class DeltaParser extends AbstractParser {

	private static final String FILE = "file";
	private static final String SIZE = "size";

	private Delta delta;

	public DeltaParser(InputStream input) throws UpdateException {
		parse(input);
	}

	public DeltaParser(String location) throws UpdateException {
		parse(location);
	}

	/**
	 * @return delta from delta.xml file
	 */
	public Delta getDelta() {
		return delta;
	}

	private void parse(InputStream input) throws UpdateException {
		Document doc = parseXML(input);
		doParse(doc);
	}

	private void parse(String location) throws UpdateException {
		Document doc = parseXML(location);
		doParse(doc);
	}

	private void doParse(Document doc) {
		Node rootNode = doc.getFirstChild();
		NamedNodeMap attributes = rootNode.getAttributes();
		Node attNode = attributes.getNamedItem(FILE);
		if (attNode == null) {
			throw new IllegalArgumentException(
					"Invalid delta tag: missing file attribute");
		}
		String zipLocation = attNode.getNodeValue();
		attNode = attributes.getNamedItem(SIZE);
		if (attNode == null) {
			throw new IllegalArgumentException(
					"Invalid delta tag: missing size attribute");
		}
		int size = Integer.valueOf(attNode.getNodeValue());
		delta = new Delta(doc, zipLocation, size);
	}

}
