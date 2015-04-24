/*******************************************************************************
 * Copyright (c) Jan 31, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.webapi.core.connection.data;

/**
 * Abstract response data type
 * @author Roy, 2011
 *
 */
public abstract class AbstractResponseData implements IResponseData {

	protected static final String BASE_PATH = "/zendServerAPIResponse/responseData";
	
	/**
	 * Type of the response data
	 */
	private final ResponseType type;
	
	/**
	 * The prefix of the data represented in the XML document
	 */
	private final String prefix;
	private final String suffix;
	private final int occurrence;
	
	public AbstractResponseData(ResponseType type, String prefix,
			String suffix, int occurrence) {
		super();
		this.type = type;
		this.prefix = prefix;
		this.occurrence = occurrence;
		this.suffix = suffix;
	}

	public AbstractResponseData(ResponseType type, String prefix, String suffix) {
		this(type, prefix, suffix, 0);
	}

	public ResponseType getType() {
		return type;
	}
	
	public String getSuffix() {
		return suffix;
	}

	protected String getPrefix() {
		return prefix;
	}

	protected int getOccurrence() {
		return occurrence;
	}

}
