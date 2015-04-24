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
 * A list of backtrace step elements.
 * 
 * @author Wojciech Galanciak, 2012
 * @since 1.2
 */
public class Backtrace extends AbstractResponseData {

	private static final String BACKTRACE = "/backtrace";
	
	private List<Step> steps;

	protected Backtrace() {
		super(ResponseType.BACKTRACE, BASE_PATH + BACKTRACE, BACKTRACE);
	}

	protected Backtrace(String prefix, int occurrance) {
		super(ResponseType.BACKTRACE, prefix, BACKTRACE, occurrance);
	}

	/**
	 * @return Steps
	 */
	public List<Step> getSteps() {
		return steps;
	}

	public boolean accept(IResponseDataVisitor visitor) {
		boolean visit = visitor.preVisit(this);
		if (visit) {
			if (getSteps() != null) {
				for (Step step : getSteps()) {
					step.accept(visitor);
				}
			}
			return visitor.visit(this);
		}
		return false;
	}

	protected void setSteps(List<Step> steps) {
		this.steps = steps;
	}

}
