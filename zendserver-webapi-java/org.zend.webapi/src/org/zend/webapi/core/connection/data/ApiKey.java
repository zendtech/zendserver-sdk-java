/*******************************************************************************
 * Copyright (c) 2013 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.webapi.core.connection.data;

/**
 * 
 * @author Wojciech Galanciak, 2013
 * @since 1.3
 */
public class ApiKey extends AbstractResponseData {

	private static final String API_KEY = "/apiKey";
	private int id;
	private String username;
	private String name;
	private String hash;
	private String creationTime;

	protected ApiKey() {
		super(ResponseType.APIKEY, BASE_PATH + API_KEY, API_KEY);
	}

	protected ApiKey(String prefix, int occurrance) {
		super(ResponseType.APIKEY, prefix, API_KEY, occurrance);
	}

	public boolean accept(IResponseDataVisitor visitor) {
		boolean visit = visitor.preVisit(this);
		if (visit) {
			return visitor.visit(this);
		}
		return false;
	}

	public int getId() {
		return id;
	}

	public String getUsername() {
		return username;
	}

	public String getName() {
		return name;
	}

	public String getHash() {
		return hash;
	}

	public String getCreationTime() {
		return creationTime;
	}

	protected void setId(int id) {
		this.id = id;
	}

	protected void setUsername(String username) {
		this.username = username;
	}

	protected void setName(String name) {
		this.name = name;
	}

	protected void setHash(String hash) {
		this.hash = hash;
	}

	protected void setCreationTime(String creationTime) {
		this.creationTime = creationTime;
	}

}
