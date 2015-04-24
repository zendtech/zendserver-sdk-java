/*******************************************************************************
 * Copyright (c) Apr 11, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.webapi.core.connection.data;

/**
 * Information about a specific deployed version of an application.
 * 
 * @author Wojtek, 2011
 * 
 */
public class DeployedVersion extends AbstractResponseData {

	private static final String DEPLOYED_VERSION = "/deployedVersion";
	
	private String version;

	protected DeployedVersion() {
		super(ResponseType.DEPLOYED_VERSION, AbstractResponseData.BASE_PATH
				+ DEPLOYED_VERSION, DEPLOYED_VERSION, 0);
	}

	protected DeployedVersion(String prefix, int occurrence) {
		super(ResponseType.DEPLOYED_VERSION, prefix, DEPLOYED_VERSION,
				occurrence);
	}

	/**
	 * @return Deployed version number of the application.
	 */
	public String getVersion() {
		return version;
	}

	public boolean accept(IResponseDataVisitor visitor) {
		if (visitor.preVisit(this)) {
			return visitor.visit(this);
		}
		return false;
	}

	protected void setVersion(String version) {
		this.version = version;
	}

}
