package org.zend.sdklib.internal.target;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;

import org.restlet.Context;

/**
 * Provides SSL initialization for default SSL factory and for restlet. If
 * global trust store System properties are set, then they're used. Otherwise,
 * ZendSdk own keystore is initialized and used in trust manager.
 */
public class SSLContextInitializer {

	private static final String ZENDSDK_KEYSTORE = System
			.getProperty("user.home") + File.separator + ".zendsdk.keystore";
	private static final String ZENDSDK_KEYSTORE_PASSWORD = "localstore";
	private static final String ZENDSDK_KEYSTORE_RESOURCE = "localstore";

	public static SSLContextInitializer instance = new SSLContextInitializer();

	private static HostnameVerifier originalHostnameVerifier;

	private SSLContextInitializer() {
		// empty
	}

	/**
	 * Sets default SSL factory to use ZendSDK specific keystore
	 */
	public void setDefaultSSLFactory() {
		originalHostnameVerifier = HttpsURLConnection
				.getDefaultHostnameVerifier();
		HttpsURLConnection
				.setDefaultHostnameVerifier(new AllHostnameVerifier());
	}

	/**
	 * Restores Default SSL factory to the one used before calling
	 * {@link #setDefaultSSLFactory()}
	 */
	public void restoreDefaultSSLFactory() {
		HttpsURLConnection.setDefaultHostnameVerifier(originalHostnameVerifier);
	}

	/**
	 * Retuns map with SSL trust manager settings.
	 */
	public Map<String, String> getSettings() {
		Map<String, String> settings = new HashMap<String, String>();
		settings.put("truststoreType",
				System.getProperty("javax.net.ssl.trustStoreType", "JKS"));

		String storePath = System.getProperty("javax.net.ssl.trustStore");
		String storePassword = System
				.getProperty("javax.net.ssl.keyStorePassword");
		if (storePath == null) {
			storePassword = ZENDSDK_KEYSTORE_PASSWORD;
			storePath = ZENDSDK_KEYSTORE;
		}

		settings.put("truststorePassword", storePassword);
		settings.put("truststorePath", storePath);

		return settings;
	}

	private void createLocalFactory() throws Exception {
		Map<String, String> settings = getSettings();
		String storePath = settings.get("truststorePath");

		if ((ZENDSDK_KEYSTORE.equals(storePath))
				&& (!new File(storePath).exists())) {
			initializeZendKeyStore(ZENDSDK_KEYSTORE_RESOURCE, storePath);
		}
	}

	private void initializeZendKeyStore(String resource, String keystore)
			throws IOException {
		InputStream is = getClass().getResourceAsStream(resource);
		FileOutputStream os = new FileOutputStream(keystore);

		byte[] buf = new byte[4096];
		int len;
		try {
			while ((len = is.read(buf)) > -1) {
				os.write(buf, 0, len);
			}
		} finally {
			os.close();
			is.close();
		}
	}

	/**
	 * Creates context with SSL security settings for restlet library.
	 */
	public Context getRestletContext() {
		try {
			createLocalFactory();
		} catch (Exception e) {
			e.printStackTrace();
		}
		Map<String, String> map = getSettings();

		Context ctx = new Context();
		for (Entry<String, String> entry : map.entrySet()) {
			ctx.getParameters().add(entry.getKey(), entry.getValue());
		}
		return ctx;
	}

	/**
	 * This class implements a fake hostname verificator, trusting any host
	 * name.
	 */
	public static class AllHostnameVerifier implements HostnameVerifier {

		/**
		 * Always return true, indicating that the host name is an acceptable
		 * match with the server's authentication scheme.
		 */
		public boolean verify(String hostname, javax.net.ssl.SSLSession session) {
			return (true);
		}
	}
}
