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

	private int id;
	private String baseUrl;
	private String appName;
	private ApplicationStatus status;
	private DeployedVersionsList deployedVersionsList;
	private MessageList messageList;

	protected ApplicationInfo(String prefix, int occurrence) {
		super(ResponseType.APPLICATION_INFO, prefix, occurrence);
	}

	protected ApplicationInfo() {
		this(BASE_PATH + "/applicationInfo", 0);
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
	 * @return Application status (see {@link ApplicationStatus}).
	 */
	public ApplicationStatus getStatus() {
		return status;
	}

	/**
	 * @return List of deployed versions for this application
	 */
	public DeployedVersionsList getDeployedVersionsList() {
		return deployedVersionsList;
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
			if (this.getDeployedVersionsList() != null) {
				this.getDeployedVersionsList().accept(visitor);
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

	protected void setStatus(ApplicationStatus status) {
		this.status = status;
	}

	protected void setDeployedVersionsList(
			DeployedVersionsList deployedVersionsList) {
		this.deployedVersionsList = deployedVersionsList;
	}

	protected void setMessageList(MessageList messageList) {
		this.messageList = messageList;
	}

}
