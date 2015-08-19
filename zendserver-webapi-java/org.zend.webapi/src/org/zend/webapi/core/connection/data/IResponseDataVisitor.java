/*******************************************************************************
 * Copyright (c) Jan 31, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.webapi.core.connection.data;

/**
 * General usage visitor for any response data.<br>
 * Using the Visitor design pattern to ease the digesting of data objects
 * 
 * @author Roy, 2011
 */
public interface IResponseDataVisitor {

	public boolean preVisit(LicenseInfo licenseInfo);

	public boolean visit(LicenseInfo licenseInfo);

	public boolean preVisit(SystemInfo systemInfo);

	public boolean visit(SystemInfo systemInfo);

	public boolean preVisit(MessageList messageList);

	public boolean visit(MessageList messageList);

	public boolean preVisit(ServersList serversList);

	public boolean visit(ServersList serversList);

	public boolean preVisit(ServerInfo serverInfo);

	public boolean visit(ServerInfo serverInfo);

	public boolean preVisit(ServerConfig serverConfig);

	public boolean visit(ServerConfig serverConfig);

	public boolean preVisit(DeployedVersion deployedVersionInfo);

	public boolean visit(DeployedVersion deployedVersionInfo);

	public boolean preVisit(DeployedVersions deployedVersionsList);

	public boolean visit(DeployedVersions deployedVersionsList);

	public boolean preVisit(ApplicationInfo applicationInfo);

	public boolean visit(ApplicationInfo applicationInfo);

	public boolean preVisit(ApplicationsList applicationsList);

	public boolean visit(ApplicationsList applicationsList);

	public boolean preVisit(ApplicationServer applicationServer);

	public boolean visit(ApplicationServer applicationServer);

	public boolean preVisit(ApplicationServers applicationServersList);

	public boolean visit(ApplicationServers applicationServersList);

	public boolean preVisit(Parameter parameter);

	public boolean visit(Parameter parameter);

	public boolean preVisit(SuperGlobals superGlobals);

	public boolean visit(SuperGlobals superGlobals);

	public boolean preVisit(Step step);

	public boolean visit(Step step);

	public boolean preVisit(CodeTracingStatus codeTracingStatus);

	public boolean visit(CodeTracingStatus codeTracingStatus);

	public boolean preVisit(CodeTrace codeTrace);

	public boolean visit(CodeTrace codeTrace);

	public boolean preVisit(EventsGroup eventsGroup);

	public boolean visit(EventsGroup eventsGroup);

	public boolean preVisit(Event event);

	public boolean visit(Event event);

	public boolean preVisit(Backtrace backtrace);

	public boolean visit(Backtrace backtrace);

	public boolean preVisit(ParameterList parameterList);

	public boolean visit(ParameterList parameterList);

	public boolean preVisit(EventsGroupDetails eventsGroupDetails);

	public boolean visit(EventsGroupDetails eventsGroupDetails);

	public boolean preVisit(RouteDetail routeDetail);

	public boolean visit(RouteDetail routeDetail);

	public boolean preVisit(EventsGroups eventsGroups);

	public boolean visit(EventsGroups eventsGroups);

	public boolean preVisit(RouteDetails routeDetails);

	public boolean visit(RouteDetails routeDetails);

	public boolean preVisit(Issue issue);

	public boolean visit(Issue issue);

	public boolean preVisit(IssueDetails issueDetails);

	public boolean visit(IssueDetails issueDetails);

	public boolean preVisit(Events events);

	public boolean visit(Events events);

	public boolean preVisit(RequestSummary requestSummary);

	public boolean visit(RequestSummary requestSummary);

	public boolean preVisit(CodeTracingList codeTracingList);

	public boolean visit(CodeTracingList codeTracingList);

	public boolean preVisit(CodeTraceFile codeTraceFile);

	public boolean visit(CodeTraceFile codeTraceFile);

	public boolean preVisit(IssueList issueList);

	public boolean visit(IssueList issueList);

	public boolean preVisit(IssueFile issueFile);

	public boolean visit(IssueFile issueFile);

	public boolean preVisit(DebugRequest debugRequest);

	public boolean visit(DebugRequest debugRequest);

	public boolean preVisit(ProfileRequest profileRequest);

	public boolean visit(ProfileRequest profileRequest);

	public boolean preVisit(GeneralDetails generalDetails);

	public boolean visit(GeneralDetails generalDetails);

	public boolean preVisit(DebugMode debugMode);

	public boolean visit(DebugMode debugMode);

	public boolean preVisit(LibraryServer libraryServer);

	public boolean visit(LibraryServer libraryServer);

	public boolean preVisit(LibraryServers libraryServers);

	public boolean visit(LibraryServers libraryServers);

	public boolean preVisit(LibraryVersion libraryVersion);

	public boolean visit(LibraryVersion libraryVersion);

	public boolean preVisit(LibraryVersions libraryVersions);

	public boolean visit(LibraryVersions libraryVersions);

	public boolean preVisit(LibraryInfo libraryInfo);

	public boolean visit(LibraryInfo libraryInfo);

	public boolean preVisit(LibraryList libraryList);

	public boolean visit(LibraryList libraryList);

	public boolean preVisit(LibraryFile libraryFile);

	public boolean visit(LibraryFile libraryFile);

	public boolean visit(Bootstrap bootstrap);

	public boolean preVisit(Bootstrap bootstrap);

	public boolean visit(ApiKey apiKey);

	public boolean preVisit(ApiKey apiKey);

	public boolean visit(VhostsList vhostsList);

	public boolean preVisit(VhostsList vhostsList);
	
	public boolean visit(VhostInfo vhostInfo);
	
	public boolean preVisit(VhostInfo vhostInfo);
	
	public boolean visit(ExtensionsList extensionsList);

	public boolean preVisit(ExtensionsList extensionsList);
	
	public boolean visit(ExtensionInfo extensionInfo);

	public boolean preVisit(ExtensionInfo extensionInfo);
	
	public boolean preVisit(VhostDetails vhostDetails);
	
	public boolean visit(VhostDetails vhostDetails);

	public boolean preVisit(VhostExtendedInfo vhostExtendedInfo);
	
	public boolean visit(VhostExtendedInfo vhostExtendedInfo);
}
