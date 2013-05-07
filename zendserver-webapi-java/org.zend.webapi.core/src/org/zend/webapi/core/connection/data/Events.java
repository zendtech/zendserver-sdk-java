/*******************************************************************************
 * Copyright (c) Feb 12, 2012 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.webapi.core.connection.data;

import java.util.List;

/**
 * A list of events.
 * 
 * @author Wojciech Galanciak, 2012
 * 
 */
public class Events extends AbstractResponseData {

	private static final String EVENTS = "/events";
	
	private List<Event> events;

	protected Events() {
		super(ResponseType.EVENTS, BASE_PATH + EVENTS, EVENTS);
	}

	protected Events(String prefix, int occurrance) {
		super(ResponseType.EVENTS, prefix, EVENTS, occurrance);
	}

	/**
	 * @return events list
	 */
	public List<Event> getEvents() {
		return events;
	}

	public boolean accept(IResponseDataVisitor visitor) {
		boolean visit = visitor.preVisit(this);
		if (visit) {
			if (getEvents() != null) {
				for (Event event : getEvents()) {
					event.accept(visitor);
				}
			}
			return visitor.visit(this);
		}
		return false;
	}

	protected void setEvents(List<Event> events) {
		this.events = events;
	}

}
