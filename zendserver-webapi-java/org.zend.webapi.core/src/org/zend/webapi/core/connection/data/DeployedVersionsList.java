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
public class DeployedVersionsList extends AbstractResponseData {

	private List<DeployedVersionInfo> deployedVersionInfo;

	protected DeployedVersionsList() {
		super(ResponseType.DEPLOYED_VERSIONS_LIST, BASE_PATH
				+ "/deployedVersionsList");
	}

	protected DeployedVersionsList(String prefix) {
		super(ResponseType.DEPLOYED_VERSIONS_LIST, prefix);
	}

	/**
	 * @return Version information. May appear 0 or more times
	 */
	public List<DeployedVersionInfo> getDeployedVersionInfo() {
		return deployedVersionInfo;
	}

	public boolean accept(IResponseDataVisitor visitor) {
		boolean visit = visitor.preVisit(this);
		if (visit) {
			if (getDeployedVersionInfo() != null) {
				for (DeployedVersionInfo info : getDeployedVersionInfo()) {
					info.accept(visitor);
				}
			}
			return visitor.visit(this);
		}
		return false;
	}

	protected void setDeployedVersionInfo(
			List<DeployedVersionInfo> deployedVersionInfo) {
		this.deployedVersionInfo = deployedVersionInfo;
	}

}
