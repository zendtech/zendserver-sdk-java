package org.zend.sdklib.manager;

public class MissingZendServerException extends DetectionException {

	private static final long serialVersionUID = 181121035306953181L;

	public MissingZendServerException(String message) {
		super(message);
	}
	
	public MissingZendServerException(Throwable e) {
		super(e);
	}
	
}
