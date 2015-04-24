/*******************************************************************************
 * Copyright (c) Feb 3, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.webapi.core.connection.request;

import org.zend.webapi.core.WebApiException;

/**
 * Service initializer gives flexibility to users to customize their service
 * method upon generation
 * 
 * @author Roy, 2011
 * @see WebApiService
 */
public interface IRequestInitializer {

	public abstract void init(IRequest request) throws WebApiException;

}
