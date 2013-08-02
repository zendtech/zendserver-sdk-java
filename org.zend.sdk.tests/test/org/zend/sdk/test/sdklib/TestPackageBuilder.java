package org.zend.sdk.test.sdklib;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.zend.sdk.test.AbstractTest;
import org.zend.sdklib.application.PackageBuilder;
import org.zend.sdklib.internal.library.AbstractChangeNotifier;
import org.zend.sdklib.mapping.IVariableResolver;
import org.zend.webapi.core.progress.IStatus;
import org.zend.webapi.core.progress.StatusCode;

public class TestPackageBuilder extends AbstractTest {

	public static final String FOLDER = "test/config/apps/";
	private static final int BUFFER = 2048;

	private File file;


	private class TestNotifier extends AbstractChangeNotifier {
		private List<IStatus> history = new ArrayList<IStatus>();

		@Override
		public void statusChanged(IStatus status) {
			history.add(status);
		}

		public List<IStatus> getHistory() {
			return history;
		}
	}

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
		PackageBuilder builder = new PackageBuilder(new File(FOLDER
				+ "Project1"));
		File result = builder.createDeploymentPackage(file.getCanonicalPath());
		assertNotNull(result);
		assertTrue(result.exists());
	}
	
	@Test
	public void testCreatePackageEmptyAppdir() throws IOException {
		PackageBuilder builder = new PackageBuilder(new File(FOLDER
				+ "Project7"));
		File result = builder.createDeploymentPackage(file.getCanonicalPath());
		assertNotNull(result);
		assertTrue(result.exists());
		unzip(result);
		File parent = result.getParentFile();
		assertTrue(new File(parent, "/data/").exists());
	}

	@Test
	public void testCreatePackageNoAppdir() throws IOException {
		PackageBuilder builder = new PackageBuilder(new File(FOLDER
				+ "Project8"));
		File result = builder.createDeploymentPackage(file.getCanonicalPath());
		assertNotNull(result);
		assertTrue(result.exists());
		unzip(result);
		File parent = result.getParentFile();
		assertTrue(new File(parent, "/include_it").exists());
	}

	@Test
	public void testCreatePackageNoScriptsdir() throws IOException {
		PackageBuilder builder = new PackageBuilder(new File(FOLDER
				+ "Project8"));
		File result = builder.createDeploymentPackage(file.getCanonicalPath());
		assertNotNull(result);
		assertTrue(result.exists());
		unzip(result);
		File parent = result.getParentFile();
		assertTrue(new File(parent, "/include_it").exists());
		assertFalse(new File(parent, "scripts/include_it").exists());
	}

	@Test
	public void testCreatePackageFile() throws IOException {
		PackageBuilder builder = new PackageBuilder(new File(FOLDER
				+ "Project1"));
		File result = builder.createDeploymentPackage(file);
		assertNotNull(result);
		assertTrue(result.exists());
	}

	@Test
	public void testPackageContent() throws IOException {
		PackageBuilder builder = new PackageBuilder(new File(FOLDER + "Project1"));
		File result = builder.createDeploymentPackage(file.getCanonicalPath());
		assertNotNull(result);
		assertTrue(result.exists());
		unzip(result);
		File parent = result.getParentFile();
		assertTrue(new File(parent, "/data/public/inner_public").exists());
		assertTrue(new File(parent, "/eula/license.txt").exists());
		assertTrue(new File(parent, "/icon/appicon.png").exists());
	}

	@Test
	public void testCreatePackageNoDescriptor() throws IOException {
		PackageBuilder builder = new PackageBuilder(new File(FOLDER
				+ "Project2"));
		File result = builder.createDeploymentPackage(file);
		assertNull(result);
	}

	@Test(expected = IllegalStateException.class)
	public void testCreatePackageNoVersion() throws IOException {
		PackageBuilder builder = new PackageBuilder(new File(FOLDER
				+ "Project3"));
		builder.createDeploymentPackage(file);
	}

	@Test(expected = IllegalStateException.class)
	public void testCreatePackageInvalidDescriptor() throws IOException {
		PackageBuilder builder = new PackageBuilder(new File(FOLDER
				+ "Project4"));
		builder.createDeploymentPackage(file);
	}

	@Test
	public void testCreatePackageNoMapping() throws IOException {
		PackageBuilder builder = new PackageBuilder(new File(FOLDER
				+ "Project5"));
		File result = builder.createDeploymentPackage(file);
		assertNotNull(result);
	}

	@Test
	public void testCreatePackageNullLocation() throws IOException {
		PackageBuilder builder = new PackageBuilder(new File(FOLDER
				+ "Project1"));
		File result = builder.createDeploymentPackage((File) null);
		assertNull(result);
	}

	@Test
	public void testCreatePackageNotification() throws IOException {
		TestNotifier notifier = new TestNotifier();
		PackageBuilder builder = new PackageBuilder(new File(FOLDER
				+ "Project1"), notifier);
		File result = builder.createDeploymentPackage(file);
		assertNotNull(result);
		assertTrue(result.exists());
		List<IStatus> statuses = notifier.getHistory();
		assertEquals(StatusCode.STARTING, statuses.get(0).getCode());
		assertEquals(StatusCode.STOPPING, statuses.get(statuses.size() - 1)
				.getCode());
		assertEquals(5, statuses.size() - 2);
	}
	
	@Test
	public void testCreatePackageVariableResolver() throws IOException {
		PackageBuilder builder = new PackageBuilder(new File(FOLDER
				+ "Project9"));
		builder.setVariableResolver(new IVariableResolver() {
			
			public String resolve(String path) {
				String varToReplace = "testVariable";
				return path.replaceAll(varToReplace, "a");
			}
		});
		File result = builder.createDeploymentPackage(file.getCanonicalPath());
		assertNotNull(result);
		assertTrue(result.exists());
		unzip(result);
		File parent = result.getParentFile();
		assertTrue(new File(parent, "data/include_it").exists());
		assertTrue(new File(parent, "data/file").exists());
	}

	public void unzip(File packageFile) {
		try {
			BufferedOutputStream dest = null;
			FileInputStream in = new FileInputStream(packageFile);
			File parent = packageFile.getParentFile();
			ZipInputStream zipStream = new ZipInputStream(new BufferedInputStream(in));
			ZipEntry entry;
			while ((entry = zipStream.getNextEntry()) != null) {
				int count;
				byte data[] = new byte[BUFFER];
				File file = new File(parent, entry.getName());
				createParents(file.getParentFile(), parent);
				if (entry.getName().endsWith("/")) {
					file.mkdir();
				} else {
					file.getParentFile().mkdirs();
					file.createNewFile();
					FileOutputStream out = new FileOutputStream(file);
					dest = new BufferedOutputStream(out, BUFFER);
					while ((count = zipStream.read(data, 0, BUFFER)) != -1) {
						dest.write(data, 0, count);
					}
					dest.flush();
					dest.close();
				}
			}
			zipStream.close();
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	private boolean createParents(File file, File root) {
		File parent = file.getParentFile();
		if (parent.equals(root)) {
			file.mkdir();
			return true;
		} else {
			if (!parent.exists()) {
				if (createParents(parent, root)) {
					file.mkdir();
					return true;
				}
			}
		}
		return true;
	}
}
