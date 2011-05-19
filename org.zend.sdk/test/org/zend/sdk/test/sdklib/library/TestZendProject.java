package org.zend.sdk.test.sdklib.library;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.zend.sdk.test.AbstractTest;
import org.zend.sdkcli.ParseError;
import org.zend.sdklib.ZendProject;

public class TestZendProject extends AbstractTest {

	@Test
	public void testZendProjectCreation1() throws ParseError {
		ZendProject project = new ZendProject("name", "target", "index", "path");
		assertNotNull(project);
		assertTrue(project.create());
	}

	@Test
	public void testZendProjectCreation2() throws ParseError {
		ZendProject project = new ZendProject("name", "target", "index", null);
		assertNotNull(project);
		assertTrue(project.create());
	}

	@Test
	public void testZendProjectCreation3() throws ParseError {
		ZendProject project = new ZendProject("name", "target", null, null);
		assertNotNull(project);
		assertTrue(project.create());
	}

	@Test
	public void testZendProjectCreation4() throws ParseError {
		ZendProject project = new ZendProject("name", null, null, null);
		assertNotNull(project);
		assertTrue(project.create());
	}

}
