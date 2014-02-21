package org.zend.webapi.test.server.utils;

import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.restlet.ext.xml.DomRepresentation;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class ServerUtils {

	public static final String CONFIG = "test/config/";

	public static String createXMLFileName(String requestName) {
		return ServerUtils.CONFIG + requestName + ".xml";
	}

	public static String createFileName(String requestName) {
		return ServerUtils.CONFIG + requestName;
	}

	public static Document readXMLFile(String fileName) {
		File file = new File(fileName);
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder db = factory.newDocumentBuilder();
			return db.parse(file);
		} catch (SAXException e) {
			fail("Error during parsing configuration file: " + fileName);
		} catch (IOException e) {
			fail("Error during reading configuration file: " + fileName);
		} catch (ParserConfigurationException e) {
			fail("XML parser configuration error: " + fileName);
		}
		return null;
	}

	public static DomRepresentation readDomRepresentation(String fileName)
			throws IOException {
		Document doc = readXMLFile(fileName);
		DomRepresentation dom = new DomRepresentation();
		dom.setDocument(doc);
		return dom;
	}

}
