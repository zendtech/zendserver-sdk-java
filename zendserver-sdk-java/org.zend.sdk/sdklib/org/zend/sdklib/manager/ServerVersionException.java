package org.zend.sdklib.manager;

public class ServerVersionException extends DetectionException {

	private static final long serialVersionUID = 1L;

	private int responseCode;
	
	public ServerVersionException(int responseCode, String message) {
		super(message);
		this.responseCode = responseCode;
	}

	public int getResponseCode() {
		return responseCode;
	}

}
