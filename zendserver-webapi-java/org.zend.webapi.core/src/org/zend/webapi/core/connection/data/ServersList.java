/*******************************************************************************
 * Copyright (c) Jan 27, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.webapi.core.connection.data;

import java.util.List;

/**
 * A list of servers Parameter Type Count Description serverInfo serverInfo 0+
 * Server information (may appear more than once)
 * 
 * @author Roy, 2011
 * 
 */
public class ServersList extends AbstractResponseData {

	private static final String SERVERS_LIST = "/serversList";
	
	private List<ServerInfo> serverInfo;

	protected ServersList() {
		super(IResponseData.ResponseType.SERVERS_LIST,
				BASE_PATH + SERVERS_LIST, SERVERS_LIST);
	}

	protected ServersList(String prefix) {
		super(IResponseData.ResponseType.SERVERS_LIST, SERVERS_LIST, prefix);
	}

	/**
	 * @return Server information (may appear more than once)
	 */
	public List<ServerInfo> getServerInfo() {
		return serverInfo;
	}

	public boolean accept(IResponseDataVisitor visitor) {
		boolean visit = visitor.preVisit(this);
		if (visit) {
			if (getServerInfo() != null) {
				for (ServerInfo info : getServerInfo()) {
					info.accept(visitor);
				}
			}
			return visitor.visit(this);
		}
		return false;
	}

	protected void setServerInfo(List<ServerInfo> serverInfo) {
		this.serverInfo = serverInfo;
	}
}
