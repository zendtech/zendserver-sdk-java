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

}
