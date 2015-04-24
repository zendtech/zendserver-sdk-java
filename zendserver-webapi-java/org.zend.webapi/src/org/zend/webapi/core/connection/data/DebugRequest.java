/*******************************************************************************
 * Copyright (c) Feb 13, 2012 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.webapi.core.connection.data;



/**
 * Debug request result.
 * 
 * @author Wojciech Galanciak, 2012
 * @since 1.2
 */
public class DebugRequest extends AbstractResponseData {

	private static final String DEBUG_REQUEST = "/debugRequest";
	
	private String success;
	private String message;

	protected DebugRequest() {
		super(ResponseType.DEBUG_REQUEST, AbstractResponseData.BASE_PATH
				+ DEBUG_REQUEST, DEBUG_REQUEST, 0);
	}

	protected DebugRequest(String prefix, int occurrence) {
		super(ResponseType.DEBUG_REQUEST, prefix, DEBUG_REQUEST, occurrence);
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
