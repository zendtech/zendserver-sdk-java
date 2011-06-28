package org.zend.sdk.test.sdklib.library;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.junit.Test;
import org.zend.sdk.test.AbstractTest;
import org.zend.sdk.test.sdkcli.commands.TestCreateProjectCommand;
import org.zend.sdkcli.ParseError;
import org.zend.sdklib.application.ZendProject;
import org.zend.sdklib.application.ZendProject.TemplateApplications;
import org.zend.sdklib.mapping.MappingModelFactory;

public class TestZendProject extends AbstractTest {

	@Test
	public void testZendProjectCreation1() throws ParseError, IOException {
		String dirName = TestCreateProjectCommand.getTempFileName();
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
	public void testZendProjectCreation2() throws ParseError, IOException {
		String dirName = TestCreateProjectCommand.getTempFileName();
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
}
