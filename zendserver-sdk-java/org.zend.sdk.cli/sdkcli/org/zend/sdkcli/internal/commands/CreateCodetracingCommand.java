/*******************************************************************************
 * Copyright (c) Feb 19, 2012 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.sdkcli.internal.commands;

import java.net.MalformedURLException;
import java.net.URL;

import org.zend.sdkcli.internal.options.Option;
import org.zend.webapi.core.connection.data.CodeTrace;

/**
 * Command to create code trace for specified URL.
 * 
 * @author Wojciech Galanciak, 2012
 * 
 */
public class CreateCodetracingCommand extends AbstractCodetracingCommand {

	private static final String URL = "u";

	@Option(opt = URL, required = true, description = "URL to trace", argName = "url")
	public String getURL() {
		return getValue(URL);
	}

	@Override
	public boolean doExecute() {
		URL url = null;
		try {
			url = new URL(getURL());
		} catch (MalformedURLException e) {
			getLogger().error("Invalid url: " + getURL());
			return false;
		}
		CodeTrace result = getCodeTracing().createTrace(url);
		if (result != null) {
			getLogger().info("Code trace created successfully:");
			getLogger().info("id:           " + result.getId());
			getLogger().info("application:  " + result.getApplicationId());
			getLogger().info("created by:   " + result.getCreatedBy());
			getLogger().info("date:         " + getDate(result.getDate()));
		} else {
			getLogger().error(
					"Failed to create code trace for " + url.toString());
			return false;
		}
		return true;
	}

}
