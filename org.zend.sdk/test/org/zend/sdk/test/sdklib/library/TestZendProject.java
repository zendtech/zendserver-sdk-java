package org.zend.sdk.test.sdklib.library;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.junit.Test;
import org.zend.sdk.test.AbstractTest;
import org.zend.sdk.test.sdkcli.commands.TestCreateProjectCommand;
import org.zend.sdkcli.ParseError;
import org.zend.sdklib.application.ZendProject;
import org.zend.sdklib.application.ZendProject.SampleApplications;

public class TestZendProject extends AbstractTest {

	@Test
	public void testZendProjectCreation1() throws ParseError, IOException {
		String dirName = TestCreateProjectCommand.getTempFileName();
		ZendProject project = new ZendProject(new File(dirName));
		assertTrue(project.create("name", SampleApplications.HELLO_WORLD, "all"));

		assertTrue(new File(dirName + "/name/deployment.xml").exists());
		assertTrue(new File(dirName + "/name/public/index.html").exists());
		assertTrue(new File(dirName + "/name/scripts/post_activate.php")
				.exists());
		assertTrue(new File(dirName + "/name/scripts/post_deactivate.php")
				.exists());
		assertTrue(new File(dirName + "/name/scripts/post_stage.php").exists());
		assertTrue(new File(dirName + "/name/scripts/post_unstage.php")
				.exists());
		assertTrue(new File(dirName + "/name/scripts/pre_activate.php")
				.exists());
		assertTrue(new File(dirName + "/name/scripts/pre_deactivate.php")
				.exists());
		assertTrue(new File(dirName + "/name/scripts/pre_stage.php").exists());
		assertTrue(new File(dirName + "/name/scripts/pre_unstage.php").exists());
	}

	@Test
	public void testZendProjectCreation2() throws ParseError, IOException {
		String dirName = TestCreateProjectCommand.getTempFileName();
		ZendProject project = new ZendProject(new File(dirName));
		assertTrue(project.create("name", SampleApplications.HELLO_WORLD, "all"));

		assertTrue(new File(dirName + "/name/deployment.xml").exists());
		assertTrue(new File(dirName + "/name/public/index.html").exists());
		assertTrue(new File(dirName + "/name/scripts/post_activate.php")
				.exists());
		assertTrue(new File(dirName + "/name/scripts/post_deactivate.php")
				.exists());
		assertTrue(new File(dirName + "/name/scripts/post_stage.php").exists());
		assertTrue(new File(dirName + "/name/scripts/post_unstage.php")
				.exists());
		assertTrue(new File(dirName + "/name/scripts/pre_activate.php")
				.exists());
		assertTrue(new File(dirName + "/name/scripts/pre_deactivate.php")
				.exists());
		assertTrue(new File(dirName + "/name/scripts/pre_stage.php").exists());
		assertTrue(new File(dirName + "/name/scripts/pre_unstage.php").exists());
	}

	@Test
	public void testZendProjectCreation3() throws ParseError, IOException {
		String dirName = TestCreateProjectCommand.getTempFileName();
		ZendProject project = new ZendProject(new File(dirName));
		assertTrue(project.create("name", SampleApplications.HELLO_WORLD, "all"));

		assertTrue(new File(dirName + "/name/deployment.xml").exists());
		assertTrue(new File(dirName + "/name/public/index.html").exists());
		assertFalse(new File(dirName + "/name/scripts/post_activate.php")
				.exists());
		assertFalse(new File(dirName + "/name/scripts/post_deactivate.php")
				.exists());
		assertFalse(new File(dirName + "/name/scripts/post_stage.php").exists());
		assertFalse(new File(dirName + "/name/scripts/post_unstage.php")
				.exists());
		assertFalse(new File(dirName + "/name/scripts/pre_activate.php")
				.exists());
		assertFalse(new File(dirName + "/name/scripts/pre_deactivate.php")
				.exists());
		assertFalse(new File(dirName + "/name/scripts/pre_stage.php").exists());
		assertFalse(new File(dirName + "/name/scripts/pre_unstage.php")
				.exists());
	}

	@Test
	public void testZendProjectUpdate1() throws ParseError, IOException {
		String dirName = TestCreateProjectCommand.getTempFileName();
		ZendProject project = new ZendProject(new File(dirName));
		assertTrue(project.update("all"));

		assertTrue(new File(dirName + "/deployment.xml").exists());
		assertFalse(new File(dirName + "/public").exists());
		assertFalse(new File(dirName + "/public/index.html").exists());
		assertFalse(new File(dirName + "/scripts").exists());
		assertFalse(new File(dirName + "/scripts/post_activate.php").exists());
		assertFalse(new File(dirName + "/scripts/post_deactivate.php").exists());
		assertFalse(new File(dirName + "/scripts/post_stage.php").exists());
		assertFalse(new File(dirName + "/scripts/post_unstage.php").exists());
		assertFalse(new File(dirName + "/scripts/pre_activate.php").exists());
		assertFalse(new File(dirName + "/scripts/pre_deactivate.php").exists());
		assertFalse(new File(dirName + "/scripts/pre_stage.php").exists());
		assertFalse(new File(dirName + "/scripts/pre_unstage.php").exists());
	}

}
