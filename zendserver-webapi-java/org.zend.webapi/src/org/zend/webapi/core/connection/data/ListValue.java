/*******************************************************************************
 * Copyright (c) 2015 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.webapi.core.connection.data;

/**
 * List value info.
 * 
 * @author Bartlomiej Laczkowski
 */
public class ListValue extends AbstractResponseData {

	private static final String LIST_VALUE = "/listValue"; //$NON-NLS-1$

	private String name;
	private String value;

	protected ListValue(String prefix, int occurrance) {
		super(ResponseType.LIST_VALUE, prefix, LIST_VALUE, occurrance);
	}

	protected ListValue() {
		this(AbstractResponseData.BASE_PATH + LIST_VALUE, 0);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.zend.webapi.core.connection.data.IResponseData#accept(org.zend.webapi
	 * .core.connection.data.IResponseDataVisitor)
	 */
	@Override
	public boolean accept(IResponseDataVisitor visitor) {
		if (visitor.preVisit(this)) {
			return visitor.visit(this);
		}
		return false;
	}

	/**
	 * Returns list value name
	 * 
	 * @return list value name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns list value value
	 * 
	 * @return list value value
	 */
	public String getValue() {
		return value;
	}

	protected void setName(String name) {
		this.name = name;
	}

	protected void setValue(String value) {
		this.value = value;
	}

}
