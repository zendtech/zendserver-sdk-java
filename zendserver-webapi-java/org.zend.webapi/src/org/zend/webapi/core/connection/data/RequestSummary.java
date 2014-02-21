/*******************************************************************************
 * Copyright (c) Feb 12, 2012 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.webapi.core.connection.data;


/**
 * A list of 0 or more events.
 * 
 * @author Wojciech Galanciak, 2012
 * @since 1.2
 */
public class RequestSummary extends AbstractResponseData {

	private static final String REQUEST_SUMMARY = "/requestSummary";
	
	private int eventsCount;
	private Events events;
	private String codeTracing;

	protected RequestSummary() {
		super(ResponseType.REQUEST_SUMMARY, BASE_PATH + REQUEST_SUMMARY,
				REQUEST_SUMMARY);
	}

	protected RequestSummary(String prefix, int occurrance) {
		super(ResponseType.REQUEST_SUMMARY, prefix, REQUEST_SUMMARY, occurrance);
	}

	public boolean accept(IResponseDataVisitor visitor) {
		boolean visit = visitor.preVisit(this);
		if (visit) {
			if (this.getEvents() != null) {
				this.getEvents().accept(visitor);
			}
			return visitor.visit(this);
		}
		return false;
	}

	/**
	 * @return Number of events in the events element
	 */
	public int getEventsCount() {
		return eventsCount;
	}

	/**
	 * @return List of event elements
	 */
	public Events getEvents() {
		return events;
	}

	/**
	 * @return Trace-file identifier
	 */
	public String getCodeTracing() {
		return codeTracing;
	}

	protected void setEventsCount(int eventsCount) {
		this.eventsCount = eventsCount;
	}

	protected void setEvents(Events events) {
		this.events = events;
	}

	protected void setCodeTracing(String codeTracing) {
		this.codeTracing = codeTracing;
	}

}
