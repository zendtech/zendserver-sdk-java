/*******************************************************************************
 * Copyright (c) 2013 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.webapi.core.connection.data;

/**
 * @author Wojciech Galanciak, 2013
 * @since 1.3
 */
public class Bootstrap extends AbstractResponseData {

	private static final String BOOTSTRAP = "/bootstrap";
	private Boolean success;
	private ApiKey apiKey;

	protected Bootstrap() {
		super(ResponseType.BOOTSTRAP, BASE_PATH + BOOTSTRAP, BOOTSTRAP);
	}

	protected Bootstrap(String prefix, int occurrance) {
		super(ResponseType.BOOTSTRAP, prefix, BOOTSTRAP, occurrance);
	}

	public boolean accept(IResponseDataVisitor visitor) {
		boolean visit = visitor.preVisit(this);
		if (visit) {
			if (this.getApiKey() != null) {
				this.getApiKey().accept(visitor);
			}
			return visitor.visit(this);
		}
		return false;
	}

	public boolean getSuccess() {
		return success;
	}

	public ApiKey getApiKey() {
		return apiKey;
	}

	protected void setSuccess(Boolean success) {
		this.success = success;
	}

	protected void setApiKey(ApiKey apiKey) {
		this.apiKey = apiKey;
	}

}
