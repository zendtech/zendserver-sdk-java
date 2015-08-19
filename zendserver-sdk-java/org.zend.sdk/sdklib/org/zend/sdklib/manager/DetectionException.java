package org.zend.sdklib.manager;

public class DetectionException extends Exception {

	public DetectionException() {
		super();
	}
	
	public DetectionException(String message) {
		super(message);
	}

	public DetectionException(Throwable e) {
		super(e);
	}

	public DetectionException(String message, Throwable e) {
		super(message, e);
	}

	private static final long serialVersionUID = 1L;

}
