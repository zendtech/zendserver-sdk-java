/*******************************************************************************
 * Copyright (c) 2013 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.webapi.core.connection.data;

/**
 * Information about a specific library server.
 * 
 * @author Wojciech Galanciak, 2013
 * 
 */
public class LibraryServer extends AbstractResponseData {

	private static final String LIBRARY_SERVER = "/libraryServer";
	
	private int id;
	private String status;
	private String lastMessage;
	private String lastUpdatedTimestamp;

	protected LibraryServer(String prefix, int occurrence) {
		super(ResponseType.LIBRARY_SERVER, prefix, LIBRARY_SERVER, occurrence);
	}

	protected LibraryServer() {
		this(BASE_PATH + LIBRARY_SERVER, 0);
	}

	public int getId() {
		return id;
	}

	public String getStatus() {
		return status;
	}

	public String getLastMessage() {
		return lastMessage;
	}

	public String getLastUpdatedTimestamp() {
		return lastUpdatedTimestamp;
	}

	public boolean accept(IResponseDataVisitor visitor) {
		if (visitor.preVisit(this)) {
			return visitor.visit(this);
		}
		return false;
	}

	protected void setId(int id) {
		this.id = id;
	}

	protected void setStatus(String status) {
		this.status = status;
	}

	protected void setLastMessage(String lastMessage) {
		this.lastMessage = lastMessage;
	}

	protected void setLastUpdatedTimestamp(String lastUpdatedTimestamp) {
		this.lastUpdatedTimestamp = lastUpdatedTimestamp;
	}

}
