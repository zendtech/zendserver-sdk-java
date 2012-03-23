/*******************************************************************************
 * Copyright (c) Dec 7, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.sdkcli.update.parser;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.zend.sdkcli.update.UpdateException;

/**
 * 
 * Parser for versions.xml file.
 * 
 * @author Wojciech Galanciak, 2011
 * 
 */
public class VersionParser extends AbstractParser {

	private static final String VERSION = "version";
	private String versionsLocation;

	public VersionParser(String versionsLocation) {
		super();
		this.versionsLocation = versionsLocation;
	}

	/**
	 * @return list of available version to which Zend SDK can be updated.
	 * @throws UpdateException
	 */
	public List<SdkVersion> getAvailableVersions() throws UpdateException {
		List<SdkVersion> result = new ArrayList<SdkVersion>();
		Document doc = parseXML(versionsLocation);
		NodeList versions = doc.getElementsByTagName(VERSION);
		for (int i = 0; i < versions.getLength(); i++) {
			result.add(new SdkVersion(versions.item(i)));
		}
		return result;
	}

}
