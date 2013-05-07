/*******************************************************************************
 * Copyright (c) Feb 13, 2012 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.webapi.core.connection.data;



/**
 * Profile request result.
 * 
 * @author Wojciech Galanciak, 2012
 * @since 1.2
 */
public class ProfileRequest extends AbstractResponseData {

	private static final String PROFILE_REQUEST = "/profileRequest";

	private String success;
	private String message;

	protected ProfileRequest() {
		super(ResponseType.PROFILE_REQUEST, AbstractResponseData.BASE_PATH
				+ PROFILE_REQUEST, PROFILE_REQUEST, 0);
	}

	protected ProfileRequest(String prefix, int occurrence) {
		super(ResponseType.PROFILE_REQUEST, prefix, PROFILE_REQUEST, occurrence);
	}

	public boolean accept(IResponseDataVisitor visitor) {
		if (visitor.preVisit(this)) {
			return visitor.visit(this);
		}
		return false;
	}

	/**
	 * @return request result
	 */
	public String getSuccess() {
		return success;
	}

	/**
	 * @return message
	 */
	public String getMessage() {
		return message;
	}

	protected void setSuccess(String success) {
		this.success = success;
	}

	protected void setMessage(String message) {
		this.message = message;
	}

}
