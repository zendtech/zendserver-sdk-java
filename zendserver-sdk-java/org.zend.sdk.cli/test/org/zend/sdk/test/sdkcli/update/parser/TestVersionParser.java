package org.zend.sdk.test.sdkcli.update.parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.zend.sdk.test.sdkcli.update.AbstractXMLTest;
import org.zend.sdkcli.update.UpdateException;
import org.zend.sdkcli.update.parser.VersionParser;

public class TestVersionParser extends AbstractXMLTest {

	@Test
	public void testValidLocation() throws UpdateException {
		VersionParser parser = new VersionParser(
				"test/config/update/versions.xml");
		assertNotNull(parser.getAvailableVersions());
	}

	@Test(expected = UpdateException.class)
	public void testInvalidLocation() throws UpdateException {
		VersionParser parser = new VersionParser(
				"test/config/update/versionsInvalid.xml");
		parser.getAvailableVersions();
	}

	@Test
	public void testNoVersions() throws UpdateException {
		VersionParser parser = new VersionParser("test/config/update/delta.xml");
		assertEquals(0, parser.getAvailableVersions().size());
	}

	@Test(expected = UpdateException.class)
	public void testNoFile() throws UpdateException {
		VersionParser parser = new VersionParser(
				"test/config/update/noExist.xml");
		assertEquals(0, parser.getAvailableVersions().size());
	}

}
