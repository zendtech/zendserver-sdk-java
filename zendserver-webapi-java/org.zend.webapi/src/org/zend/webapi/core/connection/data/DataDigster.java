/*******************************************************************************
 * Copyright (c) Jan 30, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.webapi.core.connection.data;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.restlet.data.Disposition;
import org.restlet.data.MediaType;
import org.restlet.engine.util.DateUtils;
import org.restlet.ext.xml.DomRepresentation;
import org.restlet.ext.xml.NodeList;
import org.restlet.ext.xml.XmlRepresentation;
import org.restlet.representation.Representation;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.zend.webapi.core.connection.data.CodeTracingStatus.State;
import org.zend.webapi.core.connection.data.CodeTracingStatus.Status;
import org.zend.webapi.core.connection.data.IResponseData.ResponseType;
import org.zend.webapi.core.connection.data.values.ApplicationStatus;
import org.zend.webapi.core.connection.data.values.IssueSeverity;
import org.zend.webapi.core.connection.data.values.IssueStatus;
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
	 * Validate XML or amf file representation.
	 * 
	 * @return validation result
	 */
	public boolean validateResponse() {
		if (representation != null) {
			MediaType mediaType = representation.getMediaType();
			if (mediaType != null) {
				String mtName = mediaType.getName();
				if (mtName != null
						&& (mtName.startsWith("application/vnd.zend") || mtName //$NON-NLS-1$
								.startsWith("application/x-amf"))) { //$NON-NLS-1$
					return true;
				}
			} else if (data instanceof CodeTraceFile) {
				return true;
			}
		}
		return false;
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
	 * Helper method which returns the number of nodes with specified name which
	 * occur before the current node (occurrence) in the whole document.
	 * 
	 * @return node number
	 */
	private int getPreviousNodesLength(String path, String nodeName,
			int occurrence) {
		if (occurrence == 0) {
			return 0;
		}
		final NodeList nodes = ((XmlRepresentation) representation)
				.getNodes(path);
		if (nodes.size() == 0) {
			return 0;
		}
		int result = 0;
		for (int i = 0; i < occurrence; i++) {
			Element node = (Element) nodes.get(i);
			result += node.getElementsByTagName(nodeName).getLength();
		}
		return result;
	}

	/**
	 * Helper method which returns number of nodes with specified name in a
	 * current node (occurrence).
	 * 
	 * @return node number
	 */
	private int getNodesLength(String path, String nodeName, int occurrence) {
		final NodeList nodes = ((XmlRepresentation) representation)
				.getNodes(path);
		if (nodes.size() == 0) {
			return 0;
		}
		Element node = (Element) nodes.get(occurrence);
		return node.getElementsByTagName(nodeName).getLength();
	}

	public boolean preVisit(SystemInfo systemInfo) {
		String currentPath = systemInfo.getPrefix();

		final String statusName = getValue(currentPath + "/status"); //$NON-NLS-1$
		systemInfo.setStatus(SystemStatus.byName(statusName));

		final String editionName = getValue(currentPath + "/edition"); //$NON-NLS-1$
		systemInfo.setEdition(SystemEdition.byName(editionName));

		final String serverVersion = getValue(currentPath + "/zendServerVersion"); //$NON-NLS-1$
		systemInfo.setVersion(serverVersion);

		final String supportedVersion = getValue(currentPath + "/supportedApiVersions"); //$NON-NLS-1$
		List<WebApiVersion> versions = parseVersions(supportedVersion);
		systemInfo.setSupportedApiVersions(versions);

		final String phpVersion = getValue(currentPath + "/phpVersion"); //$NON-NLS-1$
		systemInfo.setPhpVersion(phpVersion);

		final String os = getValue(currentPath + "/operatingSystem"); //$NON-NLS-1$
		systemInfo.setOperatingSystem(os);

		systemInfo.setLicenseInfo(new LicenseInfo(currentPath + "/serverLicenseInfo")); //$NON-NLS-1$
		systemInfo.setManagerLicenseInfo(new LicenseInfo(currentPath + "/managerLicenseInfo")); //$NON-NLS-1$
		systemInfo.setMessageList(new MessageList(currentPath + "/messageList", systemInfo.getOccurrence())); //$NON-NLS-1$

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

		value = getValue(currentPath + "/status"); //$NON-NLS-1$
		licenseInfo.setStatus(LicenseInfoStatus.byName(value));

		value = getValue(currentPath + "/orderNumber"); //$NON-NLS-1$
		licenseInfo.setOrderNumber(value);

		value = getValue(currentPath + "/validUntil"); //$NON-NLS-1$
		if (value != null) {
			final Date parse = DateUtils
					.parse(value, DateUtils.FORMAT_RFC_1123);
			licenseInfo.setValidUntil(parse);
		}
		value = getValue(currentPath + "/serverLimit"); //$NON-NLS-1$
		licenseInfo.setLimit(Integer.parseInt(value == null ? "0" : value)); //$NON-NLS-1$

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
		// build message info list
		List<String> errors = getMessageValues(messageList, "error"); //$NON-NLS-1$
		messageList.setError(errors.size() > 0 ? errors : null);

		List<String> warnings = getMessageValues(messageList, "warning"); //$NON-NLS-1$
		messageList.setWarning(warnings.size() > 0 ? warnings : null);

		List<String> infos = getMessageValues(messageList, "info"); //$NON-NLS-1$
		messageList.setInfo(infos.size() > 0 ? infos : null);

		return true;

	}

	private List<String> getMessageValues(MessageList messageList,
			String nodeName) {
		String currentPath = messageList.getPrefix();
		final int size = getNodesLength(currentPath, nodeName,
				messageList.getOccurrence());
		final int previousSize = getPreviousNodesLength(currentPath, nodeName,
				messageList.getOccurrence());
		List<String> messages = new ArrayList<String>(size);
		for (int index = previousSize; index < previousSize + size; index++) {
			messages.add(getValue(currentPath + "/" + nodeName, index)); //$NON-NLS-1$
		}
		return messages;
	}
	
	public boolean preVisit(ListValues listValues) {
		String currentPath = listValues.getPrefix();
		final NodeList nodes = ((XmlRepresentation) representation)
				.getNodes(currentPath + "/listValue"); //$NON-NLS-1$
		final int size = nodes.size();
		if (size == 0) {
			return false;
		}
		// build values list
		List<ListValue> listValue = new ArrayList<ListValue>(
				size);
		for (int index = 0; index < size; index++) {
			listValue.add(new ListValue(currentPath
					+ "/listValue", index)); //$NON-NLS-1$
		}
		listValues.setValues(listValue);
		return true;
	}
	
	public boolean preVisit(ListValue listValue) {
		String currentPath = listValue.getPrefix();
		int occurrence = listValue.getOccurrence();

		String value = getValue(currentPath + "/name", occurrence); //$NON-NLS-1$
		listValue.setName(value);
		
		value = getValue(currentPath + "/value", occurrence); //$NON-NLS-1$
		listValue.setValue(value);
		
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
		final String[] parsed = supportedVersion.split(","); //$NON-NLS-1$
		List<WebApiVersion> versions = new ArrayList<WebApiVersion>(
				parsed.length);
		for (String version : parsed) {
			versions.add(WebApiVersion.byFullName(version.trim()));
		}
		return versions;
	}

	public boolean preVisit(ServersList serversList) {
		String currentPath = serversList.getPrefix();

		final NodeList nodes = ((XmlRepresentation) representation)
				.getNodes(currentPath + "/serverInfo"); //$NON-NLS-1$
		final int size = nodes.size();
		if (size == 0) {
			return false;
		}

		// build servers info list
		List<ServerInfo> serversInfo = new ArrayList<ServerInfo>(size);
		for (int index = 0; index < size; index++) {
			serversInfo.add(new ServerInfo(currentPath + "/serverInfo", index)); //$NON-NLS-1$
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

		String value = getValue(currentPath + "/id", occurrence); //$NON-NLS-1$
		serverInfo.setId(parseNumberIfExists(value));

		value = getValue(currentPath + "/name", occurrence); //$NON-NLS-1$
		serverInfo.setName(value);

		value = getValue(currentPath + "/address", occurrence); //$NON-NLS-1$
		serverInfo.setAddress(value);

		value = getValue(currentPath + "/status", occurrence); //$NON-NLS-1$
		serverInfo.setStatus(ServerStatus.byName(value));

		final MessageList messageList = new MessageList(currentPath + "/messageList", occurrence); //$NON-NLS-1$
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
	
	@Override
	public boolean preVisit(ExtensionsList extensionsList) {
		String currentPath = extensionsList.getPrefix();
		final NodeList nodes = ((XmlRepresentation) representation)
				.getNodes(currentPath + "/extension"); //$NON-NLS-1$
		final int size = nodes.size();
		if (size == 0) {
			return false;
		}
		// build extensions info list
		List<ExtensionInfo> extensionsInfo = new ArrayList<ExtensionInfo>(
				size);
		for (int index = 0; index < size; index++) {
			extensionsInfo.add(new ExtensionInfo(currentPath
					+ "/extension", index)); //$NON-NLS-1$
		}
		extensionsList.setExtensionsInfo(extensionsInfo);
		return true;
	}
	
	public boolean preVisit(ExtensionInfo extensionInfo) {
		String currentPath = extensionInfo.getPrefix();
		int occurrence = extensionInfo.getOccurrence();

		String value = getValue(currentPath + "/name", occurrence); //$NON-NLS-1$
		extensionInfo.setName(value);

		value = getValue(currentPath + "/version", occurrence); //$NON-NLS-1$
		extensionInfo.setVersion(value);

		value = getValue(currentPath + "/type", occurrence); //$NON-NLS-1$
		extensionInfo.setExtensionType(value);

		value = getValue(currentPath + "/status", occurrence); //$NON-NLS-1$
		extensionInfo.setStatus(value);

		value = getValue(currentPath + "/shortDescription", occurrence); //$NON-NLS-1$
		extensionInfo.setShortDescription(value);

		value = getValue(currentPath + "/longDescription", occurrence); //$NON-NLS-1$
		extensionInfo.setLongDescription(value);

		value = getValue(currentPath + "/loaded", occurrence); //$NON-NLS-1$
		extensionInfo.setLoaded(Boolean.valueOf(value));
		
		value = getValue(currentPath + "/installed", occurrence); //$NON-NLS-1$
		extensionInfo.setInstalled(Boolean.valueOf(value));
		
		value = getValue(currentPath + "/builtIn", occurrence); //$NON-NLS-1$
		extensionInfo.setBuiltIn(Boolean.valueOf(value));
		
		value = getValue(currentPath + "/dummy", occurrence); //$NON-NLS-1$
		extensionInfo.setDummy(Boolean.valueOf(value));
		
		value = getValue(currentPath + "/restartRequired", occurrence); //$NON-NLS-1$
		extensionInfo.setRestartRequired(Boolean.valueOf(value));
		
		final MessageList messageList = new MessageList(currentPath
				+ "/messageList", occurrence); //$NON-NLS-1$
		extensionInfo.setMessageList(messageList);

		return true;
	}
	
	public boolean preVisit(DirectivesList directivesList) {
		String currentPath = directivesList.getPrefix();
		final NodeList nodes = ((XmlRepresentation) representation)
				.getNodes(currentPath + "/directive"); //$NON-NLS-1$
		final int size = nodes.size();
		if (size == 0) {
			return false;
		}
		// build directives info list
		List<DirectiveInfo> directivesInfo = new ArrayList<DirectiveInfo>(
				size);
		for (int index = 0; index < size; index++) {
			directivesInfo.add(new DirectiveInfo(currentPath
					+ "/directive", index)); //$NON-NLS-1$
		}
		directivesList.setDirectivesInfo(directivesInfo);
		return true;
	}
	
	public boolean preVisit(DirectiveInfo directiveInfo) {
		String currentPath = directiveInfo.getPrefix();
		int occurrence = directiveInfo.getOccurrence();

		String value = getValue(currentPath + "/name", occurrence); //$NON-NLS-1$
		directiveInfo.setName(value);
		
		value = getValue(currentPath + "/section", occurrence); //$NON-NLS-1$
		directiveInfo.setSection(value);
		
		value = getValue(currentPath + "/type", occurrence); //$NON-NLS-1$
		directiveInfo.setDirectiveType(value);
		
		value = getValue(currentPath + "/fileValue", occurrence); //$NON-NLS-1$
		directiveInfo.setFileValue(value);
		
		value = getValue(currentPath + "/defaultValue", occurrence); //$NON-NLS-1$
		directiveInfo.setDefaultValue(value);
		
		value = getValue(currentPath + "/previousValue", occurrence); //$NON-NLS-1$
		directiveInfo.setPreviousValue(value);
		
		value = getValue(currentPath + "/units", occurrence); //$NON-NLS-1$
		directiveInfo.setUnits(value);
		
		final ListValues listValues = new ListValues(currentPath
				+ "/listValues", occurrence); //$NON-NLS-1$
		directiveInfo.setListValues(listValues);

		return true;
	}

	public boolean preVisit(ApplicationsList applicationsList) {
		String currentPath = applicationsList.getPrefix();

		final NodeList nodes = ((XmlRepresentation) representation)
				.getNodes(currentPath + "/applicationInfo"); //$NON-NLS-1$
		final int size = nodes.size();
		if (size == 0) {
			return false;
		}

		// build applications info list
		List<ApplicationInfo> applicationsInfo = new ArrayList<ApplicationInfo>(
				size);
		for (int index = 0; index < size; index++) {
			applicationsInfo.add(new ApplicationInfo(currentPath
					+ "/applicationInfo", index)); //$NON-NLS-1$
		}

		applicationsList.setApplicationsInfo(applicationsInfo);
		return true;
	}

	public boolean preVisit(ApplicationInfo applicationInfo) {
		String currentPath = applicationInfo.getPrefix();
		int occurrence = applicationInfo.getOccurrence();

		String value = getValue(currentPath + "/id", occurrence); //$NON-NLS-1$
		applicationInfo.setId(parseNumberIfExists(value));

		value = getValue(currentPath + "/baseUrl", occurrence); //$NON-NLS-1$
		applicationInfo.setBaseUrl(value);

		value = getValue(currentPath + "/appName", occurrence); //$NON-NLS-1$
		applicationInfo.setAppName(value);

		value = getValue(currentPath + "/userAppName", occurrence); //$NON-NLS-1$
		applicationInfo.setUserAppName(value);

		value = getValue(currentPath + "/installedLocation", occurrence); //$NON-NLS-1$
		applicationInfo.setInstalledLocation(value);

		value = getValue(currentPath + "/status", occurrence); //$NON-NLS-1$
		applicationInfo.setStatus(ApplicationStatus.byName(value));

		final ApplicationServers applicationServers = new ApplicationServers(
				currentPath + "/servers", occurrence); //$NON-NLS-1$
		applicationInfo.setServers(applicationServers);

		final DeployedVersions deployedVersions = new DeployedVersions(
				currentPath + "/deployedVersions", occurrence); //$NON-NLS-1$
		applicationInfo.setDeployedVersions(deployedVersions);

		final MessageList messageList = new MessageList(currentPath
				+ "/messageList", occurrence); //$NON-NLS-1$
		applicationInfo.setMessageList(messageList);

		return true;
	}

	public boolean preVisit(DeployedVersions versions) {
		String currentPath = versions.getPrefix();
		final int size = getNodesLength(currentPath, "deployedVersion", //$NON-NLS-1$
				versions.getOccurrence());

		if (size == 0) {
			return false;
		}

		final int overallSize = getPreviousNodesLength(currentPath,
				"deployedVersion", versions.getOccurrence()); //$NON-NLS-1$

		// build versions list
		List<DeployedVersion> versionsInfo = new ArrayList<DeployedVersion>(
				size);
		for (int index = overallSize; index < overallSize + size; index++) {
			versionsInfo.add(new DeployedVersion(currentPath
					+ "/deployedVersion", index)); //$NON-NLS-1$
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
		final int size = getNodesLength(currentPath, "applicationServer", //$NON-NLS-1$
				serversList.getOccurrence());

		if (size == 0) {
			return false;
		}

		final int overallSize = getPreviousNodesLength(currentPath,
				"applicationServer", serversList.getOccurrence()); //$NON-NLS-1$

		// build servers info list
		List<ApplicationServer> servers = new ArrayList<ApplicationServer>(size);
		for (int index = overallSize; index < overallSize + size; index++) {
			servers.add(new ApplicationServer(currentPath
					+ "/applicationServer", index)); //$NON-NLS-1$
		}

		serversList.setAapplicationServers(servers);
		return true;
	}

	public boolean preVisit(ApplicationServer applicationServer) {
		String currentPath = applicationServer.getPrefix();
		int occurrence = applicationServer.getOccurrence();

		String value = getValue(currentPath + "/id", occurrence); //$NON-NLS-1$
		applicationServer.setId(parseNumberIfExists(value));

		value = getValue(currentPath + "/deployedVersion", occurrence); //$NON-NLS-1$
		applicationServer.setDeployedVersion(value);

		value = getValue(currentPath + "/status", occurrence); //$NON-NLS-1$
		applicationServer.setStatus(ApplicationStatus.byName(value));
		return true;
	}

	@Override
	public boolean preVisit(Parameter parameter) {
		String currentPath = parameter.getPrefix();
		int occurrence = parameter.getOccurrence();
		String value = getValue(currentPath + "/name", occurrence); //$NON-NLS-1$
		parameter.setName(value);
		value = getValue(currentPath + "/value", occurrence); //$NON-NLS-1$
		parameter.setValue(value);
		return true;
	}

	@Override
	public boolean preVisit(SuperGlobals superGlobals) {
		String currentPath = superGlobals.getPrefix();
		int occurrence = superGlobals.getOccurrence();
		ParameterList get = new ParameterList(currentPath + "/get", occurrence); //$NON-NLS-1$
		superGlobals.setGet(get);
		ParameterList post = new ParameterList(currentPath + "/post", //$NON-NLS-1$
				occurrence);
		superGlobals.setPost(post);
		ParameterList cookie = new ParameterList(currentPath + "/cookie", //$NON-NLS-1$
				occurrence);
		superGlobals.setCookie(cookie);
		ParameterList session = new ParameterList(currentPath + "/session", //$NON-NLS-1$
				occurrence);
		superGlobals.setSession(session);
		ParameterList server = new ParameterList(currentPath + "/server", //$NON-NLS-1$
				occurrence);
		superGlobals.setServer(server);
		return true;
	}

	@Override
	public boolean preVisit(Step step) {
		String currentPath = step.getPrefix();
		int occurrence = step.getOccurrence();
		String value = getValue(currentPath + "/number", occurrence); //$NON-NLS-1$
		step.setNumber(parseNumberIfExists(value));
		value = getValue(currentPath + "/object", occurrence); //$NON-NLS-1$
		step.setObjectId(value);
		value = getValue(currentPath + "/class", occurrence); //$NON-NLS-1$
		step.setClassId(value);
		value = getValue(currentPath + "/function", occurrence); //$NON-NLS-1$
		step.setFunction(value);
		value = getValue(currentPath + "/file", occurrence); //$NON-NLS-1$
		step.setFile(value);
		value = getValue(currentPath + "/line", occurrence); //$NON-NLS-1$
		step.setLine(parseNumberIfExists(value));
		return true;
	}

	@Override
	public boolean preVisit(CodeTracingStatus codeTracingStatus) {
		String currentPath = codeTracingStatus.getPrefix();
		int occurrence = codeTracingStatus.getOccurrence();
		String value = getValue(currentPath + "/componentStatus", occurrence); //$NON-NLS-1$
		codeTracingStatus.setComponentStatus(Status.byValue(value));
		value = getValue(currentPath + "/traceEnabled", occurrence); //$NON-NLS-1$
		codeTracingStatus.setTraceEnabled(State
				.byValue(parseNumberIfExists(value)));
		value = getValue(currentPath + "/developerMode", occurrence); //$NON-NLS-1$
		codeTracingStatus.setDeveloperMode(State
				.byValue(parseNumberIfExists(value)));
		value = getValue(currentPath + "/awaitsRestart", occurrence); //$NON-NLS-1$
		codeTracingStatus.setAwaitsRestart(State
				.byValue(parseNumberIfExists(value)));
		return true;
	}

	@Override
	public boolean preVisit(CodeTrace codeTrace) {
		String currentPath = codeTrace.getPrefix();
		int occurrence = codeTrace.getOccurrence();
		String value = getValue(currentPath + "/id", occurrence); //$NON-NLS-1$
		codeTrace.setId(value);
		value = getValue(currentPath + "/date", occurrence); //$NON-NLS-1$
		codeTrace.setDate(parseLongIfExists(value));
		value = getValue(currentPath + "/url", occurrence); //$NON-NLS-1$
		codeTrace.setUrl(value);
		value = getValue(currentPath + "/createdBy", occurrence); //$NON-NLS-1$
		codeTrace.setCreatedBy(value);
		value = getValue(currentPath + "/filesize", occurrence); //$NON-NLS-1$
		codeTrace.setFilesize(parseNumberIfExists(value));
		value = getValue(currentPath + "/applicationId", occurrence); //$NON-NLS-1$
		codeTrace.setApplicationId(parseNumberIfExists(value));
		return true;
	}

	@Override
	public boolean preVisit(CodeTracingList codeTracingList) {
		String currentPath = codeTracingList.getPrefix();
		final int size = getNodesLength(currentPath, "codeTrace", //$NON-NLS-1$
				codeTracingList.getOccurrence());

		if (size == 0) {
			return false;
		}

		final int overallSize = getPreviousNodesLength(currentPath,
				"codeTrace", codeTracingList.getOccurrence()); //$NON-NLS-1$

		List<CodeTrace> traces = new ArrayList<CodeTrace>(size);
		for (int index = overallSize; index < overallSize + size; index++) {
			traces.add(new CodeTrace(currentPath + "/codeTrace", index)); //$NON-NLS-1$
		}

		codeTracingList.setTraces(traces);
		return true;
	}

	@Override
	public boolean preVisit(EventsGroup eventsGroup) {
		String currentPath = eventsGroup.getPrefix();
		int occurrence = eventsGroup.getOccurrence();
		String value = getValue(currentPath + "/eventsGroupId", occurrence); //$NON-NLS-1$
		eventsGroup.setEventsGroupId(parseNumberIfExists(value));
		value = getValue(currentPath + "/eventsCount", occurrence); //$NON-NLS-1$
		eventsGroup.setEventsCount(parseNumberIfExists(value));
		value = getValue(currentPath + "/startTime", occurrence); //$NON-NLS-1$
		eventsGroup.setStartTime(value);
		value = getValue(currentPath + "/serverId", occurrence); //$NON-NLS-1$
		eventsGroup.setServerId(parseNumberIfExists(value));
		value = getValue(currentPath + "/class", occurrence); //$NON-NLS-1$
		eventsGroup.setClassId(value);
		value = getValue(currentPath + "/userData", occurrence); //$NON-NLS-1$
		eventsGroup.setUserData(value);
		value = getValue(currentPath + "/javaBacktrace", occurrence); //$NON-NLS-1$
		eventsGroup.setJavaBacktrace(value);
		value = getValue(currentPath + "/execTime", occurrence); //$NON-NLS-1$
		eventsGroup.setExecTime(parseNumberIfExists(value));
		value = getValue(currentPath + "/avgExecTime", occurrence); //$NON-NLS-1$
		eventsGroup.setAvgExecTime(parseNumberIfExists(value));
		value = getValue(currentPath + "/memUsage", occurrence); //$NON-NLS-1$
		eventsGroup.setMemUsage(parseNumberIfExists(value));
		value = getValue(currentPath + "/avgMemUsage", occurrence); //$NON-NLS-1$
		eventsGroup.setAvgMemUsage(parseNumberIfExists(value));
		value = getValue(currentPath + "/avgOutputSize", occurrence); //$NON-NLS-1$
		eventsGroup.setAvgOutputSize(parseNumberIfExists(value));
		value = getValue(currentPath + "/load", occurrence); //$NON-NLS-1$
		eventsGroup.setLoad(value);
		return true;
	}

	@Override
	public boolean preVisit(Event event) {
		String currentPath = event.getPrefix();
		int occurrence = event.getOccurrence();
		String value = getValue(currentPath + "/eventsGroupId", occurrence); //$NON-NLS-1$
		event.setEventsGroupId(value);
		value = getValue(currentPath + "/type", occurrence); //$NON-NLS-1$
		event.setEventType(value);
		value = getValue(currentPath + "/severity", occurrence); //$NON-NLS-1$
		event.setSeverity(value);
		value = getValue(currentPath + "/description", occurrence); //$NON-NLS-1$
		event.setDescription(value);
		value = getValue(currentPath + "/codeTracing", occurrence); //$NON-NLS-1$
		event.setCodeTracing(value);
		SuperGlobals superGlobals = new SuperGlobals(currentPath
				+ "/superGlobals", occurrence); //$NON-NLS-1$
		event.setSuperGlobals(superGlobals);
		Backtrace backtrace = new Backtrace(currentPath + "/backtrace", //$NON-NLS-1$
				occurrence);
		event.setBacktrace(backtrace);
		return true;
	}

	@Override
	public boolean preVisit(Backtrace backtrace) {
		String currentPath = backtrace.getPrefix();
		final int size = getNodesLength(currentPath, "step", //$NON-NLS-1$
				backtrace.getOccurrence());

		if (size == 0) {
			return false;
		}

		final int overallSize = getPreviousNodesLength(currentPath,
				"applicationServer", backtrace.getOccurrence()); //$NON-NLS-1$

		List<Step> steps = new ArrayList<Step>(size);
		for (int index = overallSize; index < overallSize + size; index++) {
			steps.add(new Step(currentPath + "/step", index)); //$NON-NLS-1$
		}

		backtrace.setSteps(steps);
		return true;
	}

	@Override
	public boolean preVisit(ParameterList parameterList) {
		String currentPath = parameterList.getPrefix();
		final int size = getNodesLength(currentPath, "parameter", //$NON-NLS-1$
				parameterList.getOccurrence());

		if (size == 0) {
			return false;
		}

		final int overallSize = getPreviousNodesLength(currentPath,
				"parameter", parameterList.getOccurrence()); //$NON-NLS-1$

		List<Parameter> parameters = new ArrayList<Parameter>(size);
		for (int index = overallSize; index < overallSize + size; index++) {
			parameters.add(new Parameter(currentPath + "/parameter", index)); //$NON-NLS-1$
		}

		parameterList.setParameters(parameters);
		return true;
	}

	@Override
	public boolean preVisit(EventsGroupDetails eventsGroupDetails) {
		String currentPath = eventsGroupDetails.getPrefix();
		int occurrence = eventsGroupDetails.getOccurrence();
		String value = getValue(currentPath + "/issueId", occurrence); //$NON-NLS-1$
		eventsGroupDetails.setIssueId(parseNumberIfExists(value));
		value = getValue(currentPath + "/codeTracing", occurrence); //$NON-NLS-1$
		eventsGroupDetails.setCodeTracing(value);
		EventsGroup eventsGroup = new EventsGroup(currentPath + "/eventsGroup", //$NON-NLS-1$
				occurrence);
		eventsGroupDetails.setEventsGroup(eventsGroup);
		Event event = new Event(currentPath + "/event", occurrence); //$NON-NLS-1$
		eventsGroupDetails.setEvent(event);
		return true;
	}

	@Override
	public boolean preVisit(RouteDetail routeDetail) {
		String currentPath = routeDetail.getPrefix();
		int occurrence = routeDetail.getOccurrence();
		String value = getValue(currentPath + "/key", occurrence); //$NON-NLS-1$
		routeDetail.setKey(value);
		value = getValue(currentPath + "/value", occurrence); //$NON-NLS-1$
		routeDetail.setValue(value);
		return true;
	}

	@Override
	public boolean preVisit(EventsGroups eventsGroups) {
		String currentPath = eventsGroups.getPrefix();
		final int size = getNodesLength(currentPath, "eventsGroup", //$NON-NLS-1$
				eventsGroups.getOccurrence());

		if (size == 0) {
			return false;
		}

		final int overallSize = getPreviousNodesLength(currentPath,
				"eventsGroup", eventsGroups.getOccurrence()); //$NON-NLS-1$

		List<EventsGroup> groups = new ArrayList<EventsGroup>(size);
		for (int index = overallSize; index < overallSize + size; index++) {
			groups.add(new EventsGroup(currentPath + "/eventsGroup", index)); //$NON-NLS-1$
		}

		eventsGroups.setGroups(groups);
		return true;
	}

	@Override
	public boolean preVisit(RouteDetails routeDetails) {
		String currentPath = routeDetails.getPrefix();
		final int size = getNodesLength(currentPath, "routeDetail", //$NON-NLS-1$
				routeDetails.getOccurrence());

		if (size == 0) {
			return false;
		}

		final int overallSize = getPreviousNodesLength(currentPath,
				"routeDetail", routeDetails.getOccurrence()); //$NON-NLS-1$

		List<RouteDetail> details = new ArrayList<RouteDetail>(size);
		for (int index = overallSize; index < overallSize + size; index++) {
			details.add(new RouteDetail(currentPath + "/routeDetail", index)); //$NON-NLS-1$
		}

		routeDetails.setDetails(details);
		return true;
	}

	@Override
	public boolean preVisit(Issue issue) {
		String currentPath = issue.getPrefix();
		int occurrence = issue.getOccurrence();
		String value = getValue(currentPath + "/id", occurrence); //$NON-NLS-1$
		issue.setId(parseNumberIfExists(value));
		value = getValue(currentPath + "/rule", occurrence); //$NON-NLS-1$
		issue.setRule(value);
		value = getValue(currentPath + "/lastOccurance", occurrence); //$NON-NLS-1$
		issue.setLastOccurance(value);
		value = getValue(currentPath + "/severity", occurrence); //$NON-NLS-1$
		issue.setSeverity(IssueSeverity.byName(value));
		value = getValue(currentPath + "/status", occurrence); //$NON-NLS-1$
		issue.setStatus(IssueStatus.byName(value));
		GeneralDetails generalDetails = new GeneralDetails(currentPath
				+ "/generalDetails", occurrence); //$NON-NLS-1$
		issue.setGeneralDetails(generalDetails);
		RouteDetails routeDetails = new RouteDetails(currentPath
				+ "/routeDetails", occurrence); //$NON-NLS-1$
		issue.setRouteDetails(routeDetails);
		return true;
	}

	@Override
	public boolean preVisit(IssueList issueList) {
		String currentPath = issueList.getPrefix();
		final int size = getNodesLength(currentPath, "issue", //$NON-NLS-1$
				issueList.getOccurrence());

		if (size == 0) {
			return false;
		}

		final int overallSize = getPreviousNodesLength(currentPath, "issue", //$NON-NLS-1$
				issueList.getOccurrence());

		List<Issue> issues = new ArrayList<Issue>(size);
		for (int index = overallSize; index < overallSize + size; index++) {
			issues.add(new Issue(currentPath + "/issue", index)); //$NON-NLS-1$
		}

		issueList.setIssues(issues);
		return true;
	}

	@Override
	public boolean preVisit(IssueDetails issueDetails) {
		String currentPath = issueDetails.getPrefix();
		int occurrence = issueDetails.getOccurrence();
		Issue issue = new Issue(currentPath + "/issue", occurrence); //$NON-NLS-1$
		issueDetails.setIssue(issue);
		EventsGroups groups = new EventsGroups(currentPath + "/eventsGroups", //$NON-NLS-1$
				occurrence);
		issueDetails.setEventsGroups(groups);
		return true;
	}

	@Override
	public boolean preVisit(Events events) {
		String currentPath = events.getPrefix();
		final int size = getNodesLength(currentPath, "event", //$NON-NLS-1$
				events.getOccurrence());

		if (size == 0) {
			return false;
		}

		final int overallSize = getPreviousNodesLength(currentPath, "event", //$NON-NLS-1$
				events.getOccurrence());

		List<Event> eventsList = new ArrayList<Event>(size);
		for (int index = overallSize; index < overallSize + size; index++) {
			eventsList.add(new Event(currentPath + "/event", index)); //$NON-NLS-1$
		}

		events.setEvents(eventsList);
		return true;
	}

	@Override
	public boolean preVisit(RequestSummary requestSummary) {
		String currentPath = requestSummary.getPrefix();
		int occurrence = requestSummary.getOccurrence();
		String value = getValue(currentPath + "/eventsCount", occurrence); //$NON-NLS-1$
		requestSummary.setEventsCount(parseNumberIfExists(value));
		value = getValue(currentPath + "/codeTracing", occurrence); //$NON-NLS-1$
		requestSummary.setCodeTracing(value);
		Events events = new Events(currentPath + "/events", occurrence); //$NON-NLS-1$
		requestSummary.setEvents(events);
		return true;
	}

	@Override
	public boolean preVisit(CodeTraceFile codeTraceFile) {
		Disposition disposition = representation.getDisposition();
		if (disposition != null) {
			codeTraceFile.setFilename(disposition.getFilename());
		} else {
			codeTraceFile.setFilename("default.amf"); //$NON-NLS-1$
		}
		int size = (int) representation.getSize();
		codeTraceFile.setFileSize(size);

		try {
			InputStream reader = representation.getStream();
			byte[] buffer = new byte[8192];
			int count = 0;
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			while ((count = reader.read(buffer)) > 0) {
				out.write(buffer, 0, count);
			}
			codeTraceFile.setFileSize(out.size());
			codeTraceFile.setFileContent(out.toByteArray());
		} catch (IOException e) {
			return false;
		}
		return true;
	}

	@Override
	public boolean preVisit(IssueFile issueFile) {
		Disposition disposition = representation.getDisposition();
		if (disposition != null) {
			issueFile.setFilename(disposition.getFilename());
		}
		int size = (int) representation.getSize();
		issueFile.setFileSize(size);

		try {
			byte[] content = new byte[size];
			InputStream reader = representation.getStream();
			int offset = 0;
			while (offset < size) {
				offset += reader.read(content, offset, size - offset);
			}
			issueFile.setFileContent(content);
		} catch (IOException e) {
			return false;
		}
		return true;
	}

	@Override
	public boolean preVisit(DebugRequest debugRequest) {
		String currentPath = debugRequest.getPrefix();
		int occurrence = debugRequest.getOccurrence();
		String value = getValue(currentPath + "/success", occurrence); //$NON-NLS-1$
		debugRequest.setSuccess(value);
		value = getValue(currentPath + "/message", occurrence); //$NON-NLS-1$
		debugRequest.setMessage(value);
		return true;
	}

	@Override
	public boolean preVisit(ProfileRequest profileRequest) {
		String currentPath = profileRequest.getPrefix();
		int occurrence = profileRequest.getOccurrence();
		String value = getValue(currentPath + "/success", occurrence); //$NON-NLS-1$
		profileRequest.setSuccess(value);
		value = getValue(currentPath + "/message", occurrence); //$NON-NLS-1$
		profileRequest.setMessage(value);
		return true;
	}

	@Override
	public boolean preVisit(GeneralDetails generalDetails) {
		String currentPath = generalDetails.getPrefix();
		int occurrence = generalDetails.getOccurrence();
		String value = getValue(currentPath + "/url", occurrence); //$NON-NLS-1$
		generalDetails.setUrl(value);
		value = getValue(currentPath + "/sourceFile", occurrence); //$NON-NLS-1$
		generalDetails.setSourceFile(value);
		value = getValue(currentPath + "/sourceLine", occurrence); //$NON-NLS-1$
		generalDetails.setSourceLine(parseLongIfExists(value));
		value = getValue(currentPath + "/function", occurrence); //$NON-NLS-1$
		generalDetails.setFunction(value);
		value = getValue(currentPath + "/aggregationHint", occurrence); //$NON-NLS-1$
		generalDetails.setAggregationHint(value);
		value = getValue(currentPath + "/errorString", occurrence); //$NON-NLS-1$
		generalDetails.setErrorString(value);
		value = getValue(currentPath + "/errorType", occurrence); //$NON-NLS-1$
		generalDetails.setErrorType(value);
		return true;
	}

	@Override
	public boolean preVisit(DebugMode debugMode) {
		String currentPath = debugMode.getPrefix();
		int occurrence = debugMode.getOccurrence();
		String value = getValue(currentPath + "/value", occurrence); //$NON-NLS-1$
		debugMode.setResult(Integer.valueOf(value));
		return true;
	}

	@Override
	public boolean preVisit(LibraryVersion libraryVersion) {
		String currentPath = libraryVersion.getPrefix();
		int occurrence = libraryVersion.getOccurrence();
		String value = getValue(currentPath + "/libraryVersionId", occurrence); //$NON-NLS-1$
		libraryVersion.setLibraryVersionId(Integer.valueOf(value));
		value = getValue(currentPath + "/version", occurrence); //$NON-NLS-1$
		libraryVersion.setVersion(value);
		value = getValue(currentPath + "/default", occurrence); //$NON-NLS-1$
		libraryVersion.setIsDefault(Boolean.valueOf(value));
		value = getValue(currentPath + "/installedLocation", occurrence); //$NON-NLS-1$
		libraryVersion.setInstalledLocation(value);
		value = getValue(currentPath + "/creationTime", occurrence); //$NON-NLS-1$
		libraryVersion.setCreationTime(value);
		value = getValue(currentPath + "/creationTimeTimestamp", occurrence); //$NON-NLS-1$
		libraryVersion.setCreationTimeTimestamp(value);
		value = getValue(currentPath + "/lastUsed", occurrence); //$NON-NLS-1$
		libraryVersion.setLastUsed(value);
		value = getValue(currentPath + "/lastUsedTimeTimestamp", occurrence); //$NON-NLS-1$
		libraryVersion.setLastUsedTimestamp(value);
		value = getValue(currentPath + "/status", occurrence); //$NON-NLS-1$
		libraryVersion.setStatus(value);
		LibraryServers servers = new LibraryServers(currentPath + "/servers", //$NON-NLS-1$
				occurrence);
		libraryVersion.setServers(servers);
		return true;
	}

	@Override
	public boolean preVisit(LibraryServer libraryServer) {
		String currentPath = libraryServer.getPrefix();
		int occurrence = libraryServer.getOccurrence();
		String value = getValue(currentPath + "/id", occurrence); //$NON-NLS-1$
		libraryServer.setId(Integer.valueOf(value));
		value = getValue(currentPath + "/status", occurrence); //$NON-NLS-1$
		libraryServer.setStatus(value);
		value = getValue(currentPath + "/lastMessage", occurrence); //$NON-NLS-1$
		libraryServer.setLastMessage(value);
		value = getValue(currentPath + "/lastUpdatedTimestamp", occurrence); //$NON-NLS-1$
		libraryServer.setLastUpdatedTimestamp(value);
		return true;
	}

	@Override
	public boolean preVisit(LibraryServers libraryServers) {
		String currentPath = libraryServers.getPrefix();
		final int size = getNodesLength(currentPath, "libraryServer", //$NON-NLS-1$
				libraryServers.getOccurrence());

		if (size == 0) {
			return false;
		}

		final int overallSize = getPreviousNodesLength(currentPath,
				"libraryServer", libraryServers.getOccurrence()); //$NON-NLS-1$

		List<LibraryServer> servers = new ArrayList<LibraryServer>(size);
		for (int index = overallSize; index < overallSize + size; index++) {
			servers.add(new LibraryServer(currentPath + "/libraryServer", index)); //$NON-NLS-1$
		}

		libraryServers.setServers(servers);
		return true;
	}

	@Override
	public boolean preVisit(LibraryList libraryList) {
		String currentPath = libraryList.getPrefix();
		final int size = getNodesLength(currentPath, "libraryInfo", //$NON-NLS-1$
				libraryList.getOccurrence());

		if (size == 0) {
			return false;
		}

		final int overallSize = getPreviousNodesLength(currentPath,
				"libraryInfo", libraryList.getOccurrence()); //$NON-NLS-1$

		List<LibraryInfo> infos = new ArrayList<LibraryInfo>(size);
		for (int index = overallSize; index < overallSize + size; index++) {
			infos.add(new LibraryInfo(currentPath + "/libraryInfo", index)); //$NON-NLS-1$
		}

		libraryList.setLibrariesInfo(infos);
		return true;
	}

	@Override
	public boolean preVisit(LibraryVersions libraryVersions) {
		String currentPath = libraryVersions.getPrefix();
		final int size = getNodesLength(currentPath, "libraryVersion", //$NON-NLS-1$
				libraryVersions.getOccurrence());

		if (size == 0) {
			return false;
		}

		final int overallSize = getPreviousNodesLength(currentPath,
				"libraryVersion", libraryVersions.getOccurrence()); //$NON-NLS-1$

		List<LibraryVersion> versions = new ArrayList<LibraryVersion>(size);
		for (int index = overallSize; index < overallSize + size; index++) {
			versions.add(new LibraryVersion(currentPath + "/libraryVersion", //$NON-NLS-1$
					index));
		}

		libraryVersions.setVersions(versions);
		return true;
	}

	@Override
	public boolean preVisit(LibraryInfo libraryInfo) {
		String currentPath = libraryInfo.getPrefix();
		int occurrence = libraryInfo.getOccurrence();
		String value = getValue(currentPath + "/libraryId", occurrence); //$NON-NLS-1$
		libraryInfo.setLibraryId(Integer.valueOf(value));
		value = getValue(currentPath + "/libraryName", occurrence); //$NON-NLS-1$
		libraryInfo.setLibraryName(value);
		value = getValue(currentPath + "/status", occurrence); //$NON-NLS-1$
		libraryInfo.setStatus(value);
		LibraryVersions versions = new LibraryVersions(currentPath
				+ "/libraryVersions", occurrence); //$NON-NLS-1$
		libraryInfo.setLibraryVersions(versions);
		return true;
	}

	@Override
	public boolean preVisit(LibraryFile libraryFile) {
		Disposition disposition = representation.getDisposition();
		if (disposition != null) {
			libraryFile.setFilename(disposition.getFilename());
		}
		int size = (int) representation.getSize();
		libraryFile.setFileSize(size);

		try {
			byte[] content = new byte[size];
			InputStream reader = representation.getStream();
			int offset = 0;
			while (offset < size) {
				offset += reader.read(content, offset, size - offset);
			}
			libraryFile.setFileContent(content);
		} catch (IOException e) {
			return false;
		}
		return true;
	}

	@Override
	public boolean preVisit(Bootstrap bootstrap) {
		String currentPath = bootstrap.getPrefix();
		int occurrence = bootstrap.getOccurrence();
		ApiKey apiKey = new ApiKey(currentPath + "/apiKey", occurrence); //$NON-NLS-1$
		bootstrap.setApiKey(apiKey);
		String value = getValue(currentPath + "/success", occurrence); //$NON-NLS-1$
		bootstrap.setSuccess(Boolean.valueOf(value));
		return true;
	}

	@Override
	public boolean preVisit(ApiKey apiKey) {
		String currentPath = apiKey.getPrefix();
		int occurrence = apiKey.getOccurrence();
		String value = getValue(currentPath + "/id", occurrence); //$NON-NLS-1$
		apiKey.setId(Integer.valueOf(value));
		value = getValue(currentPath + "/username", occurrence); //$NON-NLS-1$
		apiKey.setUsername(value);
		value = getValue(currentPath + "/name", occurrence); //$NON-NLS-1$
		apiKey.setName(value);
		value = getValue(currentPath + "/hash", occurrence); //$NON-NLS-1$
		apiKey.setHash(value);
		value = getValue(currentPath + "/creationTime", occurrence); //$NON-NLS-1$
		apiKey.setCreationTime(value);
		return true;
	}

	@Override
	public boolean preVisit(VhostsList vhostsList) {
		String currentPath = vhostsList.getPrefix();
		final int size = getNodesLength(currentPath, "vhostInfo", //$NON-NLS-1$
				vhostsList.getOccurrence());

		if (size == 0) {
			return false;
		}

		final int overallSize = getPreviousNodesLength(currentPath,
				"vhostInfo", vhostsList.getOccurrence()); //$NON-NLS-1$

		List<VhostInfo> infos = new ArrayList<VhostInfo>(size);
		for (int index = overallSize; index < overallSize + size; index++) {
			infos.add(new VhostInfo(currentPath + "/vhostInfo", index)); //$NON-NLS-1$
		}

		vhostsList.setVhosts(infos);
		return true;
	}
	
	@Override
	public boolean preVisit(VhostInfo vhostInfo) {
			String currentPath = vhostInfo.getPrefix();
			int occurrence = vhostInfo.getOccurrence();
			String value = getValue(currentPath + "/id", occurrence); //$NON-NLS-1$
			vhostInfo.setId(Integer.valueOf(value));
			value = getValue(currentPath + "/name", occurrence); //$NON-NLS-1$
			vhostInfo.setName(value);
			value = getValue(currentPath + "/port", occurrence); //$NON-NLS-1$
			vhostInfo.setPort(Integer.valueOf(value));
			value = getValue(currentPath + "/default", occurrence); //$NON-NLS-1$
			vhostInfo.setDefaultVhost(Boolean.valueOf(value));
			value = getValue(currentPath + "/ssl", occurrence); //$NON-NLS-1$
			vhostInfo.setSSL(Boolean.valueOf(value));
			return true;
		}

	@Override
	public boolean preVisit(VhostDetails vhostDetails) {
		String currentPath = vhostDetails.getPrefix();
		vhostDetails.setInfo(new VhostInfo(currentPath + "/vhostInfo", 0)); //$NON-NLS-1$
		vhostDetails.setExtendedInfo(new VhostExtendedInfo(currentPath + "/vhostExtended")); //$NON-NLS-1$
		return true;
	}

	@Override
	public boolean preVisit(VhostExtendedInfo vhostExtendedInfo) {
		String currentPath = vhostExtendedInfo.getPrefix();
		String value = getValue(currentPath + "/docRoot"); //$NON-NLS-1$
		vhostExtendedInfo.setDocRoot(value);
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

	/**
	 * @param value
	 * @return long
	 */
	private long parseLongIfExists(String value) {
		if (value == null || value.length() == 0) {
			return 0;
		}
		return Long.parseLong(value);
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
		case CONFIGURATION_EXTENSIONS_LIST:
			return new ExtensionsList();
		case CONFIGURATION_DIRECTIVES_LIST:
			return new DirectivesList();
		case APPLICATIONS_LIST:
			return new ApplicationsList();
		case APPLICATION_INFO:
			return new ApplicationInfo();
		case CODE_TRACING_STATUS:
			return new CodeTracingStatus();
		case CODE_TRACE:
			return new CodeTrace();
		case CODE_TRACING_LIST:
			return new CodeTracingList();
		case CODE_TRACE_FILE:
			return new CodeTraceFile();
		case REQUEST_SUMMARY:
			return new RequestSummary();
		case ISSUE_LIST:
			return new IssueList();
		case ISSUE_DETAILS:
			return new IssueDetails();
		case EVENTS_GROUP_DETAILS:
			return new EventsGroupDetails();
		case ISSUE_FILE:
			return new IssueFile();
		case ISSUE:
			return new Issue();
		case DEBUG_REQUEST:
			return new DebugRequest();
		case PROFILE_REQUEST:
			return new ProfileRequest();
		case DEBUG_MODE:
			return new DebugMode();
		case LIBRARY_LIST:
			return new LibraryList();
		case LIBRARY_INFO:
			return new LibraryInfo();
		case LIBRARY_FILE:
			return new LibraryFile();
		case BOOTSTRAP:
			return new Bootstrap();
		case APIKEY:
			return new ApiKey();
		case VHOSTS_LIST:
			return new VhostsList();
		case VHOST_INFO:
			return new VhostInfo();
		case VHOST_DETAILS:
			return new VhostDetails();
		case VHOST_EXTENDED_INFO:
			return new VhostExtendedInfo();
		default:
			return null;
		}
	}

	private Representation getRepresentation(Representation representation,
			IResponseData responseData) {
		switch (responseData.getType()) {
		case SERVER_CONFIG:
		case CODE_TRACE_FILE:
		case ISSUE_FILE:
		case LIBRARY_FILE:
			return representation;
		default:
			return new DomRepresentation(representation);
		}
	}
}