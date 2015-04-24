/*******************************************************************************
 * Copyright (c) Apr 16, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.webapi.core.connection.data;

import java.util.List;

/**
 * A list of servers which have specific application deployed.
 * 
 * @author Wojtek, 2011
 * 
 */
public class ApplicationServers extends AbstractResponseData {

	private static final String SERVERS = "/servers";
	private List<ApplicationServer> applicationServers;

	protected ApplicationServers() {
		super(ResponseType.APPLICATION_SERVERS_LIST, BASE_PATH + SERVERS,
				SERVERS);
	}

	protected ApplicationServers(String prefix, int occurrance) {
		super(ResponseType.APPLICATION_SERVERS_LIST, prefix, SERVERS,
				occurrance);
	}

	/**
	 * @return Server information. May appear 0 or more times.
	 */
	public List<ApplicationServer> getApplicationServers() {
		return applicationServers;
	}

	public boolean accept(IResponseDataVisitor visitor) {
		boolean visit = visitor.preVisit(this);
		if (visit) {
			if (getApplicationServers() != null) {
				for (ApplicationServer info : getApplicationServers()) {
					info.accept(visitor);
				}
			}
			return visitor.visit(this);
		}
		return false;
	}

	protected void setAapplicationServers(
			List<ApplicationServer> applicationServers) {
		this.applicationServers = applicationServers;
	}

}
