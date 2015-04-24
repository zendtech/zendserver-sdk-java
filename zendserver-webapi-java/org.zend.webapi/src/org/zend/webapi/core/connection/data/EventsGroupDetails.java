/*******************************************************************************
 * Copyright (c) Feb 12, 2012 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.webapi.core.connection.data;


/**
 * Details about an issue's evensGroup include the actual event data.
 * 
 * @author Wojciech Galanciak, 2012
 * @since 1.2
 */
public class EventsGroupDetails extends AbstractResponseData {

	private static final String EVENTS_GROUP_DETAILS = "/eventsGroupDetails";
	
	private int issueId;
	private EventsGroup eventsGroup;
	private Event event;
	private String codeTracing;

	protected EventsGroupDetails() {
		super(ResponseType.EVENTS_GROUP_DETAILS, BASE_PATH
				+ EVENTS_GROUP_DETAILS, EVENTS_GROUP_DETAILS);
	}

	protected EventsGroupDetails(String prefix, int occurrance) {
		super(ResponseType.EVENTS_GROUP_DETAILS, prefix, EVENTS_GROUP_DETAILS,
				occurrance);
	}

	public boolean accept(IResponseDataVisitor visitor) {
		boolean visit = visitor.preVisit(this);
		if (visit) {
			if (this.getEventsGroup() != null) {
				this.getEventsGroup().accept(visitor);
			}
			if (this.getEvent() != null) {
				this.getEvent().accept(visitor);
			}
			return visitor.visit(this);
		}
		return false;
	}

	/**
	 * @return The group's Issue identifier
	 */
	public int getIssueId() {
		return issueId;
	}

	/**
	 * @return Basic details about the eventGroup
	 */
	public EventsGroup getEventsGroup() {
		return eventsGroup;
	}

	/**
	 * @return Event with common data for the events group
	 */
	public Event getEvent() {
		return event;
	}

	/**
	 * @return Associated code tracing identifier
	 */
	public String getCodeTracing() {
		return codeTracing;
	}

	protected void setIssueId(int issueId) {
		this.issueId = issueId;
	}

	protected void setEventsGroup(EventsGroup eventsGroup) {
		this.eventsGroup = eventsGroup;
	}

	protected void setEvent(Event event) {
		this.event = event;
	}

	protected void setCodeTracing(String codeTracing) {
		this.codeTracing = codeTracing;
	}

}
