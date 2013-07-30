/*******************************************************************************
 * Copyright (c) Jan 31, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.webapi.internal.core.connection.request;

import java.util.List;

import org.restlet.data.Parameter;
import org.restlet.util.Series;

/**
 * Used to hold all request headers
 * @author Roy, 2011
 */
public class HeaderParameters extends Series<Parameter> {

	@Override
	public Parameter createEntry(String name, String value) {
		return Parameter.create(name, value);
	}

	@Override
	public Series<Parameter> createSeries(List<Parameter> delegate) {
		if (delegate == null) {
			return null;
		}
		return new HeaderParameters();
	}

}
