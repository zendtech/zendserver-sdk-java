package org.zend.webapi.test.server.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.restlet.ext.xml.DomRepresentation;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.zend.webapi.core.connection.response.ResponseCode;
import org.zend.webapi.test.Configuration;
import org.zend.webapi.test.connection.services.TestCodeTracingServices;
import org.zend.webapi.test.connection.services.TestServerConfiguration;
import org.zend.webapi.test.server.response.FileResponse;
import org.zend.webapi.test.server.response.ServerResponse;
import org.zend.webapi.test.server.response.ServiceResponse;

public class ResponseFactory {

	public static ServerResponse createConfigResponse(String requestName,
			ResponseCode code) throws IOException {
		String name = TestServerConfiguration.CONFIG_FOLDER
				+ TestServerConfiguration.EXAMLE_CONFIG;
		File file = new File(ServerUtils.createFileName(name));
		FileInputStream inputStream = new FileInputStream(file);
		try {
		int size = (int) file.length();
		byte content[] = new byte[size];
			inputStream.read(content);
			return new FileResponse(code.getCode(),
					TestServerConfiguration.EXAMLE_CONFIG, size, content);
		} finally {
			if (inputStream != null) {
				inputStream.close();
			}
		}
	}

	public static ServerResponse createCodeTraceResponse(String requestName,
			ResponseCode code) throws IOException {
		String name = TestCodeTracingServices.CONFIG_FOLDER
				+ TestCodeTracingServices.EXAMPLE_CODE_TRACE;
		File file = new File(ServerUtils.createFileName(name));
		FileInputStream inputStream = new FileInputStream(file);
		try {
			int size = (int) file.length();
			byte content[] = new byte[size];
			inputStream.read(content);
			return new FileResponse(code.getCode(),
					TestCodeTracingServices.EXAMPLE_CODE_TRACE, size, content);
		} finally {
			if (inputStream != null) {
				inputStream.close();
			}
		}
	}
	
	public static ServerResponse createFileResponse(String requestName,
			ResponseCode code, String folder, String name) throws IOException {
		File file = new File(ServerUtils.createFileName(folder + name));
		FileInputStream inputStream = new FileInputStream(file);
		try {
			int size = (int) file.length();
			byte content[] = new byte[size];
			inputStream.read(content);
			return new FileResponse(code.getCode(), name, size, content);
		} finally {
			if (inputStream != null) {
				inputStream.close();
			}
		}
	}

	public static ServiceResponse createResponse(String requestName,
			ResponseCode code) throws IOException {
		String file = ServerUtils.createXMLFileName("responseBody");
		DomRepresentation dom = ServerUtils.readDomRepresentation(file);
		setRequestData(requestName, dom);
		Node responseData = dom.getNode("/zendServerAPIResponse/responseData");
		responseData.appendChild(getResponseData(dom, requestName));
		ServiceResponse result = null;
		result = new ServiceResponse(code.getCode(), dom.getDocument());
		return result;
	}

	public static ServiceResponse createErrorResponse(String requestName,
 ResponseCode code)
			throws IOException {
		String file = ServerUtils.createXMLFileName("errorBody");
		DomRepresentation dom = ServerUtils.readDomRepresentation(file);
		setRequestData(requestName, dom);
		Node root = dom.getNode("/zendServerAPIResponse");
		root.appendChild(getResponseData(dom, String.valueOf(code.getCode())));
		ServiceResponse result = null;
		result = new ServiceResponse(code.getCode(), dom.getDocument());
		return result;
	}

	private static void setRequestData(String requestName, DomRepresentation dom) {
		Node key = dom.getNode("/zendServerAPIResponse/requestData/apiKeyName");
		key.setTextContent(Configuration.getKeyName());
		Node method = dom.getNode("/zendServerAPIResponse/requestData/method");
		method.setTextContent(requestName);
	}

	private static Node getResponseData(DomRepresentation dom,
			String requestName) throws IOException {
		String file = ServerUtils.createXMLFileName(requestName);
		Document data = ServerUtils.readXMLFile(file);
		Document domDoc = dom.getDocument();
		return domDoc.importNode(data.getFirstChild(), true);
	}

}
