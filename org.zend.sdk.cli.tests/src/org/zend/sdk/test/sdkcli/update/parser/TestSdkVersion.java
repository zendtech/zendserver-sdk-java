package org.zend.sdk.test.sdkcli.update.parser;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.w3c.dom.Node;
import org.zend.sdk.test.sdkcli.update.AbstractXMLTest;
import org.zend.sdkcli.update.UpdateException;
import org.zend.sdkcli.update.parser.SdkVersion;
import org.zend.sdkcli.update.parser.Version;

public class TestSdkVersion extends AbstractXMLTest {

	@Test
	public void testValidSdkVersion() throws UpdateException {
		String xmlString = "<version name=\"0.0.22\" range=\"[0.0.18,0.0.20]\" delta=\"delta.xml\" />";
		Node versionNode = getNodeFromString(xmlString, "version");
		assertNotNull(versionNode);
		SdkVersion version = new SdkVersion(versionNode);
		assertTrue(0 == version.getVersion().compareTo(new Version("0.0.22")));
		assertNotNull(version.getRange());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testNoDelta() throws UpdateException {
		String xmlString = "<version name=\"0.0.22\" range=\"[0.0.18,0.0.20]\"/>";
		Node versionNode = getNodeFromString(xmlString, "version");
		assertNotNull(versionNode);
		new SdkVersion(versionNode);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testNoRange() throws UpdateException {
		String xmlString = "<version name=\"0.0.22\" delta=\"delta.xml\" />";
		Node versionNode = getNodeFromString(xmlString, "version");
		assertNotNull(versionNode);
		new SdkVersion(versionNode);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testNoName() throws UpdateException {
		String xmlString = "<version range=\"[0.0.18,0.0.20]\" delta=\"delta.xml\" />";
		Node versionNode = getNodeFromString(xmlString, "version");
		assertNotNull(versionNode);
		new SdkVersion(versionNode);
	}

}
