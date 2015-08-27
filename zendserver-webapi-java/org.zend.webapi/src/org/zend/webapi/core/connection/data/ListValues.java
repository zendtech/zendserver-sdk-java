/*******************************************************************************
 * Copyright (c) 2015 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.webapi.core.connection.data;

import java.util.List;

/**
 * A list of 0 or more list values.
 * 
 * @author Bartlomiej Laczkowski
 */
public class ListValues extends AbstractResponseData {

	private static final String LIST_VALUES = "/listValues"; //$NON-NLS-1$

	private List<ListValue> values;

	protected ListValues(String prefix, int occurrance) {
		super(ResponseType.LIST_VALUES, prefix, LIST_VALUES, occurrance);
	}

	protected ListValues() {
		this(AbstractResponseData.BASE_PATH + LIST_VALUES, 0);
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
			if (getValues() != null) {
				for (ListValue listValue : getValues()) {
					listValue.accept(visitor);
				}
			}
			return visitor.visit(this);
		}
		return false;
	}

	/**
	 * Returns list of values.
	 * 
	 * @return list of values
	 */
	public List<ListValue> getValues() {
		return values;
	}

	protected void setValues(List<ListValue> values) {
		this.values = values;
	}

}
