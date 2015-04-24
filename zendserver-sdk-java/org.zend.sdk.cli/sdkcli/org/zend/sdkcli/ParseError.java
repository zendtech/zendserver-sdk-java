/*******************************************************************************
 * Copyright (c) May 16, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.sdkcli;

import org.apache.commons.cli.ParseException;

public class ParseError extends Exception {

	private Exception e;

	public ParseError(ParseException e) {
		this.e = e;
	}

	@Override
	public String getMessage() {
		return e.getMessage();
	}

	@Override
	public StackTraceElement[] getStackTrace() {
		return e.getStackTrace();
	}

	@Override
	public String getLocalizedMessage() {
		return e.getLocalizedMessage();
	}

	@Override
	public Throwable getCause() {
		return e.getCause();
	}

	private static final long serialVersionUID = -2792769465688391550L;

}
