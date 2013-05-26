package org.zend.php.common.core.utils.crypto;

import java.math.BigInteger;

import org.zend.php.common.Activator;
import org.zend.php.common.core.utils.crypto.InvalidEncryptionPrefixException;
import org.zend.php.common.core.utils.crypto.TEAV;

/**
 * ZCrypt - For encrypting and decrypting strings using DES algorithm. The class
 * also makes sure that the returned encoded string can be inserted into an XML
 * file by encoding the cipher text with an URLEncoder. User: shalom Date:
 * 17/01/2005
 */
public class ZCrypt {
	private static final String prefix = new String(new byte[] { 80, 87, 68 }); // PWD
	private static TEAV cipher;

	static {
		String keyString = new String(new byte[] { 51, 56, 100, 56, 54, 50,
				101, 51, 53, 97, 102, 48, 99, 56, 50, 97, 97, 102, 99, 53, 55,
				99, 100, 51, 100, 48, 100, 100, 50, 97, 57 });
		byte key[] = new BigInteger(keyString, 16).toByteArray();
		cipher = new TEAV(key);
	}

	/**
	 * Encrypts a character array.
	 * 
	 * @param chars
	 *            The char array to encrypt
	 * @return The encrypted text
	 */
	public static String encrypt(char[] chars) {
		return encrypt(new String(chars));
	}

	/**
	 * Encrypts a given string and returns the encrypted text.
	 * 
	 * @param str
	 *            The text to encrypt
	 * @return The encrypted text
	 */
	public static String encrypt(String str) {
		if (cipher == null || str == null) {
			return null;
		}
		if (str.length() == 0) {
			return str;
		}

		try {
			// Pad the plaintext with spaces when needed.
			str = cipher.padPlaintext(str);

			// Get bytes from the text and encode it.
			byte plainSource[] = str.getBytes();
			int enc[] = cipher.encode(plainSource, plainSource.length);

			// get the encoding as a hex string.
			return prefix + cipher.binToHex(enc);
		} catch (Throwable t) {
			Activator.log(t);
		}
		return null;
	}

	/**
	 * Encrypts a given string and returns the dncrypted text.
	 * 
	 * @param str
	 *            The string to decrypt
	 * @return The decrypted text.
	 */
	public static String decrypt(String str)
			throws InvalidEncryptionPrefixException {
		if (cipher == null || str == null) {
			return null;
		}
		if (str.length() == 0) {
			return str;
		}

		if (!str.startsWith(prefix)) {
			throw new InvalidEncryptionPrefixException("Invalid prefix"); //$NON-NLS-1$
		}

		String result;
		try {
			str = str.substring(prefix.length());
			int enc[] = cipher.hexToBin(str);
			byte dec[] = cipher.decode(enc);
			// return the decoded string trimmed to remove any padding.
			result = new String(dec).trim();
		} catch (Exception e) {
			Activator.log(e);
			return null;
		}
		return result;
	}

}