package org.zend.sdklib.internal.target;

import org.zend.sdklib.SdkException;

public class NoBootstrapException extends SdkException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8666055128452782418L;
	
	public NoBootstrapException() {
		super("Server is not bootsrapped");
	}

}
