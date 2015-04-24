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
import org.zend.webapi.core.connection.data.ApplicationInfo;

/**
 * Redeploys package to specified target.
 * 
 * @author Wojciech Galanciak, 2011
 * 
 */
public class RedeployApplicationCommand extends ApplicationAwareCommand {

	private static final String APPID = "a";
	private static final String IGNORE_FAILURES = "i";
	private static final String SERVERS = "s";

	@Option(opt = APPID, required = true, description = "Application id to redeploy", argName = "app-id")
	public String getApplicationId() {
		return getValue(APPID);
	}

	@Option(opt = IGNORE_FAILURES, required = false, description = "Ignore failures")
	public boolean isIgnoreFailures() {
		return hasOption(IGNORE_FAILURES);
	}

	@Option(opt = SERVERS, required = false, description = "Server names", argName = "server-names")
	public String[] getServers() {
		return getValues(SERVERS);
	}

	@Override
	public boolean doExecute() {
		ApplicationInfo info = getApplication().redeploy(getTargetId(),
				getApplicationId(), getServers(), isIgnoreFailures());

		if (info != null) {
			getLogger()
					.info(MessageFormat
							.format("Application {0} (id {1}) was redeployed successfully on target {2}",
									info.getAppName(), info.getId(), getTargetId()));
			return true;
		}
		return false;
	}

}
