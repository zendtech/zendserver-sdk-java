/*******************************************************************************
 * Copyright (c) Jan 27, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.webapi.core.connection.data;

import java.util.List;

/**
 * messageList A list of 0 of more messages
 * 
 * @author Roy, 2011
 * 
 *         3.4.2.1Parameter Type Count Description Info String 0+ Info-level
 *         message (may appear 0 or more times) Warning String 0+ Warning-level
 *         message (may appear 0 or more times) Error String 0+ Error-level
 *         message (may appear 0 or more times)
 */
public class MessageList extends AbstractResponseData {

	private static final String MESSAGE_LIST = "/messageList";
	
	private List<String> info;
	private List<String> warning;
	private List<String> error;

	protected MessageList(String prefix, int occurrance) {
		super(ResponseType.MESSAGE_LIST, prefix, MESSAGE_LIST, occurrance);
	}

	protected MessageList() {
		this(AbstractResponseData.BASE_PATH + MESSAGE_LIST, 0);
	}

	/**
	 * Info-level message (may appear 0 or more times)
	 */
	public List<String> getInfo() {
		return info;
	}

	/**
	 * Warning-level message (may appear 0 or more times)
	 */
	public List<String> getWarning() {
		return warning;
	}

	/**
	 * Error-level message (may appear 0 or more times)
	 */
	public List<String> getError() {
		return error;
	}

	public boolean accept(IResponseDataVisitor visitor) {
		if (visitor.preVisit(this)) {
			return visitor.visit(this);
		}
		return false;
	}

	protected void setInfo(List<String> info) {
		this.info = info;
	}

	protected void setWarning(List<String> warning) {
		this.warning = warning;
	}

	protected void setError(List<String> error) {
		this.error = error;
	}
}
