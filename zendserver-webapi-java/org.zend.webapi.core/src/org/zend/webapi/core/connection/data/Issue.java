/*******************************************************************************
 * Copyright (c) Feb 12, 2012 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.webapi.core.connection.data;

/**
 * List of basic issue properties.
 * 
 * @author Wojciech Galanciak, 2012
 * @since 1.2
 */
public class Issue extends AbstractResponseData {

	private int id;
	private String rule;
	private long lastOccurance;
	private String severity;
	private String status;
	private String url;
	private String sourceFile;
	private int sourceLine;
	private String function;
	private String aggregationHint;
	private String errorString;
	private String errorType;
	private RouteDetails routeDetails;

	protected Issue() {
		super(ResponseType.ISSUE, BASE_PATH + "/issue");
	}

	protected Issue(String prefix, int occurrance) {
		super(ResponseType.ISSUE, prefix, occurrance);
	}

	public boolean accept(IResponseDataVisitor visitor) {
		boolean visit = visitor.preVisit(this);
		if (visit) {
			if (this.getRouteDetails() != null) {
				this.getRouteDetails().accept(visitor);
			}
			return visitor.visit(this);
		}
		return false;
	}

	/**
	 * @return Issue identifier
	 */
	public int getId() {
		return id;
	}

	/**
	 * @return Issue's rule name
	 */
	public String getRule() {
		return rule;
	}

	/**
	 * @return Issue's last time of manifestation
	 */
	public long getLastOccurance() {
		return lastOccurance;
	}

	/**
	 * @return Issue's severity (Warning|Error)
	 */
	public String getSeverity() {
		return severity;
	}

	/**
	 * @return Issue's current status
	 */
	public String getStatus() {
		return status;
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
	public int getSourceLine() {
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

	/**
	 * @return Route details for the issue and the request that created it
	 */
	public RouteDetails getRouteDetails() {
		return routeDetails;
	}

	protected void setId(int id) {
		this.id = id;
	}

	protected void setRule(String rule) {
		this.rule = rule;
	}

	protected void setLastOccurance(long lastOccurance) {
		this.lastOccurance = lastOccurance;
	}

	protected void setSeverity(String severity) {
		this.severity = severity;
	}

	protected void setStatus(String status) {
		this.status = status;
	}

	protected void setUrl(String url) {
		this.url = url;
	}

	protected void setSourceFile(String sourceFile) {
		this.sourceFile = sourceFile;
	}

	protected void setSourceLine(int sourceLine) {
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

	protected void setRouteDetails(RouteDetails routeDetails) {
		this.routeDetails = routeDetails;
	}

}
