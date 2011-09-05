package org.zend.sdklib.internal.target;

public class KeyImportRequest {

	public String requestToken;
	
	public String pubKeyFile;
	
	public KeyImportRequest(String token, String key) {
		this.requestToken = token;
		this.pubKeyFile = key;
	}
	
}
