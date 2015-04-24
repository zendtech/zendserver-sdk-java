/*******************************************************************************
 * Copyright (c) Sep 30, 2012 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.webapi.core.connection.data;

/**
 * Result of debug mode action.
 * 
 * @author Wojciech Galanciak, 2012
 * @since 1.3
 */
public class DebugMode extends AbstractResponseData {

	private static final String DEBUG_MODE = "/debugMode";
	
	private int result;

	protected DebugMode() {
		super(ResponseType.ISSUE, BASE_PATH + DEBUG_MODE, DEBUG_MODE);
	}

	protected DebugMode(String prefix, int occurrance) {
		super(ResponseType.DEBUG_MODE, prefix, DEBUG_MODE, occurrance);
	}

	public boolean accept(IResponseDataVisitor visitor) {
		boolean visit = visitor.preVisit(this);
		if (visit) {
			return visitor.visit(this);
		}
		return false;
	}

	/**
	 * @return action result
	 */
	public int getResult() {
		return result;
	}

	protected void setResult(int result) {
		this.result = result;
	}

}
