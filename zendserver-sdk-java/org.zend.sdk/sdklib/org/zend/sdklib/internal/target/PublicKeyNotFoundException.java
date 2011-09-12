package org.zend.sdklib.internal.target;

import org.zend.sdklib.SdkException;

public class PublicKeyNotFoundException extends SdkException {

	private static final long serialVersionUID = -6908314413137760828L;

	public PublicKeyNotFoundException(String message) {
		super(message);
	}

	public PublicKeyNotFoundException(Exception e) {
		super(e);
	}
	
	public PublicKeyNotFoundException(String message, Exception e) {
		super(message, e);
	}
	
}
