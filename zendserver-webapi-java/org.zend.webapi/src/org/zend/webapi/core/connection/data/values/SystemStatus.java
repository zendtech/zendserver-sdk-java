package org.zend.webapi.core.connection.data.values;


/**
 * Global status information
 */
public enum SystemStatus {
	OK("OK", "system is operational"),

	NOT_LICENSED(
			"Not Licensed",
			"system is not licensed. In ZSCM, this means the ZSCM is not licensed. The nodes may be licensed and operating."),

	PENDING_RESTART("Pending Restart",
			"system is pending a PHP restart. In ZSCM this will never be set."),

	UNKNOWN(
			"Unknown",
			"Unknown System Status, if the problem remains please report it to your administrator.");

	private final String title;
	private final String description;

	private SystemStatus(String title, String description) {
		this.title = title;
		this.description = description;
	}

	public String getTitle() {
		return title;
	}

	public String getDescription() {
		return description;
	}

	public static SystemStatus byName(String name) {
		if (name == null) {
			return UNKNOWN;
		}
		SystemStatus[] values = values();
		for (int i = 0; i < values.length; i++) {
			SystemStatus systemStatus = values[i];
			if (name.equals(systemStatus.getTitle())) {
				return systemStatus;
			}
		}
		return UNKNOWN;
	}
}