/*******************************************************************************
 * Copyright (c) Feb 12, 2012 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.webapi.core.connection.data;



/**
 * Issue Route details hints provided by the monitor to indicate where or how
 * the issue was produced in a more modular and application-aware display.
 * 
 * @author Wojciech Galanciak, 2012
 * @since 1.2
 */
public class RouteDetail extends AbstractResponseData {

	private static final String ROUTE_DETAIL = "/routeDetail";

	private String key;
	private String value;

	protected RouteDetail() {
		super(ResponseType.ROUTE_DETAIL, AbstractResponseData.BASE_PATH
				+ ROUTE_DETAIL, ROUTE_DETAIL, 0);
	}

	protected RouteDetail(String prefix, int occurrence) {
		super(ResponseType.ROUTE_DETAIL, prefix, ROUTE_DETAIL, occurrence);
	}

	/**
	 * @return Route detail piece's key name
	 */
	public String getKey() {
		return key;
	}

	/**
	 * @return Route detail piece's value
	 */
	public String getValue() {
		return value;
	}

	public boolean accept(IResponseDataVisitor visitor) {
		if (visitor.preVisit(this)) {
			return visitor.visit(this);
		}
		return false;
	}

	protected void setKey(String key) {
		this.key = key;
	}

	protected void setValue(String value) {
		this.value = value;
	}

}
