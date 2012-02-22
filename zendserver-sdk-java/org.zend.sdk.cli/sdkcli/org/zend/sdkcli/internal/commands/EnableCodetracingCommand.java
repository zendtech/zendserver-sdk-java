/*******************************************************************************
 * Copyright (c) Feb 19, 2012 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.sdkcli.internal.commands;

import org.zend.sdkcli.internal.options.Option;
import org.zend.sdklib.application.ZendCodeTracing;
import org.zend.webapi.core.connection.data.CodeTracingStatus;

/**
 * Command to enable code tracing on specified target.
 * 
 * @author Wojciech Galanciak, 2012
 * 
 */
public class EnableCodetracingCommand extends AbstractCodetracingCommand {

	private static final String RESTART = "r";

	@Option(opt = RESTART, required = false, description = "Restart php during operation", argName = "restart php")
	public boolean isRestartPhp() {
		return hasOption(RESTART);
	}

	@Override
	public boolean doExecute() {
		ZendCodeTracing codeTracing = getCodeTracing();
		if (!codeTracing.isEnabled()) {
			CodeTracingStatus result = getCodeTracing().enable(isRestartPhp());
			if (result != null) {
				getLogger().info("Developer mode enabled successfully.");
				return true;
			}
		} else {
			getLogger().info("Developer mode is already enabled.");
			return true;
		}
		getLogger().error(
				"Failed to enable developer mode on target '" + getTarget()
						+ "'");
		return false;
	}

}
