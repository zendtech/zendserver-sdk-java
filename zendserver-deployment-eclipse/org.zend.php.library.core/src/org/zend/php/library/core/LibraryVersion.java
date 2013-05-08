package org.zend.php.library.core;

/**
 * 
 * @author Wojciech Galanciak, 2012
 * 
 */
public class LibraryVersion implements Comparable<LibraryVersion> {

	public static final LibraryVersion UNKNOWN = new LibraryVersion(-1, -1, -1,
			-1, Suffix.NONE, -1, "unknown");

	public enum Suffix {

		ALPHA("alpha"),

		BETA("beta"),

		RC("rc"),

		DEV("dev"),

		UNKNOWN(""),

		NONE(null);

		private String name;

		private Suffix(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}

		public static Suffix byName(String name) {
			if (name == null) {
				return UNKNOWN;
			}
			String value = parseSuffix(name);
			value = value.toLowerCase();
			Suffix[] values = values();
			for (Suffix suffix : values) {
				if (value.equals(suffix.getName())) {
					return suffix;
				}
			}
			return UNKNOWN;
		}

		private static String parseSuffix(String name) {
			return name.replaceAll("[0-9]", ""); // returns 123
		}
	}

	private int major;
	private int minor;
	private int build;
	private int revision;
	private Suffix suffix;
	private int suffixVersion;

	private String fullVersion;

	private LibraryVersion(int major, int minor, int build, int revision,
			Suffix suffix, int suffixVersion, String fullVersion) {
		this.major = major;
		this.minor = minor;
		this.build = build;
		this.revision = revision;
		this.suffix = suffix;
		this.suffixVersion = suffixVersion;
		this.fullVersion = fullVersion;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.php.internal.core.library.ILibraryVersion#getName()
	 */
	public String getName() {
		return major + "." + minor + "." + build + "." + revision;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.php.internal.core.library.ILibraryVersion#getMajor()
	 */
	public int getMajor() {
		return major;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.php.internal.core.library.ILibraryVersion#getMinor()
	 */
	public int getMinor() {
		return minor;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.php.internal.core.library.ILibraryVersion#getBuild()
	 */
	public int getBuild() {
		return build;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.php.internal.core.library.ILibraryVersion#getRevision()
	 */
	public int getRevision() {
		return revision;
	}

	public Suffix getSuffix() {
		return suffix;
	}

	public int getSuffixVersion() {
		return suffixVersion;
	}

	public String toString() {
		return fullVersion;
	}

	public int compareTo(LibraryVersion v) {
		// TODO include suffix support
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
		if (getRevision() != -1 && v.getRevision() != -1) {
			if (getRevision() < v.getRevision()) {
				return -1;
			}
			if (getRevision() > v.getRevision()) {
				return 1;
			}
		}
		int result = getSuffix().compareTo(v.getSuffix());
		if (result > 0) {
			return 1;
		}
		if (result < 0) {
			return -1;
		}
		if (result == 0) {
			if (suffixVersion > v.getSuffixVersion()) {
				return 1;
			}
			if (suffixVersion < v.getSuffixVersion()) {
				return -1;
			}
		}
		return 0;
	}

	public static LibraryVersion byName(String name) {
		if (name == null) {
			return UNKNOWN;
		}
		return parse(name);
	}

	private static LibraryVersion parse(final String name) {
		// TODO consider "self.version" value
		Suffix suffix = Suffix.NONE;
		int suffixVersion = -1;
		String toParse = name.trim();
		if (name.equals("*")) {
			return UNKNOWN;
		}

		// e.g. v2.0.0
		if (toParse.startsWith("v") || toParse.startsWith("V")) {
			toParse = toParse.substring(1);
		}

		// e.g. 2.0.0-dev or 2.0.0_dev,
		int index = -1;
		if (toParse.indexOf("-") != -1) {
			index = toParse.indexOf("-");
		}
		if (toParse.indexOf("_") != -1) {
			int i = toParse.indexOf("_");
			if (index == -1 || index > i) {
				index = i;
			}
		}
		if (index != -1) {
			String suffixString = toParse.substring(index + 1);
			suffix = Suffix.byName(suffixString);
			if (suffix != Suffix.UNKNOWN) {
				suffixVersion = parseSuffixVersion(suffixString);
			}
			toParse = toParse.substring(0, index);
		}

		String[] segments = toParse.split("\\.");
		int[] result = new int[4];
		for (int i = 0; i < result.length; i++) {
			if (segments.length > i) {
				if (segments[i].equalsIgnoreCase("x")) {
					result[i] = 9999999;
				} else if (segments[i].equalsIgnoreCase("*")) {
					result[i] = -1;
				} else {
					try {
						result[i] = Integer.valueOf(segments[i]);
					} catch (NumberFormatException e) {
						result[i] = 9999999;
					}
				}
			} else {
				result[i] = 0;
			}
		}
		return new LibraryVersion(result[0], result[1], result[2], result[3],
				suffix, suffixVersion, name);
	}

	private static int parseSuffixVersion(String name) {
		String val = name.replaceAll("[a-zA-Z]", ""); // returns 123
		if (!val.isEmpty()) {
			return Integer.valueOf(val);
		}
		return -1;
	}

}