/*******************************************************************************
 * Copyright (c) 2012 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.webapi.core.connection.data.values;

import java.text.MessageFormat;
import java.util.regex.Pattern;

/**
 * Zend Server version.
 * 
 * @author Wojciech Galanciak, 2012
 * 
 */
public class ZendServerVersion implements Comparable<ZendServerVersion> {

	private static String VERSION_TOKEN_SEPARATOR = "."; //$NON-NLS-1$
	private static String VERSION_FORMAT_STRING = MessageFormat.format("'{0}'{0}'{1}'{0}'{2}'", VERSION_TOKEN_SEPARATOR); //$NON-NLS-1$
	
	public final static ZendServerVersion V5_0_0 = new ZendServerVersion(5, 0, 0);

	public final static ZendServerVersion v5_5_0 = new ZendServerVersion(5, 5, 0);

	public final static ZendServerVersion v5_6_0 = new ZendServerVersion(5, 6, 0);

	public final static ZendServerVersion v6_0_0 = new ZendServerVersion(6, 0, 0);

	public final static ZendServerVersion v6_0_X = new ZendServerVersion(6, 0, -1);

	public final static ZendServerVersion v6_2_x = new ZendServerVersion(6, 2, -1);
	
	public final static ZendServerVersion v6_X_X = new ZendServerVersion(6, -1, -1);

	public final static ZendServerVersion UNKNOWN = new ZendServerVersion(-1, -1, -1);

	private int major;
	private int minor;
	private int build;

	private ZendServerVersion(int major, int minor, int build) {
		this.major = major;
		this.minor = minor;
		this.build = build;
	}

	public String getName() {
		return MessageFormat.format(VERSION_FORMAT_STRING, major, minor, build);
	}

	public int getMajor() {
		return major;
	}

	public int getMinor() {
		return minor;
	}

	public int getBuild() {
		return build;
	}

	public int compareTo(ZendServerVersion v) {
		if (getMajor() < v.getMajor()) {
			return -1;
		}
		if (getMajor() > v.getMajor()) {
			return 1;
		}
		if (getMinor() != -1 && v.getMinor() != -1) {
			if (getMinor() < v.getMinor()) {
				return -1;
			}
			if (getMinor() > v.getMinor()) {
				return 1;
			}
		}
		if (getBuild() != -1 && v.getBuild() != -1) {
			if (getBuild() < v.getBuild()) {
				return -1;
			}
			if (getBuild() > v.getBuild()) {
				return 1;
			}
		}
		return 0;
	}
	
	public static ZendServerVersion byName(String name) {
		return byName(name, VERSION_TOKEN_SEPARATOR);
	}
	
	public static ZendServerVersion byName(String name, String separator) {
		if (name == null) {
			return UNKNOWN;
		}
		
		int[] parsedValues = parse(name, separator);
		int major = parsedValues[0];
		int minor = parsedValues[1];
		int build = parsedValues[2];
		return new ZendServerVersion(major, minor, build);
	}

	private static int[] parse(String name, String separator) {
		String[] segments = name.split(Pattern.quote(separator));
		int[] result = new int[3];
		for (int i = 0; i < result.length; i++) {
			if (segments.length > i) {
				result[i] = Integer.valueOf(segments[i]);
			} else {
				result[i] = -1;
			}
		}
		return result;
	}

}