package org.zend.sdk.test.sdkcli.update;

import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.junit.Test;
import org.mockito.Mockito;
import org.zend.sdkcli.update.UpdateException;
import org.zend.sdkcli.update.manager.UpdateManager;
import org.zend.sdkcli.update.parser.Version;

public class TestUpdateManager extends AbstractUpdateTest {

	@Test
	public void testUpdate() throws IOException, UpdateException {
		copyFile(new File("test/config/update/sdk.version"), new File(tmp,
				"lib/sdk.version"));
		createFile(new File(tmp, "toRemove"));
		assertFileExists(tmp, "toRemove");

		File packageFile = new File("test/config/update/package.zip");
		String deltaXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
				+ "<delta file=\"file:///" + packageFile.getAbsolutePath()
				+ "\" size=\"833\">"
				+ "<add file=\"toCopy\" dest=\"a/b/c\"/>"
				+ "<add file=\"folder/\" dest=\"a/b\"/>"
				+ "<remove file=\"toRemove\"/>" + "</delta>";
		File deltaFile = new File(tmp, "delta.xml");
		deltaFile.createNewFile();
		OutputStream out = new FileOutputStream(deltaFile);
		out.write(deltaXml.getBytes());
		closeStream(out);

		String versionsXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><versions>"
				+ "<version name=\"0.0.18\" "
				+ "range=\"[0.0.18,0.0.20]\" "
				+ "delta=\""
				+ deltaFile.getAbsolutePath()
				+ "\" />"
				+ "<version name=\"0.0.22\" "
				+ "range=\"[0.0.18,0.0.20]\" "
				+ "delta=\""
				+ deltaFile.getAbsolutePath()
				+ "\" />"
				+ "</versions>";
		File versionsFile = new File(tmp, "versions.xml");
		versionsFile.createNewFile();
		out = new FileOutputStream(versionsFile);
		out.write(versionsXml.getBytes());
		closeStream(out);

		UpdateManager manager = new UpdateManager(tmp.getAbsolutePath(),
				versionsFile.getAbsolutePath());
		assertNotNull(manager.getSdkVersion());

		manager.performUpdate();

		assertFileNotExists(tmp, "toRemove");
		assertFileExists(tmp, "a/b/c/toCopy");
		assertFileExists(tmp, "a/b/folder/innerFile");
		assertFileExists(tmp, "a/b/folder/innerFolder/nextFile");
	}

	@Test
	public void testUpToDate() throws IOException, UpdateException {
		copyFile(new File("test/config/update/sdk.version"), new File(tmp,
				"lib/sdk.version"));
		UpdateManager manager = Mockito.spy(new UpdateManager(tmp
				.getAbsolutePath(), "test/config/update/versions.xml"));
		Version version = new Version("0.0.22");
		Mockito.doReturn(version).when(manager).getSdkVersion();
		manager.performUpdate();
	}

}
