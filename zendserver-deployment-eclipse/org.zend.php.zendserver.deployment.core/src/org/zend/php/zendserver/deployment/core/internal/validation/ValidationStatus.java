package org.zend.php.zendserver.deployment.core.internal.validation;

public class ValidationStatus {

	public static final int ERROR = 1;
	public static final int WARNING = 2;
	public static final int INFO = 4;
	
	private String property;
	private int severity;
	private String message;

	public ValidationStatus(String target, int severity, String message) {
		this.property = target;
		this.severity = severity;
		this.message = message;
	}
	
	public String getProperty() {
		return property;
	}
	
	public int getSeverity() {
		return severity;
	}
	
	public String getMessage() {
		return message;
	}
	
}
