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
 * Information about a specific deployed version of an application.
 * 
 * @author Wojtek, 2011
 * 
 */
public class DeployedVersionInfo extends AbstractResponseData {

	private String id;
	private String version;
	private ApplicationStatus status;

	protected DeployedVersionInfo() {
		super(ResponseType.DEPLOYED_VERSION_INFO,
				AbstractResponseData.BASE_PATH + "/versionInfo", 0);
	}

	protected DeployedVersionInfo(String prefix, int occurrence) {
		super(ResponseType.DEPLOYED_VERSION_INFO, prefix, occurrence);
	}

	/**
	 * @return Zend Server's internal ID of the deployed version.
	 */
	public String getId() {
		return id;
	}

	/**
	 * @return Deployed version number of the application.
	 */
	public String getVersion() {
		return version;
	}

	/**
	 * @return Deployed version status (see {@link ApplicationStatus}).
	 */
	public ApplicationStatus getStatus() {
		return status;
	}

	public boolean accept(IResponseDataVisitor visitor) {
		if (visitor.preVisit(this)) {
			return visitor.visit(this);
		}
		return false;
	}

	protected void setStatus(ApplicationStatus status) {
		this.status = status;
	}

	protected void setVersion(String version) {
		this.version = version;
	}

	protected void setId(String id) {
		this.id = id;
	}

}
