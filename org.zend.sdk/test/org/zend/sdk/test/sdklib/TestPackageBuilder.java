package org.zend.sdk.test.sdklib;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.Random;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.zend.sdk.test.AbstractTest;
import org.zend.sdklib.PackageBuilder;

public class TestPackageBuilder extends AbstractTest {

	public static final String FOLDER = "test/config/apps/";
	private File file;

	@Before
	public void startup() {
		final String tempDir = System.getProperty("java.io.tmpdir");
		file = new File(tempDir + File.separator + new Random().nextInt());
		file.mkdir();
	}

	@After
	public void shutdown() {
		file.deleteOnExit();
	}

	@Test
	public void testCreatePackagePath() throws IOException {
		PackageBuilder builder = new PackageBuilder(FOLDER + "Project1");
		File result = builder.createDeploymentPackage(file.getCanonicalPath());
		assertNotNull(result);
		assertTrue(result.exists());
	}

	@Test
	public void testCreatePackageFile() throws IOException {
		PackageBuilder builder = new PackageBuilder(FOLDER + "Project1");
		File result = builder.createDeploymentPackage(file);
		assertNotNull(result);
		assertTrue(result.exists());
	}

	@Test
	public void testCreatePackageNoDescriptor() throws IOException {
		PackageBuilder builder = new PackageBuilder(FOLDER + "Project2");
		File result = builder.createDeploymentPackage(file);
		assertNull(result);
	}

	@Test
	public void testCreatePackageNoVersion() throws IOException {
		PackageBuilder builder = new PackageBuilder(FOLDER + "Project3");
		File result = builder.createDeploymentPackage(file);
		assertNull(result);
	}

	@Test
	public void testCreatePackageInvalidDescriptor() throws IOException {
		PackageBuilder builder = new PackageBuilder(FOLDER + "Project4");
		File result = builder.createDeploymentPackage(file);
		assertNull(result);
	}

	@Test
	public void testCreatePackageNullLocation() throws IOException {
		PackageBuilder builder = new PackageBuilder(FOLDER + "Project1");
		File result = builder.createDeploymentPackage((File) null);
		assertNull(result);
	}

}
