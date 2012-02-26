package org.zend.sdk.test.sdkcli.update.parser;

import static org.junit.Assert.assertNotNull;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.junit.Test;
import org.zend.sdk.test.sdkcli.update.AbstractXMLTest;
import org.zend.sdkcli.update.UpdateException;
import org.zend.sdkcli.update.parser.DeltaParser;

public class TestDeltaParser extends AbstractXMLTest {

	@Test
	public void testValidDelta() throws UpdateException {
		DeltaParser delta = new DeltaParser("test/config/update/delta.xml");
		assertNotNull(delta);
	}
	
	@Test(expected = UpdateException.class)
	public void testNotExistsDelta() throws UpdateException {
		new DeltaParser("test/config/update/notExist.xml");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testNoSize() throws UpdateException, IOException {
		String xmlString = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
				+ "<delta file=\"package.zip\">" + "</delta>";
		new DeltaParser(new ByteArrayInputStream(xmlString.getBytes()));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testNoFile() throws UpdateException, IOException {
		String xmlString = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
				+ "<delta size=\"833\">" + "</delta>";
		new DeltaParser(new ByteArrayInputStream(xmlString.getBytes()));
	}

}
