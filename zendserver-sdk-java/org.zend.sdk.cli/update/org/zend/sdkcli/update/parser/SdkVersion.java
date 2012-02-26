/*******************************************************************************
 * Copyright (c) Dec 7, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.sdkcli.update.parser;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.zend.sdkcli.update.UpdateException;

/**
 * 
 * Represents version entry from versions.xml file. Each version contains its
 * name, versions range and location of detla.xml file.
 * 
 * @author Wojciech Galanciak, 2011
 * 
 */
public class SdkVersion {

	private static final String NAME = "name";
	private static final String RANGE = "range";
	private static final String DELTA = "delta";

	private Version version;
	private Range range;
	private String deltaLocation;

	public SdkVersion(Node node) {
		parse(node);
	}

	/**
	 * @return version
	 */
	public Version getVersion() {
		return version;
	}

	/**
	 * @return versions range which are allowed to use this version for update
	 */
	public Range getRange() {
		return range;
	}

	/**
	 * @return delta instances
	 * @throws UpdateException
	 */
	public Delta getDelta() throws UpdateException {
		DeltaParser parser = new DeltaParser(deltaLocation);
		return parser.getDelta();
	}

	private void parse(Node node) {
		NamedNodeMap attributes = node.getAttributes();
		Node attNode = attributes.getNamedItem(NAME);
		if (attNode == null) {
			throw new IllegalArgumentException(
					"Invalid version name tag: missing name attribute");
		}
		version = new Version(attributes.getNamedItem(NAME).getNodeValue());
		attNode = attributes.getNamedItem(RANGE);
		if (attNode == null) {
			throw new IllegalArgumentException(
					"Invalid version name tag: missing range attribute");
		}
		range = new Range(attributes.getNamedItem(RANGE).getNodeValue());
		attNode = attributes.getNamedItem(DELTA);
		if (attNode == null) {
			throw new IllegalArgumentException(
					"Invalid version name tag: missing delta attribute");
		}
		deltaLocation = attributes.getNamedItem(DELTA).getNodeValue();
	}
}
