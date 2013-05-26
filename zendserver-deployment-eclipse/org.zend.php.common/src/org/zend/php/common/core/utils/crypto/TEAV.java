package org.zend.php.common.core.utils.crypto;

// This import is only for the main() example.  It is not required by TEAV.

import java.math.BigInteger;

import org.zend.php.common.core.utils.crypto.TEAV;

/**
 * Tiny Encryption Algorithm - Variant.
 * <P>
 * (The following description is from the web page for the C and Assembler
 * source code at <A HREF="http://vader.brad.ac.uk/tea/tea.shtml"> University of
 * Bradford Yorkshire, England - The Cryptography & Computer Communications
 * Security Group</A>) The description is used with the permission of the
 * authors, Dr S J Shepherd and D A G Gillies.
 * <P>
 * The Tiny Encryption Algorithm is one of the fastest and most efficient
 * cryptographic algorithms in existence. It was developed by David Wheeler and
 * Roger Needham at the Computer Laboratory of Cambridge University. It is a
 * Feistel cipher which uses operations from mixed (orthogonal) algebraic groups
 * - XORs and additions in this case. It encrypts 64 data bits at a time using a
 * 128-bit key. It seems highly resistant to differential cryptanalysis, and
 * achieves complete diffusion (where a one bit difference in the plaintext will
 * cause approximately 32 bit differences in the ciphertext) after only six
 * rounds. Performance on a modern desktop computer or workstation is very
 * impressive.
 * <P>
 * TEA takes 64 bits of data in v[0] and v[1], and 128 bits of key in k[0] -
 * k[3]. The result is returned in w[0] and w[1]. Returning the result
 * separately makes implementation of cipher modes other than Electronic Code
 * Book a little bit easier.
 * <P>
 * TEA can be operated in any of the modes of DES.
 * <P>
 * n is the number of iterations. 32 is ample, 16 is sufficient, as few as eight
 * should be OK for most applications, especially ones where the data age
 * quickly (real-time video, for example). The algorithm achieves good
 * dispersion after six iterations. The iteration count can be made variable if
 * required.
 * <P>
 * Note this algorithm is optimised for 32-bit CPUs with fast shift
 * capabilities. It can very easily be ported to assembly language on most CPUs.
 * <P>
 * delta is chosen to be the Golden ratio ((5/4)1/2 - 1/2 ~ 0.618034) multiplied
 * by 232. On entry to decipher(), sum is set to be delta * n. Which way round
 * you call the functions is arbitrary: DK(EK(P)) = EK(DK(P)) where EK and DK
 * are encryption and decryption under key K respectively.
 * <P>
 * Translator's notes:
 * <UL>
 * <LI>Although the <I>this algorithm is optimised for 32-bit CPUs with fast
 * shift capabilities</I> Java manages to throw it all away by not providing
 * unsigned values resulting in the excessive use of AND's to prevent sign
 * extension on promotion of a byte to an integer.</LI>
 * <P>
 * <LI>
 * The following description is taken from the Mach5 Software cryptography
 * archives at <A HREF="http://www.mach5.com/crypto/">www.mach5.com/crypto</A>.
 * <p>
 * <font face="Arial" size="4">Tiny Encryption Algorithm (TEA)</font><br>
 * <font size="3" face="Arial">TEA is a cryptographic algorithm designed to
 * minimize memory footprint, and maximize speed. However, the cryptographers
 * from <a
 * <p/>
 * href="http://www.counterpane.com">Counterpane Systems</a> have <a
 * <p/>
 * href="http://www.cs.berkeley.edu/~daw/keysched-crypto96.ps">discovered three
 * related-key attacks </a>on TEA, the best of which requires only 223 chosen
 * plaintexts and one related key query. The problems arise from the overly
 * simple key schedule. Each TEA key can be found to have three other equivalent
 * keys, as described in <a
 * <p/>
 * href="http://www.cs.berkeley.edu/~daw/keysched-icics97.ps">a paper</a> by <a
 * <p/>
 * href="http://www.cs.berkeley.edu/~daw/">David Wagner</a>, John Kelsey, and <a
 * <p/>
 * href="http://www.counterpane.com/schneier.html">Bruce Schneier</a>. This
 * precludes the possibility of using TEA as a hash function. Roger Needham and
 * David Wheeler have proposed <a
 * href="http://www.cl.cam.ac.uk/ftp/users/djw3/xtea.ps">extensions to TEA</a>
 * that counters the above attacks.</font>
 * </p>
 * </LI>
 * </UL>
 * <B>This is the extension, or variant, of TEA that addresses these
 * concerns.</B>
 * <p/>
 * <P>
 * Example of use:
 * 
 * <PRE>
 * byte key[] = new BigInteger(&quot;39e858f86df9b909a8c87cb8d9ad599&quot;, 16).toByteArray();
 * TEAV t = new TEAV(key);
 * &lt;BR&gt;
 * String src = &quot;hello world!&quot;;
 * System.out.println(&quot;input = &quot; + src);
 * byte plainSource[] = src.getBytes();
 * int enc[] = t.encode(plainSource, plainSource.length);
 * System.out.println(t.padding() + &quot; bytes added as padding.&quot;);
 * byte dec[] = t.decode(enc);
 * System.out.println(&quot;output = &quot; + new String(dec));
 * </PRE>
 */

public class TEAV {
	private int _key[]; // The 128 bit key.
	private byte _keyBytes[]; // original key as found
	private int _padding; // amount of padding added in byte --> integer
							// conversion.

	/**
	 * Array of hex char mappings. hex[0] = '0', hex[15] = 'F'.
	 */
	protected static final char hex[] = { '0', '1', '2', '3', '4', '5', '6',
			'7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };

	/**
	 * Encodes and decodes "Hello world!" for your personal pleasure.
	 */
	public static void main(String args[]) {
		// A simple test of TEAV.
		String keyString = "39e858f86df9b909a8c87cb8d9ad599"; //$NON-NLS-1$
		byte key[] = new BigInteger(keyString, 16).toByteArray();
		TEAV t = new TEAV(key);

		String src = "Hello!!! I am a password!!"; //$NON-NLS-1$
		System.out.println("input = [" + src + "]"); //$NON-NLS-1$ //$NON-NLS-2$

		// Pad the plaintext with spaces.
		src = t.padPlaintext(src);

		// Get bytes from the text and encode it.
		byte plainSource[] = src.getBytes();
		int enc[] = t.encode(plainSource, plainSource.length);

		// Report on padding, it should be zero since we originally padded the
		// string with spaces.
		System.out.println(t.padding() + " bytes added as padding."); //$NON-NLS-1$

		// Display what the encoding would be in a hex string.
		String hexStr = t.binToHex(enc);
		System.out.println("Encoding as Hex string: " + hexStr); //$NON-NLS-1$

		// Just to prove that converting it to a hex string a back works decode
		// the hex string.
		int enc2[] = t.hexToBin(hexStr);

		byte dec[] = t.decode(enc2);

		// Display the resulting decoded string, trimmed.to remove padding.
		System.out.println("output = [" + new String(dec).trim() + "]"); //$NON-NLS-1$ //$NON-NLS-2$

		System.exit(0);
	}

	/**
	 * Accepts key for enciphering/deciphering.
	 * 
	 * @param key
	 *            128 bit (16 byte) key.
	 * @throws ArrayIndexOutOfBoundsException
	 *             if the key isn't the correct length.
	 */
	public TEAV(byte key[]) {
		int klen = key.length;
		_key = new int[4];

		// Incorrect key length throws exception.
		if (klen != 16)
			throw new ArrayIndexOutOfBoundsException(this.getClass().getName()
					+ ": Key is not 16 bytes"); //$NON-NLS-1$

		int j, i;
		for (i = 0, j = 0; j < klen; j += 4, i++)
			_key[i] = (key[j] << 24) | (((key[j + 1]) & 0xff) << 16)
					| (((key[j + 2]) & 0xff) << 8) | ((key[j + 3]) & 0xff);

		_keyBytes = key; // save for toString.
	}

	public TEAV(int key[]) {
		_key = key;
	}

	/**
	 * Representation of TEA class
	 */
	public String toString() {
		String tea = this.getClass().getName();
		tea += ": Tiny Encryption Algorithm (TEA)  key: " + getHex(_keyBytes); //$NON-NLS-1$
		return tea;
	}

	/**
	 * Encipher two <code>int</code>s. Replaces the original contents of the
	 * parameters with the results. The integers are usually created from 8
	 * bytes. The usual way to collect bytes to the int array is:
	 * 
	 * <PRE>
	 * byte ba[] = { .... };
	 * int v[] = new int[2];
	 * v[0] = (ba[j] &lt;&lt; 24 ) | (((ba[j+1])&amp;0xff) &lt;&lt; 16) | (((ba[j+2])&amp;0xff) &lt;&lt; 8) | ((ba[j+3])&amp;0xff);
	 * v[1] = (ba[j+4] &lt;&lt; 24 ) | (((ba[j+5])&amp;0xff) &lt;&lt; 16) | (((ba[j+6])&amp;0xff) &lt;&lt; 8) | ((ba[j+7])&amp;0xff);
	 * v = encipher(v);
	 * </PRE>
	 * 
	 * @param v
	 *            two <code>int</code> array as input.
	 * @return array of two <code>int</code>s, enciphered.
	 */
	public int[] encipher(int v[]) {
		int y = v[0];
		int z = v[1];
		int sum = 0;
		int delta = 0x9E3779B9;

		int n = 32;

		while (n-- > 0) {
			y += (z << 4 ^ z >>> 5) + z ^ sum + _key[(int) (sum & 3)];
			sum += delta;
			z += (y << 4 ^ y >>> 5) + y ^ sum + _key[(int) (sum >>> 11) & 3];
		}

		int w[] = new int[2];
		w[0] = (int) y;
		w[1] = (int) z;

		return w;
	}

	/**
	 * Decipher two <code>int</code>s. Replaces the original contents of the
	 * parameters with the results. The integers are usually decocted to 8
	 * bytes. The decoction of the <code>int</code>s to bytes can be done this
	 * way.
	 * 
	 * <PRE>
	 * int x[] = decipher(ins);
	 * outb[j] = (byte) (x[0] &gt;&gt;&gt; 24);
	 * outb[j + 1] = (byte) (x[0] &gt;&gt;&gt; 16);
	 * outb[j + 2] = (byte) (x[0] &gt;&gt;&gt; 8);
	 * outb[j + 3] = (byte) (x[0]);
	 * outb[j + 4] = (byte) (x[1] &gt;&gt;&gt; 24);
	 * outb[j + 5] = (byte) (x[1] &gt;&gt;&gt; 16);
	 * outb[j + 6] = (byte) (x[1] &gt;&gt;&gt; 8);
	 * outb[j + 7] = (byte) (x[1]);
	 * </PRE>
	 * 
	 * @param v
	 *            <code>int</code> array of 2
	 * @return deciphered <code>int</code> array of 2
	 */
	public int[] decipher(int v[]) {
		int y = v[0];
		int z = v[1];
		int sum = 0xC6EF3720;
		int delta = 0x9E3779B9;

		int n = 32;

		// sum = delta<<5, in general sum = delta * n

		while (n-- > 0) {
			z -= (y << 4 ^ y >>> 5) + y ^ sum + _key[(sum >>> 11) & 3];
			sum -= delta;
			y -= (z << 4 ^ z >>> 5) + z ^ sum + _key[sum & 3];
		}

		int w[] = new int[2];
		w[0] = (int) y;
		w[1] = (int) z;

		return w;
	}

	/**
	 * Byte wrapper for encoding. Converts bytes to ints. Padding will be added
	 * if required.
	 * 
	 * @param b
	 *            incoming <code>byte</code> array
	 * @param count
	 * @return integer conversion array, possibly with padding.
	 * @see #padding
	 */
	public int[] encode(byte b[], int count) {
		int j, i;
		int bLen = count;
		byte bp[] = b;

		_padding = bLen % 8;
		if (_padding != 0) // Add some padding, if necessary.
		{
			_padding = 8 - (bLen % 8);
			bp = new byte[bLen + _padding];
			System.arraycopy(b, 0, bp, 0, bLen);
			bLen = bp.length;
		}

		int intCount = bLen / 4;
		int r[] = new int[2];
		int out[] = new int[intCount];

		for (i = 0, j = 0; j < bLen; j += 8, i += 2) {
			r[0] = (bp[j] << 24) | (((bp[j + 1]) & 0xff) << 16)
					| (((bp[j + 2]) & 0xff) << 8) | ((bp[j + 3]) & 0xff);
			r[1] = (bp[j + 4] << 24) | (((bp[j + 5]) & 0xff) << 16)
					| (((bp[j + 6]) & 0xff) << 8) | ((bp[j + 7]) & 0xff);
			r = encipher(r);
			out[i] = r[0];
			out[i + 1] = r[1];
		}

		return out;
	}

	/**
	 * Report how much padding was done in the last encode.
	 * 
	 * @return bytes of padding added
	 * @see #encode
	 */
	public int padding() {
		return _padding;
	}

	/**
	 * Convert a byte array to ints and then decode. There may be some padding
	 * at the end of the byte array from the previous encode operation.
	 * 
	 * @param b
	 *            bytes to decode
	 * @param count
	 *            number of bytes in the array to decode
	 * @return <code>byte</code> array of decoded bytes.
	 */
	public byte[] decode(byte b[], int count) {
		int i, j;

		int intCount = count / 4;
		int ini[] = new int[intCount];
		for (i = 0, j = 0; i < intCount; i += 2, j += 8) {
			ini[i] = (b[j] << 24) | (((b[j + 1]) & 0xff) << 16)
					| (((b[j + 2]) & 0xff) << 8) | ((b[j + 3]) & 0xff);
			ini[i + 1] = (b[j + 4] << 24) | (((b[j + 5]) & 0xff) << 16)
					| (((b[j + 6]) & 0xff) << 8) | ((b[j + 7]) & 0xff);
		}
		return decode(ini);
	}

	/**
	 * Decode an integer array. There may be some padding at the end of the byte
	 * array from the previous encode operation.
	 * 
	 * @param b
	 *            bytes to decode
	 * @return <code>byte</code> array of decoded bytes.
	 */
	public byte[] decode(int b[]) {
		// create the large number and start stripping ints out, two at a time.
		int intCount = b.length;

		byte outb[] = new byte[intCount * 4];
		int tmp[] = new int[2];

		// decipher all the ints.
		int i, j;
		for (j = 0, i = 0; i < intCount; i += 2, j += 8) {
			tmp[0] = b[i];
			tmp[1] = b[i + 1];
			tmp = decipher(tmp);
			outb[j] = (byte) (tmp[0] >>> 24);
			outb[j + 1] = (byte) (tmp[0] >>> 16);
			outb[j + 2] = (byte) (tmp[0] >>> 8);
			outb[j + 3] = (byte) (tmp[0]);
			outb[j + 4] = (byte) (tmp[1] >>> 24);
			outb[j + 5] = (byte) (tmp[1] >>> 16);
			outb[j + 6] = (byte) (tmp[1] >>> 8);
			outb[j + 7] = (byte) (tmp[1]);
		}

		return outb;
	}

	/**
	 * Convert a string into an integer array form suitable for decoding.
	 * 
	 * @param hexStr
	 *            String of hexadecimal digits.
	 * @return integer array.
	 * @throws ArrayIndexOutOfBoundsException
	 *             if the string length is not divisible into integer length
	 *             pieces.
	 */
	public int[] hexToBin(String hexStr) throws ArrayIndexOutOfBoundsException {
		int hexStrLen = hexStr.length();

		// Decode a hex string into a collection of ints.
		if ((hexStrLen % 8) != 0)
			throw new ArrayIndexOutOfBoundsException(
					"Hex string has incorrect length, required to be divisible by eight: " + hexStrLen); //$NON-NLS-1$

		int outLen = hexStrLen / 8;
		int out[] = new int[outLen];
		byte nibble[] = new byte[2];
		byte b[] = new byte[4];
		int posn = 0;
		for (int i = 0; i < outLen; i++) {
			for (int j = 0; j < 4; j++) {
				for (int k = 0; k < 2; k++) {
					switch (hexStr.charAt(posn++)) {
					case '0':
						nibble[k] = (byte) 0;
						break;
					case '1':
						nibble[k] = (byte) 1;
						break;
					case '2':
						nibble[k] = (byte) 2;
						break;
					case '3':
						nibble[k] = (byte) 3;
						break;
					case '4':
						nibble[k] = (byte) 4;
						break;
					case '5':
						nibble[k] = (byte) 5;
						break;
					case '6':
						nibble[k] = (byte) 6;
						break;
					case '7':
						nibble[k] = (byte) 7;
						break;
					case '8':
						nibble[k] = (byte) 8;
						break;
					case '9':
						nibble[k] = (byte) 9;
						break;
					case 'A':
						nibble[k] = (byte) 0xA;
						break;
					case 'B':
						nibble[k] = (byte) 0xB;
						break;
					case 'C':
						nibble[k] = (byte) 0xC;
						break;
					case 'D':
						nibble[k] = (byte) 0xD;
						break;
					case 'E':
						nibble[k] = (byte) 0xE;
						break;
					case 'F':
						nibble[k] = (byte) 0xF;
						break;
					case 'a':
						nibble[k] = (byte) 0xA;
						break;
					case 'b':
						nibble[k] = (byte) 0xB;
						break;
					case 'c':
						nibble[k] = (byte) 0xC;
						break;
					case 'd':
						nibble[k] = (byte) 0xD;
						break;
					case 'e':
						nibble[k] = (byte) 0xE;
						break;
					case 'f':
						nibble[k] = (byte) 0xF;
						break;
					}
				}

				b[j] = (byte) (nibble[0] << 4 | nibble[1]);
			}
			out[i] = (b[0] << 24) | (((b[1]) & 0xff) << 16)
					| (((b[2]) & 0xff) << 8) | ((b[3]) & 0xff);
		}

		return out;
	}

	/**
	 * Convert an array of ints into a hex string.
	 * 
	 * @param enc
	 *            Array of integers.
	 * @return String hexadecimal representation of the integer array.
	 * @throws ArrayIndexOutOfBoundsException
	 *             if the array doesn't contain pairs of integers.
	 */
	public String binToHex(int enc[]) throws ArrayIndexOutOfBoundsException {
		// The number of ints should always be a multiple of two as required by
		// TEA (64 bits).
		if ((enc.length % 2) == 1)
			throw new ArrayIndexOutOfBoundsException(
					"Odd number of ints found: " + enc.length); //$NON-NLS-1$

		StringBuffer sb = new StringBuffer();
		byte outb[] = new byte[8];
		// int tmp[] = new int[2];
		// int counter = enc.length / 2;

		for (int i = 0; i < enc.length; i += 2) {
			outb[0] = (byte) (enc[i] >>> 24);
			outb[1] = (byte) (enc[i] >>> 16);
			outb[2] = (byte) (enc[i] >>> 8);
			outb[3] = (byte) (enc[i]);
			outb[4] = (byte) (enc[i + 1] >>> 24);
			outb[5] = (byte) (enc[i + 1] >>> 16);
			outb[6] = (byte) (enc[i + 1] >>> 8);
			outb[7] = (byte) (enc[i + 1]);

			sb.append(getHex(outb));
		}

		return sb.toString();
	}

	// Display some bytes in HEX.
	//
	public String getHex(byte b[]) {
		StringBuffer r = new StringBuffer();

		for (int i = 0; i < b.length; i++) {
			int c = ((b[i]) >>> 4) & 0xf;
			r.append(hex[c]);
			c = ((int) b[i] & 0xf);
			r.append(hex[c]);
		}

		return r.toString();
	}

	/**
	 * Pad a string out to the proper length with the given character.
	 * 
	 * @param str
	 *            Plain text string.
	 * @param pc
	 *            Padding character.
	 */
	public String padPlaintext(String str, char pc) {
		StringBuffer sb = new StringBuffer(str);
		int padding = sb.length() % 8;
		for (int i = 0; i < padding; i++)
			sb.append(pc);

		return sb.toString();
	}

	/**
	 * Pad a string out to the proper length with spaces.
	 * 
	 * @param str
	 *            Plain text string.
	 */
	public String padPlaintext(String str) {
		return padPlaintext(str, ' ');
	}

}