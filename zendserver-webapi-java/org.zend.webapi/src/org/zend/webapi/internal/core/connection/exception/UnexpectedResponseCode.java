/*******************************************************************************
 * Copyright (c) Feb 28, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.webapi.internal.core.connection.exception;

import java.io.IOException;

import org.restlet.data.MediaType;
import org.restlet.ext.xml.DomRepresentation;
import org.restlet.representation.Representation;
import org.w3c.dom.Node;
import org.zend.webapi.core.WebApiClient;
import org.zend.webapi.core.WebApiException;
import org.zend.webapi.core.connection.response.ResponseCode;

/**
 * Unexpected response code
 * 
 * @author Roy, 2011
 * 
 */
public class UnexpectedResponseCode extends WebApiException {

	private static final long serialVersionUID = -2095471259882902506L;

	final private int httpCode;
	private String message;
	private String errorCode;

	public UnexpectedResponseCode(int httpCode, Representation handle) {
		this.httpCode = httpCode;
		ResponseCode code = null;
		if (MediaType.TEXT_HTML.equals(handle.getMediaType())) {
			switch (httpCode) {
			case 401:
				code = ResponseCode.NO_XML_UNAUTORIZED;
				break;
			case 500:
				code = ResponseCode.INTERNAL_SERVER_ERROR;
				break;
			default:
				WebApiClient.logError("unknown response code: " + httpCode);
				try {
					WebApiClient.logError("unknown response content:\n" + handle.getText());
				} catch (IOException e) {
					// ignore
				}
				code = ResponseCode.PAGE_NOT_FOUND;
			}
			this.errorCode = code.getErrorCode();
			this.message = code.getDescription();
		} else {
			final DomRepresentation domRepresentation = new DomRepresentation(
					handle);
			try {
				Node node = domRepresentation
						.getNode("/zendServerAPIResponse/errorData/errorMessage");
				this.message = node == null ? null : node.getTextContent()
						.trim();
				node = domRepresentation
						.getNode("/zendServerAPIResponse/errorData/errorCode");
				this.errorCode = node == null ? null : node.getTextContent()
						.trim();
			} catch (RuntimeException ex) {
				code = ResponseCode.PAGE_NOT_FOUND;
				this.errorCode = code.getErrorCode();
				this.message = code.getDescription();
			}
		}
	}

	@Override
	public String getMessage() {
		return message != null ? message : getResponseCode().getDescription();
	}

	@Override
	public ResponseCode getResponseCode() {
		if (httpCode == 200) {
			return ResponseCode.OK;
		}
		if (httpCode == 202) {
			return ResponseCode.ACCEPTED;
		}
		return ResponseCode.byErrorCode(errorCode);
	}
}
