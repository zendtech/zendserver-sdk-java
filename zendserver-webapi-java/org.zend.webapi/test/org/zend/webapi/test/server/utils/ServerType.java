package org.zend.webapi.test.server.utils;

public enum ServerType {
	EMBEDDED("embedded"), EXTERNAL("external"), UNKNOWN("unknown");

	private final String type;

	private ServerType(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}

	public static ServerType byType(String type) {
		final ServerType[] values = values();
		for (int i = 0; i < values.length; i++) {
			ServerType serverType = values[i];
			if (serverType.getType().equalsIgnoreCase(type)) {
				return serverType;
			}
		}
		return UNKNOWN;
	}
}
