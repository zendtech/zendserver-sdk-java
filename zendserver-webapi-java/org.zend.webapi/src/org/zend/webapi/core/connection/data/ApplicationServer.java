/*******************************************************************************
 * Copyright (c) Apr 16, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.webapi.core.connection.data;

import org.zend.webapi.core.connection.data.values.ApplicationStatus;

/**
 * Information about aN application status one a specific server.
 * 
 * @author Wojtek, 2011
 * 
 */
public class ApplicationServer extends AbstractResponseData {

	private static final String APPLICATION_SERVER = "/applicationServer";
	
	private int id;
	private String deployedVersion;
	private ApplicationStatus status;

	protected ApplicationServer() {
		super(ResponseType.APPLICATION_SERVER, AbstractResponseData.BASE_PATH
				+ APPLICATION_SERVER, APPLICATION_SERVER, 0);
	}

	protected ApplicationServer(String prefix, int occurrence) {
		super(ResponseType.APPLICATION_SERVER, prefix, APPLICATION_SERVER,
				occurrence);
	}

	/**
	 * @return Server ID
	 */
	public int getId() {
		return id;
	}

	/**
	 * @return The latest version of the application identified on the server.
	 */
	public String getDeployedVersion() {
		return deployedVersion;
	}

	/**
	 * @return The deployedVersion's status, see {@link ApplicationStatus}.
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

	public void setDeployedVersion(String deployedVersion) {
		this.deployedVersion = deployedVersion;
	}

	protected void setId(int id) {
		this.id = id;
	}

	protected void setStatus(ApplicationStatus status) {
		this.status = status;
	}

}
