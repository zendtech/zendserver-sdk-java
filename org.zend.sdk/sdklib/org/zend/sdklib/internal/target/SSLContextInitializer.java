package org.zend.sdklib.internal.target;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.CertificateException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import org.restlet.Context;

/**
 * Provides SSL initialization for default SSL factory and for restlet.
 * If global trust store System properties are set, then they're used. Otherwise,
 * ZendSdk own keystore is initialized and used in trust manager. 
 */
public class SSLContextInitializer {
	
	private static final String ZENDSDK_KEYSTORE = System.getProperty("user.home") + File.separator + ".zendsdk.keystore";
	private static final String ZENDSDK_KEYSTORE_PASSWORD = "localstore";
	private static final String ZENDSDK_KEYSTORE_RESOURCE = "localstore";

	public static SSLContextInitializer instance = new SSLContextInitializer();

	private SSLSocketFactory originalFactory;
	private SSLSocketFactory localFactory;
	
	private SSLContextInitializer() {
		// empty
	}
	
	/**
	 * Sets default SSL factory to use ZendSDK specific keystore
	 */
	public void setDefaultSSLFactory() {
		if (localFactory == null) {
			try {
			localFactory = createLocalFactory();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		
		originalFactory = HttpsURLConnection.getDefaultSSLSocketFactory();
		if (originalFactory == localFactory) {
			return;
		}
		
		HttpsURLConnection.setDefaultSSLSocketFactory(localFactory);
	}
	
	/**
	 * Restores Default SSL factory to the one used before calling {@link #setDefaultSSLFactory()}
	 */
	public void restoreDefaultSSLFactory() {
		if (originalFactory != null) {
			HttpsURLConnection.setDefaultSSLSocketFactory(originalFactory);
		}
	}
	
	/**
	 * Retuns map with SSL trust manager settings.
	 */
	public Map<String, String> getSettings() {
		Map<String, String> settings = new HashMap<String, String>();
		settings.put("truststoreType", System.getProperty("javax.net.ssl.trustStoreType", "JKS"));
		
		String storePath = System.getProperty("javax.net.ssl.trustStore");
		String storePassword = System.getProperty("javax.net.ssl.keyStorePassword");
		if (storePath == null) {
			storePassword = ZENDSDK_KEYSTORE_PASSWORD;
			storePath = ZENDSDK_KEYSTORE;
		}
		
		settings.put("truststorePassword", storePassword);
		settings.put("truststorePath", storePath);
		
		return settings;
	}

	private SSLSocketFactory createLocalFactory() throws Exception {
		Map<String, String> settings = getSettings();
		String storePath = settings.get("truststorePath");
		String password = settings.get("truststorePassword");
		String storeType = settings.get("truststoreType");
		
		if ((ZENDSDK_KEYSTORE.equals(storePath)) && (! new File(storePath).exists())) {
			initializeZendKeyStore(ZENDSDK_KEYSTORE_RESOURCE, storePath);
		}
		
		TrustManager[] myTMs = new TrustManager [] {
                createTrustManager(storePath, password, storeType) };		
		
		SSLContext ctx = SSLContext.getInstance("TLS");
		ctx.init(null, myTMs, null);
		SSLContext.setDefault(ctx);
		return ctx.getSocketFactory();
	}
	
	private X509TrustManager createTrustManager(String keystore, String password, String storeType) throws KeyStoreException, NoSuchAlgorithmException, CertificateException, FileNotFoundException, IOException, NoSuchProviderException {
		KeyStore ks = null;
		if (keystore != null) {
			ks = KeyStore.getInstance(storeType);
			ks.load(new FileInputStream(keystore), password.toCharArray());
		}

		TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509",
				"SunJSSE");
		tmf.init(ks);

		TrustManager tms[] = tmf.getTrustManagers();

		for (int i = 0; i < tms.length; i++) {
			if (tms[i] instanceof X509TrustManager) {
				return (X509TrustManager) tms[i];
			}
		}

		throw new IllegalArgumentException("X509TrustManager not found.");
	}
	
	private void initializeZendKeyStore(String resource, String keystore) throws IOException {
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
		Map<String, String> map = getSettings();
		
		Context ctx = new Context();
		for (Entry<String, String> entry : map.entrySet()) {
			ctx.getParameters().add(entry.getKey(), entry.getValue());
		}
		return ctx;
	}
}
