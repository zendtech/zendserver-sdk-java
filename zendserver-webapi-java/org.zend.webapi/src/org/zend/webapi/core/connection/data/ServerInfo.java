/*******************************************************************************
 * Copyright (c) Jan 27, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.webapi.core.connection.data;

import org.zend.webapi.core.connection.data.values.ServerStatus;


/**
 * An object representing a single server with information about the server
 * 
 * @author Roy, 2011
 * 
 */
public class ServerInfo extends AbstractResponseData {

	private static final String SERVER_INFO = "/serverInfo";
	
	private int id;
	private String name;
	private String address;
	private ServerStatus status;
	private MessageList messageList;

	protected ServerInfo(String prefix, int occurrence) {
		super(ResponseType.SERVER_INFO, prefix, SERVER_INFO, occurrence);
	}

	protected ServerInfo() {
		this(BASE_PATH + SERVER_INFO, 0);
	}

	/**
	 * @return server id
	 */
	public int getId() {
		return id;
	}
	
	/**
	 * @return Server name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return Server address as HTTP URL
	 */
	public String getAddress() {
		return address;
	}

	/**
	 * @return Server Status
	 */
	public ServerStatus getStatus() {
		return status;
	}

	/**
	 * @return List of messages reported by this server. Can be empty if there
	 *         are no messages to show
	 */
	public MessageList getMessageList() {
		return messageList;
	}

	public boolean accept(IResponseDataVisitor visitor) {
		boolean visit = visitor.preVisit(this);
		if (visit) {
			if (this.getMessageList() != null) {
				this.getMessageList().accept(visitor);
			}
			return visitor.visit(this);
		}
		return false;
	}

	protected void setName(String name) {
		this.name = name;
	}

	protected void setAddress(String address) {
		this.address = address;
	}

	protected void setStatus(ServerStatus status) {
		this.status = status;
	}

	protected void setMessageList(MessageList messageList) {
		this.messageList = messageList;
	}

	protected void setId(int id) {
		this.id = id;
	}

}
