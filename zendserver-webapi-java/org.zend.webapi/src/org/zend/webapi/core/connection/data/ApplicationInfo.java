/*******************************************************************************
 * Copyright (c) Apr 11, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.webapi.core.connection.data;

import org.zend.webapi.core.connection.data.values.ApplicationStatus;

/**
 * Information about a specific deployed application.
 * 
 * @author Wojtek, 2011
 * 
 */
public class ApplicationInfo extends AbstractResponseData {

	private static final String APPLICATION_INFO = "/applicationInfo";
	
	private int id;
	private String baseUrl;
	private String appName;
	private String userAppName;
	private String installedLocation;
	private ApplicationStatus status;
	private ApplicationServers servers;
	private DeployedVersions deployedVersions;
	private MessageList messageList;

	protected ApplicationInfo(String prefix, int occurrence) {
		super(ResponseType.APPLICATION_INFO, prefix, APPLICATION_INFO,
				occurrence);
	}

	protected ApplicationInfo() {
		this(BASE_PATH + APPLICATION_INFO, 0);
	}

	/**
	 * @return Application ID.
	 */
	public int getId() {
		return id;
	}

	/**
	 * @return Application base URL.
	 */
	public String getBaseUrl() {
		return baseUrl;
	}

	/**
	 * @return Application name.
	 */
	public String getAppName() {
		return appName;
	}

	/**
	 * @return Free text for user defined application identifier.
	 */
	public String getUserAppName() {
		return userAppName;
	}

	/**
	 * @return The location on the file system where the application's source
	 *         code is located.
	 */
	public String getInstalledLocation() {
		return installedLocation;
	}

	/**
	 * @return Application status (see {@link ApplicationStatus}).
	 */
	public ApplicationStatus getStatus() {
		return status;
	}

	/**
	 * @return Breakdown of the the application status and version per server.
	 */
	public ApplicationServers getServers() {
		return servers;
	}

	/**
	 * @return List of deployed versions for this application.
	 */
	public DeployedVersions getDeployedVersions() {
		return deployedVersions;
	}

	/**
	 * @return A list of messages related to this application.
	 */
	public MessageList getMessageList() {
		return messageList;
	}

	public boolean accept(IResponseDataVisitor visitor) {
		boolean visit = visitor.preVisit(this);
		if (visit) {
			if (this.getServers() != null) {
				this.getServers().accept(visitor);
			}
			if (this.getDeployedVersions() != null) {
				this.getDeployedVersions().accept(visitor);
			}
			if (this.getMessageList() != null) {
				this.getMessageList().accept(visitor);
			}
			return visitor.visit(this);
		}
		return false;
	}

	protected void setId(int id) {
		this.id = id;
	}

	protected void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}

	protected void setAppName(String appName) {
		this.appName = appName;
	}

	protected void setUserAppName(String userAppName) {
		this.userAppName = userAppName;
	}

	protected void setInstalledLocation(String installedLocation) {
		this.installedLocation = installedLocation;
	}

	protected void setStatus(ApplicationStatus status) {
		this.status = status;
	}

	protected void setServers(ApplicationServers servers) {
		this.servers = servers;
	}

	protected void setDeployedVersions(DeployedVersions deployedVersions) {
		this.deployedVersions = deployedVersions;
	}

	protected void setMessageList(MessageList messageList) {
		this.messageList = messageList;
	}

}
