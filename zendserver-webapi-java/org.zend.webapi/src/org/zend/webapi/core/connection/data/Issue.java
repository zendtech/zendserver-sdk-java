/*******************************************************************************
 * Copyright (c) Feb 12, 2012 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.webapi.core.connection.data;

import org.zend.webapi.core.connection.data.values.IssueSeverity;
import org.zend.webapi.core.connection.data.values.IssueStatus;

/**
 * List of basic issue properties.
 * 
 * @author Wojciech Galanciak, 2012
 * @since 1.2
 */
public class Issue extends AbstractResponseData {

	private static final String ISSUE = "/issue";
	
	private int id;
	private String rule;
	private String lastOccurance;
	private IssueSeverity severity;
	private IssueStatus status;
	private GeneralDetails generalDetails;
	private RouteDetails routeDetails;

	protected Issue() {
		super(ResponseType.ISSUE, BASE_PATH + ISSUE, ISSUE);
	}

	protected Issue(String prefix, int occurrance) {
		super(ResponseType.ISSUE, prefix, ISSUE, occurrance);
	}

	public boolean accept(IResponseDataVisitor visitor) {
		boolean visit = visitor.preVisit(this);
		if (visit) {
			if (this.getGeneralDetails() != null) {
				this.getGeneralDetails().accept(visitor);
			}
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
	 * @return Issue's last time (DD-MMM-YYYY HH:MM) of manifestation
	 */
	public String getLastOccurance() {
		return lastOccurance;
	}

	/**
	 * @return Issue's severity
	 */
	public IssueSeverity getSeverity() {
		return severity;
	}

	/**
	 * @return Issue's current status
	 */
	public IssueStatus getStatus() {
		return status;
	}

	/**
	 * @return Route details for the issue and the request that created it
	 */
	public RouteDetails getRouteDetails() {
		return routeDetails;
	}

	/**
	 * @return general details
	 */
	public GeneralDetails getGeneralDetails() {
		return generalDetails;
	}

	protected void setId(int id) {
		this.id = id;
	}

	protected void setRule(String rule) {
		this.rule = rule;
	}

	protected void setLastOccurance(String lastOccurance) {
		this.lastOccurance = lastOccurance;
	}

	protected void setSeverity(IssueSeverity severity) {
		this.severity = severity;
	}

	protected void setStatus(IssueStatus status) {
		this.status = status;
	}

	protected void setGeneralDetails(GeneralDetails generalDetails) {
		this.generalDetails = generalDetails;
	}

	protected void setRouteDetails(RouteDetails routeDetails) {
		this.routeDetails = routeDetails;
	}

}
