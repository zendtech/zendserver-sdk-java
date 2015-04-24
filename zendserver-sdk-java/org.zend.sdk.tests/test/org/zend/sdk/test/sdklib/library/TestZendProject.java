package org.zend.sdk.test.sdklib.library;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.junit.Test;
import org.zend.sdk.test.AbstractTest;
import org.zend.sdklib.application.ZendProject;
import org.zend.sdklib.application.ZendProject.TemplateApplications;
import org.zend.sdklib.mapping.MappingModelFactory;

public class TestZendProject extends AbstractTest {

	public static final String FOLDER = "test/config/apps/";

	@Test
	public void testZendProjectCreation1() throws IOException {
		String dirName = getTempFileName();
		ZendProject project = new ZendProject(new File(dirName));
		assertTrue(project.create("name", TemplateApplications.SIMPLE, "all"));
		assertTrue(new File(dirName + "/"
				+ MappingModelFactory.DEPLOYMENT_PROPERTIES).exists());
		assertTrue(new File(dirName + "/deployment.xml").exists());
		assertTrue(new File(dirName + "/public/index.html").exists());
		assertTrue(new File(dirName + "/scripts/post_activate.php").exists());
		assertTrue(new File(dirName + "/scripts/post_deactivate.php").exists());
		assertTrue(new File(dirName + "/scripts/post_stage.php").exists());
		assertTrue(new File(dirName + "/scripts/post_unstage.php").exists());
		assertTrue(new File(dirName + "/scripts/pre_activate.php").exists());
		assertTrue(new File(dirName + "/scripts/pre_deactivate.php").exists());
		assertTrue(new File(dirName + "/scripts/pre_stage.php").exists());
		assertTrue(new File(dirName + "/scripts/pre_unstage.php").exists());
	}

	@Test
	public void testZendProjectCreationZF() throws IOException {
		String dirName = getTempFileName();
		ZendProject project = new ZendProject(new File(dirName));
		assertTrue(project.create("name", TemplateApplications.ZEND, "all"));
		assertTrue(new File(dirName + "/"
				+ MappingModelFactory.DEPLOYMENT_PROPERTIES).exists());
		assertTrue(new File(dirName + "/deployment.xml").exists());
		assertTrue(new File(dirName + "/public/index.php").exists());
		assertTrue(new File(dirName + "/scripts/post_activate.php").exists());
		assertTrue(new File(dirName + "/scripts/post_deactivate.php").exists());
		assertTrue(new File(dirName + "/scripts/post_stage.php").exists());
		assertTrue(new File(dirName + "/scripts/post_unstage.php").exists());
		assertTrue(new File(dirName + "/scripts/pre_activate.php").exists());
		assertTrue(new File(dirName + "/scripts/pre_deactivate.php").exists());
		assertTrue(new File(dirName + "/scripts/pre_stage.php").exists());
		assertTrue(new File(dirName + "/scripts/pre_unstage.php").exists());
		assertTrue(new File(dirName + "/application/Bootstrap.php").exists());
	}

	@Test
	public void testZendProjectCreationQuickstart() throws IOException {
		String dirName = getTempFileName();
		ZendProject project = new ZendProject(new File(dirName));
		assertTrue(project.create("name", TemplateApplications.QUICKSTART,
				"all"));
		assertTrue(new File(dirName + "/"
				+ MappingModelFactory.DEPLOYMENT_PROPERTIES).exists());
		assertTrue(new File(dirName + "/deployment.xml").exists());
		assertTrue(new File(dirName + "/public/index.php").exists());
		assertTrue(new File(dirName + "/scripts/post_activate.php").exists());
		assertTrue(new File(dirName + "/scripts/post_deactivate.php").exists());
		assertTrue(new File(dirName + "/scripts/post_stage.php").exists());
		assertTrue(new File(dirName + "/scripts/post_unstage.php").exists());
		assertTrue(new File(dirName + "/scripts/pre_activate.php").exists());
		assertTrue(new File(dirName + "/scripts/pre_deactivate.php").exists());
		assertTrue(new File(dirName + "/scripts/pre_stage.php").exists());
		assertTrue(new File(dirName + "/scripts/pre_unstage.php").exists());
		assertTrue(new File(dirName + "/application/Bootstrap.php").exists());
		assertTrue(new File(dirName
				+ "/application/controllers/GuestbookController.php").exists());
	}

	@Test
	public void testZendProjectCreation2() throws IOException {
		String dirName = getTempFileName();
		ZendProject project = new ZendProject(new File(dirName));
		assertTrue(project.create("name", TemplateApplications.SIMPLE, "all"));
		assertTrue(new File(dirName + "/"
				+ MappingModelFactory.DEPLOYMENT_PROPERTIES).exists());
		assertTrue(new File(dirName + "/deployment.xml").exists());
		assertTrue(new File(dirName + "/public/index.html").exists());
		assertTrue(new File(dirName + "/scripts/post_activate.php").exists());
		assertTrue(new File(dirName + "/scripts/post_deactivate.php").exists());
		assertTrue(new File(dirName + "/scripts/post_stage.php").exists());
		assertTrue(new File(dirName + "/scripts/post_unstage.php").exists());
		assertTrue(new File(dirName + "/scripts/pre_activate.php").exists());
		assertTrue(new File(dirName + "/scripts/pre_deactivate.php").exists());
		assertTrue(new File(dirName + "/scripts/pre_stage.php").exists());
		assertTrue(new File(dirName + "/scripts/pre_unstage.php").exists());
	}

	@Test
	public void testZendProjectUpdate() throws IOException {
		String dirName = getTempFileName();
		File dest = new File(dirName);
		File src = new File(FOLDER + "Project6");
		copyProject(src, dest, src.getParentFile().getAbsolutePath());
		File projectFile = new File(dirName + File.separator + "Project6");
		ZendProject project = new ZendProject(projectFile);
		assertTrue(project.update("all"));
		assertTrue(new File(projectFile,
				MappingModelFactory.DEPLOYMENT_PROPERTIES).exists());
		assertTrue(new File(projectFile, "deployment.xml").exists());
		assertTrue(new File(projectFile, "other/post_activate.php").exists());
		assertTrue(new File(projectFile, "other/post_deactivate.php").exists());
		assertTrue(new File(projectFile, "other/post_stage.php").exists());
		assertTrue(new File(projectFile, "other/post_unstage.php").exists());
		assertTrue(new File(projectFile, "other/pre_activate.php").exists());
		assertTrue(new File(projectFile, "other/pre_deactivate.php").exists());
		assertTrue(new File(projectFile, "other/pre_stage.php").exists());
		assertTrue(new File(projectFile, "other/pre_unstage.php").exists());
	}

	private void copyProject(File file, File dest, String root)
			throws IOException {
		if (file.isDirectory()) {
			String absolutePath = file.getAbsolutePath();
			String newPath = absolutePath.substring(root.length());
			new File(dest, newPath).mkdir();
			File[] children = file.listFiles();
			for (File child : children) {
				copyProject(child, dest, root);
			}
		} else {
			String absolutePath = file.getAbsolutePath();
			String newPath = absolutePath.substring(root.length());
			copyFile(file, new File(dest, newPath));
		}
	}

	public void copyFile(File in, File out) throws IOException {
		out.createNewFile();
		FileInputStream fis = new FileInputStream(in.getAbsolutePath());
		FileOutputStream fos = new FileOutputStream(out);
		try {
			byte[] buf = new byte[1024];
			int i = 0;
			while ((i = fis.read(buf)) != -1) {
				fos.write(buf, 0, i);
			}
		} finally {
			if (fis != null)
				fis.close();
			if (fos != null)
				fos.close();
		}
	}

	public static String getTempFileName() throws IOException {
		File temp = File.createTempFile("temp", "tst");
		temp.delete();
		temp.mkdir();
		return temp.getAbsolutePath();
	}

}
