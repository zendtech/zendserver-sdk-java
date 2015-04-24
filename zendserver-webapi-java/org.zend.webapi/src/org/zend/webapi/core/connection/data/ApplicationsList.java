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
 * A list of 0 or more applications.
 * 
 * @author Wojtek, 2011
 * 
 */
public class ApplicationsList extends AbstractResponseData {

	private static final String APPLICATIONS_LIST = "/applicationsList";
	
	private List<ApplicationInfo> applicationsInfo;

	protected ApplicationsList() {
		super(ResponseType.APPLICATIONS_LIST, BASE_PATH + APPLICATIONS_LIST,
				APPLICATIONS_LIST);
	}

	protected ApplicationsList(String prefix) {
		super(ResponseType.APPLICATIONS_LIST, prefix, APPLICATIONS_LIST);
	}

	/**
	 * @return Application information. May appear 0 or more times.
	 */
	public List<ApplicationInfo> getApplicationsInfo() {
		return applicationsInfo;
	}

	public boolean accept(IResponseDataVisitor visitor) {
		boolean visit = visitor.preVisit(this);
		if (visit) {
			if (getApplicationsInfo() != null) {
				for (ApplicationInfo info : getApplicationsInfo()) {
					info.accept(visitor);
				}
			}
			return visitor.visit(this);
		}
		return false;
	}

	protected void setApplicationsInfo(List<ApplicationInfo> applicationsInfo) {
		this.applicationsInfo = applicationsInfo;
	}

}
