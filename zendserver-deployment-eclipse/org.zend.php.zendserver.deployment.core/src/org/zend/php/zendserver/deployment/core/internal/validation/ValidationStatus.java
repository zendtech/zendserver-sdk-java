package org.zend.php.zendserver.deployment.core.internal.validation;

import org.zend.php.zendserver.deployment.core.internal.descriptor.Feature;

public class ValidationStatus {

	public static final int WARNING = 1;
	public static final int ERROR = 2;
	public static final int INFO = 4;
	
	private Feature property;
	private int severity;
	private String message;
	private int line;
	private int start;
	private int end;

	public ValidationStatus(Feature target, int line, int start, int end, int severity, String message) {
		this.property = target;
		this.line = line;
		this.start = start;
		this.end = end;
		this.severity = severity;
		this.message = message;
	}
	
	public Feature getProperty() {
		return property;
	}
	
	public int getSeverity() {
		return severity;
	}
	
	public String getMessage() {
		return message;
	}

	public int getLine() {
		return line;
	}

	public int getStart() {
		return start;
	}

	public int getEnd() {
		return end;
	}
	
}
