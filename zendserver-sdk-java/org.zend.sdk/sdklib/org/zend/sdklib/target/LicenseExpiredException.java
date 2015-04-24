/*******************************************************************************
 * Copyright (c) 2013 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.sdklib.target;

import java.util.Date;


/**
 * @author Wojciech Galanciak, 2013
 *
 */
public class LicenseExpiredException extends Exception {

	private Date validUntil;

	public LicenseExpiredException(Date validUntil) {
		this.validUntil = validUntil;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 3603180281609950446L;
	
	@Override
	public String getMessage() {
		return "Selected target has expired license.";
	}

	public Date getValidUntil() {
		return validUntil;
	}
	
}
