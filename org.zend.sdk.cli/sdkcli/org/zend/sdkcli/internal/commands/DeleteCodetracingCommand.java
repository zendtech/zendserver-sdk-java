/*******************************************************************************
 * Copyright (c) Feb 19, 2012 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.sdkcli.internal.commands;

import org.zend.sdkcli.internal.options.Option;
import org.zend.webapi.core.connection.data.CodeTrace;

/**
 * Command to delete code trace with specified id.
 * 
 * @author Wojciech Galanciak, 2012
 * 
 */
public class DeleteCodetracingCommand extends AbstractCodetracingCommand {

	private static final String TRACE_ID = "i";

	@Option(opt = TRACE_ID, required = true, description = "Trace ID", argName = "trace id")
	public String getTraceId() {
		return getValue(TRACE_ID);
	}

	@Override
	public boolean doExecute() {
		String traceId = getTraceId();
		CodeTrace result = getCodeTracing().deleteTrace(traceId);
		if (result != null) {
			getLogger().info("Code trace deleted successfully:");
			getLogger().info("Code Trace ID:	" + result.getId());
			getLogger().info("Application ID:	" + result.getApplicationId());
			getLogger().info("Created by:	" + result.getCreatedBy());
			getLogger().info("Date:	" + result.getDate());
		} else {
			getLogger()
					.error("Failed to delete code trace + '" + traceId + "'");
			return false;
		}
		return true;
	}

}
