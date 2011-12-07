package org.zend.sdk.test.sdkcli.update;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;

import org.junit.After;
import org.junit.Before;

public class AbstractUpdateTest {

	protected File tmp;

	@Before
	public void startUp() {
		final String tempDir = System.getProperty("java.io.tmpdir");
		tmp = new File(tempDir + File.separator + new Random().nextInt());
		assertTrue(tmp.mkdir());
	}

	@After
	public void shutdown() {
		delete(tmp);
	}

	protected void createFile(File file) throws IOException {
		File parent = file.getParentFile();
		if (!parent.exists()) {
			assertTrue(parent.mkdirs());
		}
		assertTrue(file.createNewFile());
	}

	protected void copyFile(File in, File out) throws IOException {
		createFile(out);
		FileInputStream fis = new FileInputStream(in);
		FileOutputStream fos = new FileOutputStream(out);
		try {
			byte[] buf = new byte[4096];
			int i = 0;
			while ((i = fis.read(buf)) != -1) {
				fos.write(buf, 0, i);
			}
		} finally {
			closeStream(fis);
			closeStream(fos);
		}
	}

	protected void assertFileExists(File tmp, String name) {
		assertTrue(new File(tmp, name).exists());
	}

	protected void assertFileNotExists(File tmp, String name) {
		assertFalse(new File(tmp, name).exists());
	}

	protected boolean delete(File file) {
		if (file == null || !file.exists()) {
			return true;
		}
		if (file.isDirectory()) {
			String[] children = file.list();
			for (int i = 0; i < children.length; i++) {
				boolean result = delete(new File(file, children[i]));
				if (!result) {
					return false;
				}
			}
		}
		return file.delete();
	}

	protected void closeStream(Closeable stream) throws IOException {
		if (stream != null) {
			stream.close();
		}
	}

}
