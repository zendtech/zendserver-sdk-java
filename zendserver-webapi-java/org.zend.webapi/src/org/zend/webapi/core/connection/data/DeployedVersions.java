/*******************************************************************************
 * Copyright (c) Apr 11, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.webapi.core.connection.data;

import java.util.List;

/**
 * A list of deployed versions, contained in an applicationInfo object. May be
 * empty.
 * 
 * @author Wojtek, 2011
 * 
 */
public class DeployedVersions extends AbstractResponseData {

	private static final String DEPLOYED_VERSIONS = "/deployedVersions";
	
	private List<DeployedVersion> deployedVersions;

	protected DeployedVersions() {
		super(ResponseType.DEPLOYED_VERSIONS_LIST, BASE_PATH
				+ DEPLOYED_VERSIONS, DEPLOYED_VERSIONS, 0);
	}

	protected DeployedVersions(String prefix, int occurance) {
		super(ResponseType.DEPLOYED_VERSIONS_LIST, prefix, DEPLOYED_VERSIONS,
				occurance);
	}

	/**
	 * @return Version information. May appear 0 or more times
	 */
	public List<DeployedVersion> getDeployedVersions() {
		return deployedVersions;
	}

	public boolean accept(IResponseDataVisitor visitor) {
		boolean visit = visitor.preVisit(this);
		if (visit) {
			if (getDeployedVersions() != null) {
				for (DeployedVersion info : getDeployedVersions()) {
					info.accept(visitor);
				}
			}
			return visitor.visit(this);
		}
		return false;
	}

	protected void setDeployedVersions(List<DeployedVersion> deployedVersions) {
		this.deployedVersions = deployedVersions;
	}

}
