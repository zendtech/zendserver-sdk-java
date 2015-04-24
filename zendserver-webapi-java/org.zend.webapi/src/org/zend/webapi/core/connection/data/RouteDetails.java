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
public class RouteDetails extends AbstractResponseData {

	private static final String ROUTE_DETAILS = "/routeDetails";
	
	private List<RouteDetail> details;

	protected RouteDetails() {
		super(ResponseType.ROUTE_DETAILS, BASE_PATH + ROUTE_DETAILS,
				ROUTE_DETAILS);
	}

	protected RouteDetails(String prefix, int occurrance) {
		super(ResponseType.ROUTE_DETAILS, prefix, ROUTE_DETAILS, occurrance);
	}

	/**
	 * @return list of route details
	 */
	public List<RouteDetail> getDetails() {
		return details;
	}

	public boolean accept(IResponseDataVisitor visitor) {
		boolean visit = visitor.preVisit(this);
		if (visit) {
			if (getDetails() != null) {
				for (RouteDetail detail : getDetails()) {
					detail.accept(visitor);
				}
			}
			return visitor.visit(this);
		}
		return false;
	}

	protected void setDetails(List<RouteDetail> details) {
		this.details = details;
	}

}
