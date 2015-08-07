/*******************************************************************************
 * Copyright (c) Feb 9, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.webapi.core.connection.data;

/**
 * Generic purpose response data visitor.
 * 
 * @author Roy, 2011
 */
public class GenericResponseDataVisitor implements IResponseDataVisitor {

	public boolean preVisit(LicenseInfo licenseInfo) {
		return true;
	}

	public boolean visit(LicenseInfo licenseInfo) {
		return true;
	}

	public boolean preVisit(SystemInfo systemInfo) {
		return true;
	}

	public boolean visit(SystemInfo systemInfo) {
		return true;
	}

	public boolean preVisit(MessageList messageList) {
		return true;
	}

	public boolean visit(MessageList messageList) {
		return true;
	}

	public boolean preVisit(ServersList serversList) {
		return true;
	}

	public boolean visit(ServersList serversList) {
		return true;
	}

	public boolean preVisit(ServerInfo serverInfo) {
		return true;
	}

	public boolean visit(ServerInfo serverInfo) {
		return true;
	}

	public boolean preVisit(ServerConfig serverConfig) {
		return true;
	}

	public boolean visit(ServerConfig serverConfig) {
		return true;
	}

	public boolean preVisit(DeployedVersion deployedVersionInfo) {
		return true;
	}

	public boolean visit(DeployedVersion deployedVersionInfo) {
		return true;
	}

	public boolean preVisit(DeployedVersions deployedVersionsList) {
		return true;
	}

	public boolean visit(DeployedVersions deployedVersionsList) {
		return true;
	}

	public boolean preVisit(ApplicationInfo applicationInfo) {
		return true;
	}

	public boolean visit(ApplicationInfo applicationInfo) {
		return true;
	}

	public boolean preVisit(ApplicationsList applicationsList) {
		return true;
	}

	public boolean visit(ApplicationsList applicationsList) {
		return true;
	}

	public boolean preVisit(ApplicationServer applicationServer) {
		return true;
	}

	public boolean visit(ApplicationServer applicationServer) {
		return true;
	}

	public boolean preVisit(ApplicationServers applicationServersList) {
		return true;
	}

	public boolean visit(ApplicationServers applicationServersList) {
		return true;
	}

	public boolean preVisit(Parameter parameter) {
		return true;
	}

	public boolean visit(Parameter parameter) {
		return true;
	}

	public boolean preVisit(SuperGlobals superGlobals) {
		return true;
	}

	public boolean visit(SuperGlobals superGlobals) {
		return true;
	}

	public boolean preVisit(Step step) {
		return true;
	}

	public boolean visit(Step step) {
		return true;
	}

	public boolean preVisit(CodeTracingStatus codeTracingStatus) {
		return true;
	}

	public boolean visit(CodeTracingStatus codeTracingStatus) {
		return true;
	}

	public boolean preVisit(CodeTrace codeTrace) {
		return true;
	}

	public boolean visit(CodeTrace codeTrace) {
		return true;
	}

	public boolean preVisit(EventsGroup eventsGroup) {
		return true;
	}

	public boolean visit(EventsGroup eventsGroup) {
		return true;
	}

	public boolean preVisit(Event event) {
		return true;
	}

	public boolean visit(Event event) {
		return true;
	}

	public boolean preVisit(Backtrace backtrace) {
		return true;
	}

	public boolean visit(Backtrace backtrace) {
		return true;
	}

	public boolean preVisit(ParameterList parameterList) {
		return true;
	}

	public boolean visit(ParameterList parameterList) {
		return true;
	}

	public boolean preVisit(EventsGroupDetails eventsGroupDetails) {
		return true;
	}

	public boolean visit(EventsGroupDetails eventsGroupDetails) {
		return true;
	}

	public boolean preVisit(RouteDetail routeDetail) {
		return true;
	}

	public boolean visit(RouteDetail routeDetail) {
		return true;
	}

	public boolean preVisit(EventsGroups eventsGroups) {
		return true;
	}

	public boolean visit(EventsGroups eventsGroups) {
		return true;
	}

	public boolean preVisit(RouteDetails routeDetails) {
		return true;
	}

	public boolean visit(RouteDetails routeDetails) {
		return true;
	}

	public boolean preVisit(Issue issue) {
		return true;
	}

	public boolean visit(Issue issue) {
		return true;
	}

	public boolean preVisit(IssueDetails issueDetails) {
		return true;
	}

	public boolean visit(IssueDetails issueDetails) {
		return true;
	}

	public boolean preVisit(Events events) {
		return true;
	}

	public boolean visit(Events events) {
		return true;
	}

	public boolean preVisit(RequestSummary requestSummary) {
		return true;
	}

	public boolean visit(RequestSummary requestSummary) {
		return true;
	}

	public boolean preVisit(CodeTracingList codeTracingList) {
		return true;
	}

	public boolean visit(CodeTracingList codeTracingList) {
		return true;
	}

	public boolean preVisit(CodeTraceFile codeTraceFile) {
		return true;
	}

	public boolean visit(CodeTraceFile codeTraceFile) {
		return true;
	}

	public boolean preVisit(IssueList issueList) {
		return true;
	}

	public boolean visit(IssueList issueList) {
		return true;
	}

	public boolean preVisit(IssueFile issueFile) {
		return true;
	}

	public boolean visit(IssueFile issueFile) {
		return true;
	}

	public boolean preVisit(DebugRequest debugRequest) {
		return true;
	}

	public boolean visit(DebugRequest debugRequest) {
		return true;
	}

	public boolean preVisit(ProfileRequest profileRequest) {
		return true;
	}

	public boolean visit(ProfileRequest profileRequest) {
		return true;
	}

	public boolean preVisit(GeneralDetails generalDetails) {
		return true;
	}

	public boolean visit(GeneralDetails generalDetails) {
		return true;
	}

	public boolean preVisit(DebugMode debugMode) {
		return true;
	}

	public boolean visit(DebugMode debugMode) {
		return true;
	}

	public boolean preVisit(LibraryServer libraryServer) {
		return true;
	}

	public boolean visit(LibraryServer libraryServer) {
		return true;
	}

	public boolean preVisit(LibraryServers libraryServers) {
		return true;
	}

	public boolean visit(LibraryServers libraryServers) {
		return true;
	}

	public boolean preVisit(LibraryVersion libraryVersion) {
		return true;
	}

	public boolean visit(LibraryVersion libraryVersion) {
		return true;
	}

	public boolean preVisit(LibraryVersions libraryVersions) {
		return true;
	}

	public boolean visit(LibraryVersions libraryVersions) {
		return true;
	}

	public boolean preVisit(LibraryInfo libraryInfo) {
		return true;
	}

	public boolean visit(LibraryInfo libraryInfo) {
		return true;
	}

	public boolean preVisit(LibraryList libraryList) {
		return true;
	}

	public boolean visit(LibraryList libraryList) {
		return true;
	}

	public boolean preVisit(LibraryFile libraryFile) {
		return true;
	}

	public boolean visit(LibraryFile libraryFile) {
		return true;
	}

	public boolean visit(Bootstrap bootstrap) {
		return true;
	}

	public boolean preVisit(Bootstrap bootstrap) {
		return true;
	}

	public boolean visit(ApiKey apiKey) {
		return true;
	}

	public boolean preVisit(ApiKey apiKey) {
		return true;
	}

	public boolean visit(VhostsList vhostsList) {
		return true;
	}

	public boolean preVisit(VhostsList vhostsList) {
		return true;
	}

	public boolean preVisit(VhostInfo vhostInfo) {
		return true;
	}

	public boolean visit(VhostInfo vhostInfo) {
		return true;
	}
	
	public boolean visit(ExtensionsList extensionsList) {
		return true;
	}

	public boolean preVisit(ExtensionsList extensionsList) {
		return true;
	}

	public boolean visit(ExtensionInfo extensionInfo) {
		return true;
	}

	public boolean preVisit(ExtensionInfo extensionInfo) {
		return true;
	}

}
