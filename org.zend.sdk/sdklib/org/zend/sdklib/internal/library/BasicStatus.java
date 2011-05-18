/*******************************************************************************
 * Copyright (c) May 18, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/

package org.zend.sdklib.internal.library;

import org.zend.sdklib.library.IStatus;
import org.zend.sdklib.library.StatusCode;

/**
 * Basic implementation of {@link IStatus}.
 * 
 * @author Wojciech Galanciak, 2011
 * 
 */
public class BasicStatus implements IStatus {

	private StatusCode code;
	private String title;
	private String message;

	public BasicStatus(StatusCode code, String title, String message) {
		super();
		this.code = code;
		this.title = title;
		this.message = message;
	}

	@Override
	public StatusCode getCode() {
		return code;
	}

	@Override
	public String getTitle() {
		return title;
	}

	@Override
	public String getMessage() {
		return message;
	}

}
