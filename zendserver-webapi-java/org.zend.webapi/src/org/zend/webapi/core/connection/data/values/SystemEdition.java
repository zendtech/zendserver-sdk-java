package org.zend.webapi.core.connection.data.values;

/**
 * Zend Server Edition.
 */
public enum SystemEdition {

	ZEND_SERVER("ZendServer"),

	ZEND_SERVER_CLUSER_MANAGER("ZendServerClusterManager"),

	ZEND_SERVER_COMMUNITY_EDITION("ZendServerCommunityEdition"),

	UNKNOWN("Unknown");

	private final String name;

	private SystemEdition(String name) {
		this.name = name;
	}

	public static SystemEdition byName(String name) {
		if (name == null) {
			return UNKNOWN;
		}

		SystemEdition[] values = values();
		for (int i = 0; i < values.length; i++) {
			SystemEdition systemEdition = values[i];
			if (name.equals(systemEdition.name)) {
				return systemEdition;
			}
		}
		return UNKNOWN;
	}

}