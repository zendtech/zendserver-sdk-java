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
 * A list of events groups.
 * 
 * @author Wojciech Galanciak, 2012
 * 
 */
public class EventsGroups extends AbstractResponseData {

	private static final String EVENTS_GROUPS = "/eventsGroups";
	
	private List<EventsGroup> groups;

	protected EventsGroups() {
		super(ResponseType.EVENTS_GROUPS, BASE_PATH + EVENTS_GROUPS,
				EVENTS_GROUPS);
	}

	protected EventsGroups(String prefix, int occurrance) {
		super(ResponseType.EVENTS_GROUPS, prefix, EVENTS_GROUPS, occurrance);
	}

	/**
	 * @return events groups
	 */
	public List<EventsGroup> getGroups() {
		return groups;
	}

	public boolean accept(IResponseDataVisitor visitor) {
		boolean visit = visitor.preVisit(this);
		if (visit) {
			if (getGroups() != null) {
				for (EventsGroup group : getGroups()) {
					group.accept(visitor);
				}
			}
			return visitor.visit(this);
		}
		return false;
	}

	protected void setGroups(List<EventsGroup> groups) {
		this.groups = groups;
	}

}
