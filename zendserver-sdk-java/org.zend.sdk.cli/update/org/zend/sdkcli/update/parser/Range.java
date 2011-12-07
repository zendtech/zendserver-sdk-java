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
 * Represents allowed versions range. Here are some rules about defining valid
 * range:
 * <ul>
 * <li>'[' means that lower bound is included</li>
 * <li>']' means that upper bound is included</li>
 * <li>'(' means that lower bound is excluded</li>
 * <li>')' means that upper bound is excluded</li>
 * <li>Lack of upper bound means that it is not limited</li>
 * <li>Lack of lower bound means that it is equals to 0</li>
 * </ul>
 * 
 * @author Wojciech Galanciak, 2011
 * 
 */
public class Range {

	private static final String VALUE_SEPARATOR = ",";
	private static final String EMPTY_STRING = "";

	private int up;
	private int down;

	public Range(String range) {
		parse(range);
	}
	
	/**
	 * @param version
	 * @return <code>true</code> if specified version is in the range;
	 *         <code>false</code> otherwise
	 */
	public boolean isAllowed(Version version) {
		int value = version.getValue();
		if (up > 0 && (down >= value || up < value)) {
			return false;
		} else if (down >= value) {
			return false;
		}
		return true;
	}

	private void parse(String range) {
		boolean downInclude = range.startsWith("[") ? true : false;
		if (!downInclude && !range.startsWith("(")) {
			throw new IllegalArgumentException(
					"Incorrect version range format. It has to start with '(' or '['");
		}
		boolean upInclude = range.endsWith("]") ? true : false;
		if (!upInclude && !range.endsWith(")")) {
			throw new IllegalArgumentException(
					"Incorrect version range format. It has to end with ')' or ']'");
		}
		range = range.substring(1, range.length() - 1);
		String[] values = range.split(VALUE_SEPARATOR);
		if (values.length > 2 || values.length == 0) {
			throw new IllegalArgumentException("Invalid version range");
		}
		if (values[0].equals(EMPTY_STRING)) {
			down = 0;
		} else {
			Version downVersion = new Version(values[0]);
			down = Integer.valueOf(downVersion.getValue())
					- (downInclude ? 1 : 0);
		}
		if (values.length == 1 || values[1].equals(EMPTY_STRING)) {
			up = -1;
		} else {
			Version upVersion = new Version(values[1]);
			up = Integer.valueOf(upVersion.getValue()) - (upInclude ? 0 : 1);
		}
	}

}
