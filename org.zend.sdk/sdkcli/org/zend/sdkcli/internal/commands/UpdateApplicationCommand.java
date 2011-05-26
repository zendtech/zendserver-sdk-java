/*******************************************************************************
 * Copyright (c) May 24, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.sdkcli.internal.commands;

import org.zend.sdkcli.internal.options.Option;
import org.zend.webapi.core.connection.data.ApplicationInfo;

/**
 * Deploys package to specified target.
 * 
 * @author Wojciech Galanciak, 2011
 * 
 */
public class UpdateApplicationCommand extends ApplicationAwareCommand {

	private static final String PATH = "p";
	private static final String TARGET = "t";
	private static final String APPID = "id";
	private static final String PARAMS = "pa";
	private static final String IGNORE_FAILURES = "f";

	@Option(opt = PATH, required = true, description = "The path to the project or application package")
	public String getPath() {
		return getValue(PATH);
	}

	@Option(opt = TARGET, required = true, description = "The target id")
	public String getTargetId() {
		return getValue(TARGET);
	}

	@Option(opt = APPID, required = true, description = "The application id")
	public String getApplicationId() {
		return getValue(APPID);
	}
	
	@Option(opt = PARAMS, required = false, description = "The path to parameters properties file")
	public String getParams() {
		return getValue(PARAMS);
	}

	@Option(opt = IGNORE_FAILURES, required = false, description = "Ignore failures")
	public boolean isIgnoreFailures() {
		return hasOption(IGNORE_FAILURES);
	}

	@Override
	public boolean doExecute() {
		
		ApplicationInfo info = getApplication().update(getPath(), getTargetId(), getApplicationId(), getParams(), isIgnoreFailures());
		
		if (info == null) {
			return false;
		}
		return true;
	}

}
