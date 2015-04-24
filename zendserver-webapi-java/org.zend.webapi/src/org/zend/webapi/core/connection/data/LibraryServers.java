/*******************************************************************************
 * Copyright (c) 2013 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.webapi.core.connection.data;

import java.util.List;

/**
 * A list of library servers.
 * 
 * @author Wojciech Galanciak, 2013
 * 
 */
public class LibraryServers extends AbstractResponseData {

	private static final String SERVERS = "/servers";
	
	private List<LibraryServer> servers;

	protected LibraryServers() {
		super(ResponseType.LIBRARY_SERVERS, BASE_PATH + SERVERS, SERVERS);
	}

	protected LibraryServers(String prefix, int occurrance) {
		super(ResponseType.LIBRARY_SERVERS, prefix, SERVERS, occurrance);
	}

	public List<LibraryServer> getServers() {
		return servers;
	}

	public boolean accept(IResponseDataVisitor visitor) {
		boolean visit = visitor.preVisit(this);
		if (visit) {
			if (getServers() != null) {
				for (LibraryServer server : getServers()) {
					server.accept(visitor);
				}
			}
			return visitor.visit(this);
		}
		return false;
	}

	protected void setServers(List<LibraryServer> servers) {
		this.servers = servers;
	}

}
