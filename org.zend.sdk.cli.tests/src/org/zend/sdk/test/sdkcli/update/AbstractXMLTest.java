package org.zend.sdk.test.sdkcli.update;

import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.zend.sdkcli.update.UpdateException;

public class AbstractXMLTest extends AbstractUpdateTest {

	protected Node getNodeFromFile(String path, String name) {
		Document doc;
		try {
			doc = parseXML(path);
			NodeList elements = doc.getElementsByTagName(name);
			if (elements.getLength() > 0) {
				return elements.item(0);
			}
		} catch (UpdateException e) {
			fail(e.getMessage());
		}
		return null;
	}

	protected Node getNodeFromString(String xmlString, String name)
			throws UpdateException {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(new ByteArrayInputStream(xmlString
					.getBytes()));
			NodeList elements = doc.getElementsByTagName(name);
			if (elements.getLength() > 0) {
				return elements.item(0);
			}
		} catch (Exception e) {
			fail(e.getMessage());
		}
		return null;
	}

	private Document parseXML(String path) throws UpdateException {
		try {
			return parseXML(new FileInputStream(new File(path)));
		} catch (FileNotFoundException e) {
			throw new UpdateException(e);
		}
	}

	private Document parseXML(InputStream input) throws UpdateException {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder db = dbf.newDocumentBuilder();
			return db.parse(input);
		} catch (Exception e) {
			throw new UpdateException(e);
		}
	}

}
