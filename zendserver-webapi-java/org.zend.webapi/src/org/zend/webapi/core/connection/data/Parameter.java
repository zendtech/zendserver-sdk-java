/*******************************************************************************
 * Copyright (c) Feb 12, 2012 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.webapi.core.connection.data;



/**
 * Name and value pair for parameters exposed to the script's environment.
 * 
 * @author Wojciech Galanciak, 2012
 * @since 1.2
 */
public class Parameter extends AbstractResponseData {

	private static final String PARAMETER = "/parameter";
	
	private String name;
	private String value;

	protected Parameter() {
		super(ResponseType.PARAMETER, AbstractResponseData.BASE_PATH
				+ PARAMETER, PARAMETER, 0);
	}

	protected Parameter(String prefix, int occurrence) {
		super(ResponseType.PARAMETER, prefix, PARAMETER, occurrence);
	}

	/**
	 * @return Parameter name or identifier
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return String value of the parameter
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

	protected void setName(String name) {
		this.name = name;
	}

	protected void setValue(String value) {
		this.value = value;
	}

}
