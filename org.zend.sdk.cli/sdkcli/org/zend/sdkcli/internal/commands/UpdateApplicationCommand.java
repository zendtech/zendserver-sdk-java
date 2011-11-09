/*******************************************************************************
 * Copyright (c) May 24, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.sdkcli.internal.commands;

import java.text.MessageFormat;

import org.zend.sdkcli.internal.options.Option;
import org.zend.sdkcli.monitor.StatusChangeListener;
import org.zend.webapi.core.connection.data.ApplicationInfo;

/**
 * Deploys package to specified target.
 * 
 * @author Wojciech Galanciak, 2011
 * 
 */
public class UpdateApplicationCommand extends ApplicationAwareCommand {

	private static final String PATH = "p";
	private static final String APPID = "a";
	private static final String PARAMS = "m";
	private static final String IGNORE_FAILURES = "f";

	@Option(opt = PATH, required = false, description = "The path to the project or application package", argName="path")
	public String getPath() {
		final String value = getValue(PATH);
		if (value == null) {
			return getCurrentDirectory();
		}
		return value;
	}

	@Option(opt = APPID, required = true, description = "The application id", argName="app-id")
	public String getApplicationId() {
		return getValue(APPID);
	}
	
	@Option(opt = PARAMS, required = false, description = "The path to parameters properties file", argName="parameters")
	public String getParams() {
		return getValue(PARAMS);
	}

	@Option(opt = IGNORE_FAILURES, required = false, description = "Ignore failures")
	public boolean isIgnoreFailures() {
		return hasOption(IGNORE_FAILURES);
	}

	@Override
	public boolean doExecute() {
		getApplication().addStatusChangeListener(new StatusChangeListener());
		ApplicationInfo info = getApplication().update(getPath(),
				getTargetId(),
				getApplicationId(), getParams(), isIgnoreFailures());
		
		if (info == null) {
			return false;
		}
		
		getLogger().info(
				MessageFormat.format(
						"Application {0} (id {1}) was updated to {2}",
						info.getAppName(), info.getId(), info.getBaseUrl()));
		
		return true;
	}

}
