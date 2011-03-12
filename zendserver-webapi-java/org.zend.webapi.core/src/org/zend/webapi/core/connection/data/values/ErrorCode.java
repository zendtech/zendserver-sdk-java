/*******************************************************************************
 * Copyright (c) Jan 27, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.webapi.core.connection.data.values;

/**
 * Error code 
 * @author Roy, 2011
 *
 */
public enum ErrorCode {

	missingHttpHeader(400, "A required HTTP header is missing"),

	unexpectedHttpMethod(400,
			"Unexpected HTTP method, GET used but POST expected"),

	invalidParameter(400, "One or more request parameter contains invalid data"),

	missingParameter(400, "Request is missing a required parameter"),

	unknownMethod(400, "Unknown Zend Server API method"),

	malformedRequest(400, "The server is unable to understand the request"),

	authError(401,
			"Authentication error, unknown key or invalid request signature"),

	insufficientAccessLevel(401,
			"User is not authorized to perform this action"),

	timeSkewError(401, "Request timestamp deviates too much from server time"),

	directAccessForbidden(403,
			"Direct access to a ZSCM managed Server is not allowed"),

	notImplementedByEdition(405,
			"Method is not implemented by this edition of Zend Server"),

	unsupportedApiVersion(406,
			"API version is not supported by this version of Zend Server"),

	internalServerError(500, "An unexpected error on the server side"),

	serverNotConfigured(
			500,
			"This Zend Server installation was not yet initialized (user did not go through the initial setup wizard)"),

	serverNotLicensed(
			500,
			"Server does not have a valid license which is required to perform this operation");

	private final int code;
	private final String description;

	private ErrorCode(int code, String description) {
		this.code = code;
		this.description = description;
	}

	public int getCode() {
		return code;
	}

	public String getMessage() {
		return description;
	}

}
