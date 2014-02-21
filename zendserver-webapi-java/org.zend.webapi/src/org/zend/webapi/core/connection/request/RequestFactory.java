/*******************************************************************************
 * Copyright (c) Jan 30, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.webapi.core.connection.request;

import java.lang.reflect.Constructor;
import java.util.Date;

import org.zend.webapi.core.connection.data.values.ServerType;
import org.zend.webapi.core.connection.data.values.WebApiVersion;
import org.zend.webapi.core.service.WebApiMethodType;

/**
 * Creates the request method
 * 
 * @author Roy, 2011
 * 
 */
public class RequestFactory {

	public static final IRequest createRequest(WebApiMethodType type,
			WebApiVersion version, Date date, String keyName, String userAgent,
			String host, String secretKey, ServerType serverType) {

		final Class<? extends IRequest> requestClass = type.getRequestClass();
		Constructor<? extends IRequest> constructor = null;
		try {
			constructor = requestClass.getConstructor(WebApiVersion.class,
					Date.class, String.class, String.class, String.class,
					String.class, ServerType.class);
			return constructor.newInstance(version, date, keyName, userAgent,
					host, secretKey, serverType);
		} catch (Exception e) {
			throw new IllegalStateException("Couldn't instantiate class "
					+ requestClass.toString(), e);
		}
	}
}
