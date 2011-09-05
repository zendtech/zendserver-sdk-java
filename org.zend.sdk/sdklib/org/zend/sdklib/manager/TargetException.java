package org.zend.sdklib.manager;

public class TargetException extends Exception {

	private static final long serialVersionUID = -6782314869015783299L;

	public TargetException(Exception e) {
		super(e);
	}
	
	public TargetException(String message) {
		super(message);
	}
	
	public TargetException(String message, Exception e) {
		super(message, e);
	}
}
