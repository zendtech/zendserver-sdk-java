package org.zend.sdk.test.sdklib.target;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Random;

import org.junit.Before;
import org.junit.Test;
import org.zend.sdklib.internal.target.ZendTarget;
import org.zend.sdklib.target.IZendTarget;

public class TestZendTarget {

	private String id;
	private URL url;
	private String key;
	private String secretKey;

	@Before
	public void startup() throws MalformedURLException {
		id = "dev4";
		url = new URL("http://localhost");
		key = "mykey";
		secretKey = "343441";
	}

	@Test
	public void testCreateValidTarget() throws MalformedURLException {
		IZendTarget target = new ZendTarget(id, url, key, secretKey);
		assertNotNull(target);
		assertEquals(id, target.getId());
		assertEquals(key, target.getKey());
		assertEquals(secretKey, target.getSecretKey());
		assertEquals(url, target.getHost());
	}

	@Test
	public void testCreateEmptyTarget() throws MalformedURLException {
		IZendTarget target = new ZendTarget();
		assertNotNull(target);
		assertNull(target.getId());
		assertNull(target.getKey());
		assertNull(target.getSecretKey());
		assertNull(target.getHost());
	}

	@Test
	public void testLoad() throws IOException {
		IZendTarget target = new ZendTarget();
		assertNotNull(target);
		InputStream is = this.getClass().getResourceAsStream("conf");
		target.load(is);
		assertEquals(id, target.getId());
		assertEquals(key, target.getKey());
		assertEquals(secretKey, target.getSecretKey());
		assertEquals(url, target.getHost());
		assertEquals("value", target.getProperty("param"));
	}

	@Test
	public void testStore() throws IOException {
		IZendTarget target = new ZendTarget(id, url, key, secretKey);
		assertNotNull(target);
		File file = File.createTempFile("testStore",
				String.valueOf(new Random().nextInt()));
		file.createNewFile();
		OutputStream os = new FileOutputStream(file);
		target.store(os);
		IZendTarget storedTarget = new ZendTarget();
		assertNotNull(storedTarget);
		InputStream is = new FileInputStream(file);
		storedTarget.load(is);
		assertEquals(target.getId(), storedTarget.getId());
		assertEquals(target.getKey(), storedTarget.getKey());
		assertEquals(target.getSecretKey(), storedTarget.getSecretKey());
		assertEquals(target.getHost(), storedTarget.getHost());
	}

	@Test
	public void testAddProperty() throws IOException {
		ZendTarget target = new ZendTarget(id, url, key, secretKey);
		assertNotNull(target);
		String key = "test";
		String value = "11";
		target.addProperty(key, value);
		assertEquals(value, target.getProperty(key));
	}

	@Test
	public void testCreateTargetNullId() {
		IZendTarget target = new ZendTarget(null, url, key, secretKey);
		assertNotNull(target);
	}

	@Test
	public void testCreateTargetInvalidKeyFirstChar() {
		IZendTarget target = new ZendTarget(id, url, "1" + key, secretKey);
		assertNotNull(target);
	}

	@Test
	public void testCreateTargetInvalidKey() {
		IZendTarget target = new ZendTarget(id, url, "invalid.key", secretKey);
		assertNotNull(target);
	}

	@Test
	public void testCreateTargetHostWithPort() throws MalformedURLException {
		IZendTarget target = new ZendTarget(id, new URL(
				"http://localhost:11111"), key, secretKey);
		assertNotNull(target);
	}
}
