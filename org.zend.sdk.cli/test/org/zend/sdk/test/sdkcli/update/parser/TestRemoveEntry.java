package org.zend.sdk.test.sdkcli.update.parser;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.junit.Test;
import org.w3c.dom.Node;
import org.zend.sdk.test.sdkcli.update.AbstractXMLTest;
import org.zend.sdkcli.update.UpdateException;
import org.zend.sdkcli.update.parser.RemoveEntry;

public class TestRemoveEntry extends AbstractXMLTest {

	@Test
	public void testRemoveFile() throws UpdateException, IOException {
		String xmlString = "<remove file=\"toRemove\"/>";
		Node node = getNodeFromString(xmlString, "remove");
		RemoveEntry entry = new RemoveEntry(node);
		// create file to remove
		createFile(new File(tmp, "toRemove"));
		assertTrue(entry.execute(tmp));
		assertFileNotExists(tmp, "toRemove");
	}

	@Test
	public void testRemoveFiles() throws UpdateException, IOException {
		String xmlString = "<remove file=\"update/\"/>";
		Node node = getNodeFromString(xmlString, "remove");
		RemoveEntry entry = new RemoveEntry(node);
		// create files to copy
		createFile(new File(tmp, "update/a/b"));
		createFile(new File(tmp, "update/a/w/c"));
		createFile(new File(tmp, "update/a/z/d"));
		assertTrue(entry.execute(tmp));
		assertFileNotExists(tmp, "update/a/b");
		assertFileNotExists(tmp, "update/a/w/c");
		assertFileNotExists(tmp, "update/a/z/d");
	}

	@Test
	public void testRemoveAsterisk() throws UpdateException, IOException {
		String xmlString = "<remove file=\"update/*\"/>";
		Node node = getNodeFromString(xmlString, "remove");
		RemoveEntry entry = new RemoveEntry(node);
		// create files to copy
		createFile(new File(tmp, "update/a/b"));
		createFile(new File(tmp, "update/a/w/c"));
		createFile(new File(tmp, "update/a/z/d"));
		assertTrue(entry.execute(tmp));
		assertFileNotExists(tmp, "update/a/b");
		assertFileNotExists(tmp, "update/a/w/c");
		assertFileNotExists(tmp, "update/a/z/d");
		assertFileExists(tmp, "update");
	}

	@Test
	public void testExclude() throws UpdateException, IOException {
		String xmlString = "<remove file=\"update/*\" exclude=\"b|c\"/>";
		Node node = getNodeFromString(xmlString, "remove");
		RemoveEntry entry = new RemoveEntry(node);
		// create files to copy
		createFile(new File(tmp, "update/a/b"));
		createFile(new File(tmp, "update/a/w/c"));
		createFile(new File(tmp, "update/a/z/d"));
		assertTrue(entry.execute(tmp));
		assertFileExists(tmp, "update/a/b");
		assertFileExists(tmp, "update/a/w/c");
		assertFileNotExists(tmp, "update/a/z/d");
		assertFileExists(tmp, "update");
	}

	@Test
	public void testCopyUnexistingFile() throws UpdateException, IOException {
		String xmlString = "<remove file=\"notExist/\"/>";
		Node node = getNodeFromString(xmlString, "remove");
		RemoveEntry entry = new RemoveEntry(node);
		assertTrue(entry.execute(tmp));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testNoFile() throws UpdateException, IOException {
		String xmlString = "<remove />";
		Node node = getNodeFromString(xmlString, "remove");
		new RemoveEntry(node);
	}

}
