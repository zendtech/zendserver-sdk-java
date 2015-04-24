/*******************************************************************************
 * Copyright (c) Feb 15, 2012 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.webapi.core.connection.data;

/**
 * General details of particular issue
 * 
 * @author Wojciech Galanciak, 2012
 * @since 1.2
 */
public class GeneralDetails extends AbstractResponseData {

	private static final String GENERAL_DETAILS = "/generalDetails";
	
	private String url;
	private String sourceFile;
	private long sourceLine;
	private String function;
	private String aggregationHint;
	private String errorString;
	private String errorType;

	protected GeneralDetails() {
		super(ResponseType.GENERAL_DETAILS, BASE_PATH + GENERAL_DETAILS,
				GENERAL_DETAILS);
	}

	protected GeneralDetails(String prefix, int occurrance) {
		super(ResponseType.GENERAL_DETAILS, prefix, GENERAL_DETAILS, occurrance);
	}

	public boolean accept(IResponseDataVisitor visitor) {
		if (visitor.preVisit(this)) {
			return visitor.visit(this);
		}
		return false;
	}

	/**
	 * @return Issue's creating URL string
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * @return Path to the file where the issue manifested
	 */
	public String getSourceFile() {
		return sourceFile;
	}

	/**
	 * @return Line number where the issue manifests within the sourceFile
	 */
	public long getSourceLine() {
		return sourceLine;
	}

	/**
	 * @return Name of the function that caused the issue to manifest
	 */
	public String getFunction() {
		return function;
	}

	/**
	 * @return A unique identifier that groups all events under this issue
	 */
	public String getAggregationHint() {
		return aggregationHint;
	}

	/**
	 * @return The error string generated for the event
	 */
	public String getErrorString() {
		return errorString;
	}

	/**
	 * @return PHP Error type created for the event
	 */
	public String getErrorType() {
		return errorType;
	}

	protected void setUrl(String url) {
		this.url = url;
	}

	protected void setSourceFile(String sourceFile) {
		this.sourceFile = sourceFile;
	}

	protected void setSourceLine(long sourceLine) {
		this.sourceLine = sourceLine;
	}

	protected void setFunction(String function) {
		this.function = function;
	}

	protected void setAggregationHint(String aggregationHint) {
		this.aggregationHint = aggregationHint;
	}

	protected void setErrorString(String errorString) {
		this.errorString = errorString;
	}

	protected void setErrorType(String errorType) {
		this.errorType = errorType;
	}

}
