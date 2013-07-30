/*******************************************************************************
 * Copyright (c) Feb 12, 2012 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.webapi.core.connection.data;

/**
 * List of event properties with metadata and backtrace information.
 * 
 * @author Wojciech Galanciak, 2012
 * @since 1.2
 */
public class Event extends AbstractResponseData {

	private static final String EVENT = "/event";
	
	private String eventsGroupId;
	private String eventType;
	private String description;
	private SuperGlobals superGlobals;
	private String severity;
	private Backtrace backtrace;
	private String codeTracing;

	protected Event() {
		super(ResponseType.EVENT, BASE_PATH + EVENT, EVENT);
	}

	protected Event(String prefix, int occurrance) {
		super(ResponseType.EVENT, prefix, EVENT, occurrance);
	}

	public boolean accept(IResponseDataVisitor visitor) {
		boolean visit = visitor.preVisit(this);
		if (visit) {
			if (this.getSuperGlobals() != null) {
				this.getSuperGlobals().accept(visitor);
			}
			if (this.getBacktrace() != null) {
				this.getBacktrace().accept(visitor);
			}
			return visitor.visit(this);
		}
		return false;
	}

	/**
	 * @return URL for debugging the event
	 */
	public String getEventsGroupId() {
		return eventsGroupId;
	}

	/**
	 * @return Issue type name
	 */
	public String getEventType() {
		return eventType;
	}

	/**
	 * @return Free form text field with details about the Issue
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @return Super global arrays and their values: get, post, cookie, session,
	 *         server
	 */
	public SuperGlobals getSuperGlobals() {
		return superGlobals;
	}

	/**
	 * @return Severity indicator for the event: Info, Warning, Critical
	 */
	public String getSeverity() {
		return severity;
	}

	/**
	 * @return A list of backtrace step elements
	 */
	public Backtrace getBacktrace() {
		return backtrace;
	}
	
	/**
	 * @return Associated code tracing identifier
	 */
	public String getCodeTracing() {
		return codeTracing;
	}

	protected void setEventsGroupId(String eventsGroupId) {
		this.eventsGroupId = eventsGroupId;
	}

	protected void setEventType(String eventType) {
		this.eventType = eventType;
	}

	protected void setDescription(String description) {
		this.description = description;
	}

	protected void setSuperGlobals(SuperGlobals superGlobals) {
		this.superGlobals = superGlobals;
	}

	protected void setSeverity(String severity) {
		this.severity = severity;
	}

	protected void setBacktrace(Backtrace backtrace) {
		this.backtrace = backtrace;
	}
	
	protected void setCodeTracing(String codeTracing) {
		this.codeTracing = codeTracing;
	}

}
