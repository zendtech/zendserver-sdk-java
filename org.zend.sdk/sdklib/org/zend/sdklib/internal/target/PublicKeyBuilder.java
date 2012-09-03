package org.zend.sdklib.internal.target;

import java.io.IOException;

import org.zend.sdklib.SdkException;

/**
 * PublicKeyBuilder is capable of generating a public key for given private key
 */
public interface PublicKeyBuilder {

	/**
	 * Returns encoded public key string for provided private key file path
	 *  
	 * @param privateKeyPath
	 * @return
	 * @throws SdkException 
	 * @throws IOException 
	 */
	String getPublicKey(String privateKeyPath) throws PublicKeyNotFoundException;
	
	/**
	 * Returns passphrase for provided private key
	 * 
	 * @param privateKey
	 * @return
	 * @throws PublicKeyNotFoundException
	 */
	String getPassphase(String privateKey) throws PublicKeyNotFoundException;;

}
