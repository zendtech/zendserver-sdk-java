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

}
