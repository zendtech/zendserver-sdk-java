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
 * A list of parameters.
 * 
 * @author Wojciech Galanciak, 2012
 * 
 */
public class ParameterList extends AbstractResponseData {

	private List<Parameter> parameters;

	protected ParameterList(String name) {
		super(ResponseType.PARAMETER_LIST, BASE_PATH + "/" + name, "/" + name);
	}

	protected ParameterList(String prefix, int occurrance) {
		super(ResponseType.PARAMETER_LIST, prefix, prefix.substring(prefix
				.lastIndexOf('/')), occurrance);
	}

	/**
	 * @return Parameters
	 */
	public List<Parameter> getParameters() {
		return parameters;
	}

	public boolean accept(IResponseDataVisitor visitor) {
		boolean visit = visitor.preVisit(this);
		if (visit) {
			if (getParameters() != null) {
				for (Parameter parameter : getParameters()) {
					parameter.accept(visitor);
				}
			}
			return visitor.visit(this);
		}
		return false;
	}

	protected void setParameters(List<Parameter> parameters) {
		this.parameters = parameters;
	}

}
