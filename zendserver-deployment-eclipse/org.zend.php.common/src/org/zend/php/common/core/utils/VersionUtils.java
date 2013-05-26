/*******************************************************************************
 * Copyright (c) 2005, 2009 Zend Technologies.
 * All rights reserved. This program and the accompanying materials
 * are the copyright of Zend Technologies and is protected under
 * copyright laws of the United States.
 * You must not copy, adapt or redistribute this document for 
 * any use. 
 *
 *******************************************************************************/
package org.zend.php.common.core.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Provides the utility methods to compare 2 version strings. The version string
 * format are "major.minor.micor.[*]"
 * 
 * @author Q.S.Wang
 * 
 */
public class VersionUtils {

	private static String seperator = "."; //$NON-NLS-1$
	private static String EMPTY = ""; //$NON-NLS-1$

	/**
	 * Compare if the 2 version strings are equal. The version string format are
	 * "major.minor.micor.[*]"
	 * 
	 * @param version1
	 *            String of version 1
	 * @param version2
	 *            String of version 2
	 * @param parts
	 *            the amount of the parts of the version format.
	 * @return true if equal, otherwise false.
	 */
	public static boolean equal(String version1, String version2, int parts) {
		List<String> v1List = splitVersionToList(version1);
		List<String> v2List = splitVersionToList(version2);

		return equal(v1List, v2List, parts);
	}

	/**
	 * Compare if the version 1 is greater that version 2. The version string
	 * format are "major.minor.micor.[*]"
	 * 
	 * @param version1
	 *            String of version 1
	 * @param version2
	 *            String of version 2
	 * @param parts
	 *            the amount of the parts of the version format.
	 * @return true if greater, otherwise false.
	 */
	public static boolean greater(String version1, String version2, int parts) {
		List<String> v1List = splitVersionToList(version1);
		List<String> v2List = splitVersionToList(version2);

		return greater(v1List, v2List, parts);
	}

	private static List<String> splitVersionToList(String version) {
		ArrayList<String> strings = new ArrayList<String>();
		StringTokenizer st = new StringTokenizer(version, seperator);
		while (st.hasMoreTokens()) {
			strings.add(st.nextToken());
		}
		return strings;
	}

	private static void alignVersionLists(List<String> versions[], int parts) {
		int length = 0;
		for (int i = 0; i < versions.length; i++)
			length = Math.max(length, versions[i].size());

		if (length > parts)
			length = parts;

		for (int i = 0; i < versions.length; i++) {
			while (versions[i].size() > length)
				versions[i].remove(versions[i].size() - 1);
			while (versions[i].size() < length)
				versions[i].add(EMPTY);
		}
	}

	@SuppressWarnings("unchecked")
	private static boolean equal(List<String> version1, List<String> version2,
			int parts) {
		List<String>[] lists = new List[] { version1, version2 };
		alignVersionLists(lists, parts);

		for (int i = 0; i < version1.size(); i++) {
			if (0 != compare((String) version1.get(i), (String) version2.get(i)))
				return false;
		}

		return true;
	}

	@SuppressWarnings("unchecked")
	private static boolean greater(List<String> version1,
			List<String> version2, int parts) {

		List<String>[] lists = new List[] { version1, version2 };
		alignVersionLists(lists, parts);

		for (int i = 0; i < version1.size(); i++) {
			if (compare((String) version1.get(i), (String) version2.get(i)) > 0)
				return true;

			if (compare((String) version1.get(i), (String) version2.get(i)) < 0)
				return false;
		}

		return false;
	}

	private static int compare(String string1, String string2) {
		int number1 = 0;
		int number2 = 0;

		try {
			if (!EMPTY.equals(string1))
				number1 = Integer.valueOf(string1);

			if (!EMPTY.equals(string2))
				number2 = Integer.valueOf(string2);

			return number1 - number2;
		} catch (NumberFormatException ex) {
		}

		return string1.compareTo(string2);
	}

}
