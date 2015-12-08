/*******************************************************************************
 * Copyright (c) 2013 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.php.zendserver.deployment.debug.core.jobs;

import org.eclipse.core.runtime.jobs.Job;
import org.zend.php.zendserver.deployment.core.debugger.LibraryDeployData;
import org.zend.webapi.core.connection.response.ResponseCode;

/**
 * Abstract representation of library deployment job.
 * 
 * @author Wojciech Galanciak, 2013
 * 
 */
public abstract class AbstractLibraryJob extends Job {

	protected ResponseCode responseCode;
	protected LibraryDeployData data;

	public AbstractLibraryJob(String name, LibraryDeployData data) {
		super(name);
		this.data = data;
	}

	public ResponseCode getResponseCode() {
		return responseCode;
	}

	public LibraryDeployData getData() {
		return data;
	}

}
