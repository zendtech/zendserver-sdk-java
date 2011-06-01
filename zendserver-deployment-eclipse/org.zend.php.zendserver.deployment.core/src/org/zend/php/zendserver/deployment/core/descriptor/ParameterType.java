package org.zend.php.zendserver.deployment.core.descriptor;

/**
 * Represents possible parameter types defined in deployment descriptor file.
 * 
 * @author wojtek.g, 2011
 * 
 */
public enum ParameterType {

	STRING("string"), CHOICE("choice"), CHECKBOX("checkbox"), PASSWORD(
			"password"), EMAIL("email"), UNKNOWN("unknown");

	private final String name;

	private ParameterType(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public static ParameterType byName(String name) {
		if (name == null) {
			return UNKNOWN;
		}
		ParameterType[] values = values();
		for (int i = 0; i < values.length; i++) {
			ParameterType type = values[i];
			if (name.equals(type.getName())) {
				return type;
			}
		}
		return UNKNOWN;
	}
}
