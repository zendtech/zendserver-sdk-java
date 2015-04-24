/*******************************************************************************
 * Copyright (c) May 18, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/

package org.zend.webapi.core.progress;

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
	private int totalWork;
	private Throwable throwable;

	public BasicStatus(StatusCode code, String title, String message) {
		super();
		this.code = code;
		this.title = title;
		this.message = message;
		this.totalWork = 1;
	}

	public BasicStatus(StatusCode code, String title, String message,
			Throwable throwable) {
		super();
		this.code = code;
		this.title = title;
		this.message = message;
		this.totalWork = 1;
		this.throwable = throwable;
	}

	public BasicStatus(StatusCode code, String title, String message,
			int totalWork) {
		super();
		this.code = code;
		this.title = title;
		this.message = message;
		this.totalWork = totalWork;
	}

	public StatusCode getCode() {
		return code;
	}

	public String getTitle() {
		return title;
	}

	public String getMessage() {
		return message;
	}

	public int getTotalWork() {
		return totalWork;
	}

	public Throwable getThrowable() {
		return throwable;
	}

}
