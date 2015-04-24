/*******************************************************************************
 * Copyright (c) Feb 12, 2012 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.webapi.core.connection.data;

/**
 * Detailed view of a single issue.
 * 
 * @author Wojciech Galanciak, 2012
 * @since 1.2
 */
public class IssueDetails extends AbstractResponseData {

	private static final String ISSUE_DETAILS = "/issueDetails";
	
	private Issue issue;
	private EventsGroups eventsGroups;

	protected IssueDetails() {
		super(ResponseType.ISSUE_DETAILS, AbstractResponseData.BASE_PATH
				+ ISSUE_DETAILS, ISSUE_DETAILS, 0);
	}

	protected IssueDetails(String prefix, int occurrence) {
		super(ResponseType.ISSUE_DETAILS, prefix, ISSUE_DETAILS, occurrence);
	}

	public boolean accept(IResponseDataVisitor visitor) {
		if (visitor.preVisit(this)) {
			if (getEventsGroups() != null) {
				this.getEventsGroups().accept(visitor);
			}
			if (getIssue() != null) {
				this.getIssue().accept(visitor);
			}
			return visitor.visit(this);
		}
		return false;
	}

	/**
	 * @return Issue
	 */
	public Issue getIssue() {
		return issue;
	}

	/**
	 * @return Details about event groups in the current issue
	 */
	public EventsGroups getEventsGroups() {
		return eventsGroups;
	}

	protected void setIssue(Issue issue) {
		this.issue = issue;
	}

	protected void setEventsGroups(EventsGroups eventsGroups) {
		this.eventsGroups = eventsGroups;
	}

}
