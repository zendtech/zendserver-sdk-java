/*******************************************************************************
 * Copyright (c) Dec 7, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.sdkcli.update.parser;

/**
 * 
 * Represents Zend SDK version. It has format <code>major.minor.build</code>,
 * e.g. 2.0.0.
 * 
 * @author Wojciech Galanciak, 2011
 * 
 */
public class Version implements Comparable<Version> {

	private int value;
	private String stringValue;

	public Version(String version) {
		this.stringValue = version;
		parse(version);
	}

	public int getValue() {
		return value;
	}

	@Override
	public int compareTo(Version o) {
		return getValue() - o.getValue();
	}

	public String getStringValue() {
		return stringValue;
	}

	private void parse(String version) {
		String[] blocks = version.split("\\.");
		if (blocks.length != 3) {
			throw new IllegalArgumentException("Incorrect version convention.");
		}
		int major = Integer.valueOf(blocks[0]);
		int minor = Integer.valueOf(blocks[1]);
		int build = Integer.valueOf(blocks[2]);
		if (major < 0 || minor < 0 || build < 0) {
			throw new IllegalArgumentException("Negative version part: "
					+ version);
		}
		value = (major << 16) + (minor << 8) + build;
	}

}
