/*******************************************************************************
 * Copyright (c) Feb 19, 2012 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.sdkcli.internal.commands;

import org.zend.sdkcli.internal.options.Option;
import org.zend.webapi.core.connection.data.CodeTracingStatus;
import org.zend.webapi.core.connection.data.CodeTracingStatus.Status;

/**
 * Command to disable code tracing on specified target.
 * 
 * @author Wojciech Galanciak, 2012
 * 
 */
public class DisableCodetracingCommand extends AbstractCodetracingCommand {

	private static final String RESTART = "r";

	@Option(opt = RESTART, required = false, description = "Restart php during operation", argName = "restart php")
	public boolean isRestartPhp() {
		return hasOption(RESTART);
	}

	@Override
	public boolean doExecute() {
		CodeTracingStatus result = getCodeTracing().enable(isRestartPhp());
		if (result != null) {
			if (result.getComponentStatus() == Status.ACTIVE) {
				getLogger().info("Code tracing enabled successfully.");
				return true;
			}
		}
		getLogger()
				.error("Failed to enable code tracing on target '"
						+ getTarget() + "'");
		return false;
	}

}
