/*******************************************************************************
 * Copyright (c) Jan 30, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.webapi.core.connection.data;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.restlet.data.Disposition;
import org.restlet.engine.util.DateUtils;
import org.restlet.ext.xml.DomRepresentation;
import org.restlet.ext.xml.NodeList;
import org.restlet.ext.xml.XmlRepresentation;
import org.restlet.representation.Representation;
import org.w3c.dom.Node;
import org.zend.webapi.core.connection.data.IResponseData.ResponseType;
import org.zend.webapi.core.connection.data.values.ApplicationStatus;
import org.zend.webapi.core.connection.data.values.LicenseInfoStatus;
import org.zend.webapi.core.connection.data.values.ServerStatus;
import org.zend.webapi.core.connection.data.values.SystemEdition;
import org.zend.webapi.core.connection.data.values.SystemStatus;
import org.zend.webapi.core.connection.data.values.WebApiVersion;
import org.zend.webapi.core.connection.request.IRequest;

/**
 * Digest the low-level response into a high level response data
 * 
 * @author Roy, 2011
 * 
 *         TODO: do something with the strings
 */
public class DataDigster extends GenericResponseDataVisitor {

	private final Representation representation;
	private final IResponseData data;

	public DataDigster(IResponseData data, Representation representation) {
		this.data = data;
		this.representation = getRepresentation(representation, data);
	}

	/**
	 * Perform the digest step
	 * 
	 * @param data
	 */
	public void digest() {
		if (data != null) {
			data.accept(this);
		}
	}

	public DataDigster(IResponseData.ResponseType type,
			Representation representation) {
		this(create(type), representation);
	}

	public DataDigster(IRequest request, Representation representation) {
		this(create(request), representation);
	}

	/**
	 * Helper method to get a value out of a given XPath
	 * 
	 * @return value of the XPath
	 */
	private String getValue(String path) {
		return getValue(path, 0);
	}

	/**
	 * Helper method to get a value out of a given XPath
	 * 
	 * @return value of the XPath
	 */
	private String getValue(String path, int occurrence) {
		final NodeList nodes = ((XmlRepresentation) representation)
				.getNodes(path);
		if (nodes.size() == 0) {
			return null;
		}
		final Node node = nodes.get(occurrence);
		return node.getTextContent().trim();
	}

	/**
	 * Helper method to get a value out of a given XPath
	 * 
	 * @return value of the XPath
	 */
	private String[] getValues(String path) {
		final NodeList nodes = ((XmlRepresentation) representation)
				.getNodes(path);
		if (nodes.size() == 0) {
			return null;
		}
		String[] values = new String[nodes.size()];
		for (int i = 0; i < values.length; i++) {
			values[i] = nodes.get(i).getTextContent().trim();

		}
		return values;
	}

	public boolean preVisit(SystemInfo systemInfo) {
		String currentPath = systemInfo.getPrefix();

		final String statusName = getValue(currentPath + "/status");
		systemInfo.setStatus(SystemStatus.byName(statusName));

		final String editionName = getValue(currentPath + "/edition");
		systemInfo.setEdition(SystemEdition.byName(editionName));

		final String serverVersion = getValue(currentPath
				+ "/zendServerVersion");
		systemInfo.setVersion(serverVersion);

		final String supportedVersion = getValue(currentPath
				+ "/supportedApiVersions");
		List<WebApiVersion> versions = parseVersions(supportedVersion);
		systemInfo.setSupportedApiVersions(versions);

		final String phpVersion = getValue(currentPath + "/phpVersion");
		systemInfo.setPhpVersion(phpVersion);

		final String os = getValue(currentPath + "/operatingSystem");
		systemInfo.setOperatingSystem(os);

		systemInfo.setLicenseInfo(new LicenseInfo(currentPath
				+ "/serverLicenseInfo"));
		systemInfo.setManagerLicenseInfo(new LicenseInfo(currentPath
				+ "/managerLicenseInfo"));
		systemInfo
				.setMessageList(new MessageList(currentPath + "/messageList"));

		return true;
	}

	/**
	 * <pre>
	 * 	<status>OK</status> 
	 * 	<orderNumber>ZEND-ORDER-66</orderNumber>
	 * 	<validUntil>Sat, 31 Mar 2012 00:00:00 GMT</validUntil>
	 * 	<serverLimit>0</serverLimit>
	 * </pre>
	 */
	public boolean preVisit(LicenseInfo licenseInfo) {
		String currentPath = licenseInfo.getPrefix();

		// temp for reading values
		String value;

		value = getValue(currentPath + "/status");
		licenseInfo.setStatus(LicenseInfoStatus.byName(value));

		value = getValue(currentPath + "/orderNumber");
		licenseInfo.setOrderNumber(value);

		value = getValue(currentPath + "/validUntil");
		final Date parse = DateUtils.parse(value, DateUtils.FORMAT_RFC_1123);
		licenseInfo.setValidUntil(parse);

		value = getValue(currentPath + "/serverLimit");
		licenseInfo.setLimit(Integer.parseInt(value == null ? "0" : value));

		return true;
	}

	/**
	 * <pre>
	 * <messageList> 
	 * 	<warning>This server is waiting a PHP restart</warning>
	 * </messageList>
	 * </pre>
	 */
	public boolean preVisit(MessageList messageList) {
		String currentPath = messageList.getPrefix();
		String[] values;

		values = getValues(currentPath + "/info");
		messageList.setInfo(values != null ? Arrays.asList(values) : null);

		values = getValues(currentPath + "/warning");
		messageList.setWarning(values != null ? Arrays.asList(values) : null);

		values = getValues(currentPath + "/error");
		messageList.setError(values != null ? Arrays.asList(values) : null);

		return true;
	}

	public IResponseData getResponseData() {
		return data;
	}

	/**
	 * @param supportedVersion
	 * @return
	 */
	private List<WebApiVersion> parseVersions(final String supportedVersion) {
		if (supportedVersion == null) {
			return null;
		}
		final String[] parsed = supportedVersion.split(",");
		List<WebApiVersion> versions = new ArrayList<WebApiVersion>(
				parsed.length);
		for (String version : parsed) {
			versions.add(WebApiVersion.byFullName(version));
		}
		return versions;
	}

	public boolean preVisit(ServersList serversList) {
		String currentPath = serversList.getPrefix();

		final NodeList nodes = ((XmlRepresentation) representation)
				.getNodes(currentPath + "/serverInfo");
		final int size = nodes.size();
		if (size == 0) {
			return false;
		}

		// build servers info list
		List<ServerInfo> serversInfo = new ArrayList<ServerInfo>(size);
		for (int index = 0; index < size; index++) {
			serversInfo.add(new ServerInfo(currentPath + "/serverInfo", index));
		}

		serversList.setServerInfo(serversInfo);
		return true;
	}

	/**
	 * <pre>
	 *       <serverInfo>
	 *         <id>2</id>
	 *         <name>www-02</name>
	 *         <address>https://www-02.local:10082/ZendServer</address>
	 *         <status>restarting</status>
	 *         <messageList />
	 *       </serverInfo>
	 * 
	 * </pre>
	 */
	public boolean preVisit(ServerInfo serverInfo) {
		String currentPath = serverInfo.getPrefix();
		int occurrence = serverInfo.getOccurrence();

		String value = getValue(currentPath + "/id", occurrence);
		serverInfo.setId(parseNumberIfExists(value));

		value = getValue(currentPath + "/name", occurrence);
		serverInfo.setName(value);

		value = getValue(currentPath + "/address", occurrence);
		serverInfo.setAddress(value);

		value = getValue(currentPath + "/status", occurrence);
		serverInfo.setStatus(ServerStatus.byName(value));

		final MessageList messageList = new MessageList(currentPath
				+ "/messageList");
		serverInfo.setMessageList(messageList);

		return true;
	}

	@Override
	public boolean preVisit(ServerConfig serverConfig) {
		// representation.get

		final Disposition disposition = representation.getDisposition();
		if (disposition != null) {
			serverConfig.setFilename(disposition.getFilename());
		}
		final int size = (int) representation.getSize();
		serverConfig.setFileSize(size);

		try {
			byte[] content = new byte[size];
			InputStream reader = representation.getStream();
			reader.read(content);
			serverConfig.setFileContent(content);
		} catch (IOException e) {
			// TODO log exception
		}

		return super.preVisit(serverConfig);
	}

	public boolean preVisit(ApplicationsList applicationsList) {
		String currentPath = applicationsList.getPrefix();

		final NodeList nodes = ((XmlRepresentation) representation)
				.getNodes(currentPath + "/applicationInfo");
		final int size = nodes.size();
		if (size == 0) {
			return false;
		}

		// build applications info list
		List<ApplicationInfo> applicationsInfo = new ArrayList<ApplicationInfo>(
				size);
		for (int index = 0; index < size; index++) {
			applicationsInfo.add(new ApplicationInfo(currentPath
					+ "/applicationInfo", index));
		}

		applicationsList.setApplicationsInfo(applicationsInfo);
		return true;
	}

	public boolean preVisit(ApplicationInfo applicationInfo) {
		String currentPath = applicationInfo.getPrefix();
		int occurrence = applicationInfo.getOccurrence();

		String value = getValue(currentPath + "/id", occurrence);
		applicationInfo.setId(parseNumberIfExists(value));

		value = getValue(currentPath + "/baseUrl", occurrence);
		applicationInfo.setBaseUrl(value);

		value = getValue(currentPath + "/appName", occurrence);
		applicationInfo.setAppName(value);

		value = getValue(currentPath + "/userAppName", occurrence);
		applicationInfo.setUserAppName(value);

		value = getValue(currentPath + "/installedLocation", occurrence);
		applicationInfo.setInstalledLocation(value);

		value = getValue(currentPath + "/status", occurrence);
		applicationInfo.setStatus(ApplicationStatus.byName(value));

		final ApplicationServers applicationServers = new ApplicationServers(
				currentPath + "/servers");
		applicationInfo.setServers(applicationServers);

		final DeployedVersions deployedVersions = new DeployedVersions(
				currentPath + "/deployedVersions");
		applicationInfo.setDeployedVersions(deployedVersions);

		final MessageList messageList = new MessageList(currentPath
				+ "/messageList");
		applicationInfo.setMessageList(messageList);

		return true;
	}

	public boolean preVisit(DeployedVersions versions) {
		String currentPath = versions.getPrefix();

		final NodeList nodes = ((XmlRepresentation) representation)
				.getNodes(currentPath + "/deployedVersion");
		final int size = nodes.size();
		if (size == 0) {
			return false;
		}

		// build versions list
		List<DeployedVersion> versionsInfo = new ArrayList<DeployedVersion>(
				size);
		for (int index = 0; index < size; index++) {
			versionsInfo.add(new DeployedVersion(currentPath
					+ "/deployedVersion", index));
		}

		versions.setDeployedVersions(versionsInfo);
		return true;
	}

	public boolean preVisit(DeployedVersion version) {
		String currentPath = version.getPrefix();
		int occurrence = version.getOccurrence();
		String value = getValue(currentPath, occurrence);
		version.setVersion(value);
		return true;
	}

	public boolean preVisit(ApplicationServers serversList) {
		String currentPath = serversList.getPrefix();

		final NodeList nodes = ((XmlRepresentation) representation)
				.getNodes(currentPath + "/applicationServer");
		final int size = nodes.size();
		if (size == 0) {
			return false;
		}

		// build servers info list
		List<ApplicationServer> servers = new ArrayList<ApplicationServer>(size);
		for (int index = 0; index < size; index++) {
			servers.add(new ApplicationServer(currentPath
					+ "/applicationServer", index));
		}

		serversList.setAapplicationServers(servers);
		return true;
	}

	public boolean preVisit(ApplicationServer applicationServer) {
		String currentPath = applicationServer.getPrefix();
		int occurrence = applicationServer.getOccurrence();

		String value = getValue(currentPath + "/id", occurrence);
		applicationServer.setId(parseNumberIfExists(value));

		value = getValue(currentPath + "/deployedVersion", occurrence);
		applicationServer.setDeployedVersion(value);

		value = getValue(currentPath + "/status", occurrence);
		applicationServer.setStatus(ApplicationStatus.byName(value));
		return true;
	}

	/**
	 * @param value
	 * @return
	 */
	private int parseNumberIfExists(String value) {
		if (value == null || value.length() == 0) {
			return 0;
		}
		return Integer.parseInt(value);
	}

	private static IResponseData create(IRequest request) {
		final ResponseType type = request.getExpectedResponseDataType();
		return create(type);
	}

	private static IResponseData create(ResponseType type) {
		switch (type) {
		case SYSTEM_INFO:
			return new SystemInfo();
		case SERVERS_LIST:
			return new ServersList();
		case SERVER_INFO:
			return new ServerInfo();
		case SERVER_CONFIG:
			return new ServerConfig();
		case APPLICATIONS_LIST:
			return new ApplicationsList();
		case APPLICATION_INFO:
			return new ApplicationInfo();
		default:
			return null;
		}
	}

	private Representation getRepresentation(Representation representation,
			IResponseData responseData) {
		switch (responseData.getType()) {
		case SERVER_CONFIG:
			return representation;
		default:
			return new DomRepresentation(representation);
		}
	}
}