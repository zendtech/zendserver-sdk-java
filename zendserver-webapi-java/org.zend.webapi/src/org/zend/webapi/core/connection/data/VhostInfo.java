/*******************************************************************************
 * Copyright (c) 2015 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.webapi.core.connection.data;

/**
 * Information about a vhost.
 * 
 * @author Wojciech Galanciak, 2015
 * 
 */
public class VhostInfo extends AbstractResponseData {

	private static final String VHOST_INFO = "/vhostInfo";

	private int id;
	private String name;
	private int port;
	private boolean defaultVhost;
	private boolean ssl;

	protected VhostInfo(String prefix, int occurrence) {
		super(ResponseType.VHOST_INFO, prefix, VHOST_INFO, occurrence);
	}

	protected VhostInfo() {
		this(BASE_PATH + VHOST_INFO, 0);
	}

	public static String getVhostInfo() {
		return VHOST_INFO;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public int getPort() {
		return port;
	}

	public boolean isDefaultVhost() {
		return defaultVhost;
	}

	public boolean isSSL() {
		return ssl;
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

	protected void setName(String name) {
		this.name = name;
	}

	protected void setPort(int port) {
		this.port = port;
	}

	protected void setDefaultVhost(boolean defaultVhost) {
		this.defaultVhost = defaultVhost;
	}

	protected void setSSL(boolean ssl) {
		this.ssl = ssl;
	}

}
