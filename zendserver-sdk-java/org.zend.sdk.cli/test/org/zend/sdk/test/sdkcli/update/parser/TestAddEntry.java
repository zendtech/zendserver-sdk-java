package org.zend.sdk.test.sdkcli.update.parser;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.junit.Test;
import org.w3c.dom.Node;
import org.zend.sdk.test.sdkcli.update.AbstractXMLTest;
import org.zend.sdkcli.update.UpdateException;
import org.zend.sdkcli.update.parser.AddEntry;

public class TestAddEntry extends AbstractXMLTest {

	@Test
	public void testCopyFile() throws UpdateException, IOException {
		String xmlString = "<add file=\"toCopy\" dest=\"a/b/c\"/>";
		Node node = getNodeFromString(xmlString, "add");
		AddEntry entry = new AddEntry(node, tmp);
		// create file to copy
		createFile(new File(tmp, "toCopy"));
		assertTrue(entry.execute(tmp));
		assertFileExists(tmp, "a/b/c/toCopy");
	}

	@Test
	public void testCopyFiles() throws UpdateException, IOException {
		String xmlString = "<add file=\"update/\" dest=\"abc\"/>";
		Node node = getNodeFromString(xmlString, "add");
		AddEntry entry = new AddEntry(node, tmp);
		// create files to copy
		createFile(new File(tmp, "update/a/b"));
		createFile(new File(tmp, "update/a/w/c"));
		createFile(new File(tmp, "update/a/z/d"));
		assertTrue(entry.execute(tmp));
		assertFileExists(tmp, "abc/update/a/b");
		assertFileExists(tmp, "abc/update/a/w/c");
		assertFileExists(tmp, "abc/update/a/z/d");
	}

	@Test
	public void testOverwrite() throws UpdateException, IOException {
		String xmlString = "<add file=\"toCopy\" />";
		Node node = getNodeFromString(xmlString, "add");
		AddEntry entry = new AddEntry(node, tmp);
		// create file to copy
		createFile(new File(tmp, "toCopy"));
		assertTrue(entry.execute(tmp));
		assertFileExists(tmp, "toCopy");
	}

	@Test(expected = UpdateException.class)
	public void testCopyUnexistingFile() throws UpdateException, IOException {
		String xmlString = "<add file=\"notExist/\" dest=\"abc\"/>";
		Node node = getNodeFromString(xmlString, "add");
		AddEntry entry = new AddEntry(node, tmp);
		entry.execute(tmp);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testNoFile() throws UpdateException, IOException {
		String xmlString = "<add dest=\"abc\"/>";
		Node node = getNodeFromString(xmlString, "add");
		new AddEntry(node, tmp);
	}

}
