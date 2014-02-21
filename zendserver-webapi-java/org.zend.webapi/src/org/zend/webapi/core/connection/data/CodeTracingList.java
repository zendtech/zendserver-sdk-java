/*******************************************************************************
 * Copyright (c) Feb 13, 2012 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.webapi.core.connection.data;

import java.util.List;

/**
 * A list of code traces.
 * 
 * @author Wojciech Galanciak, 2012
 * 
 */
public class CodeTracingList extends AbstractResponseData {

	private static final String CODE_TRACING_LIST = "/codeTracingList";
	
	private List<CodeTrace> traces;

	protected CodeTracingList() {
		super(ResponseType.CODE_TRACING_LIST, BASE_PATH + CODE_TRACING_LIST,
				CODE_TRACING_LIST);
	}

	protected CodeTracingList(String prefix, int occurrance) {
		super(ResponseType.CODE_TRACING_LIST, prefix, CODE_TRACING_LIST,
				occurrance);
	}

	/**
	 * @return code traces
	 */
	public List<CodeTrace> getTraces() {
		return traces;
	}

	public boolean accept(IResponseDataVisitor visitor) {
		boolean visit = visitor.preVisit(this);
		if (visit) {
			if (getTraces() != null) {
				for (CodeTrace trace : getTraces()) {
					trace.accept(visitor);
				}
			}
			return visitor.visit(this);
		}
		return false;
	}

	protected void setTraces(List<CodeTrace> traces) {
		this.traces = traces;
	}

}
