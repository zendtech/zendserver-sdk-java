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

}
