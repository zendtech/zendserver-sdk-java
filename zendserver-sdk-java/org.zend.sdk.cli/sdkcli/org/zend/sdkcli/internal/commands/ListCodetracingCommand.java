/*******************************************************************************
 * Copyright (c) Feb 19, 2012 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.sdkcli.internal.commands;

import java.util.Arrays;
import java.util.List;

import org.zend.sdkcli.internal.options.Option;
import org.zend.webapi.core.connection.data.CodeTrace;

/**
 * Command to list code traces on specified application(s).
 * 
 * @author Wojciech Galanciak, 2012
 * 
 */
public class ListCodetracingCommand extends AbstractCodetracingCommand {

	private static final String APP_ID = "a";

	@Option(opt = APP_ID, required = false, description = "One or more application IDs", argName = "app id")
	public String[] getApplicationId() {
		return getValues(APP_ID);
	}

	@Override
	public boolean doExecute() {
		List<CodeTrace> traces = getCodeTracing().getTraces(true,
				getApplicationId());
		if (traces != null) {
			if (traces.size() == 0) {
				getLogger().info(
						"No code traces available for "
								+ Arrays.toString(getApplicationId()));
			}
			for (CodeTrace codeTrace : traces) {
				getLogger().info(codeTrace.getId() + ":");
				getLogger().info("\turl:          " + codeTrace.getUrl());
				getLogger().info(
						"\tapplication:  " + codeTrace.getApplicationId());
				getLogger().info("\tcreated by:   " + codeTrace.getCreatedBy());
				getLogger().info(
						"\tdate:         " + getDate(codeTrace.getDate()));
			}
		} else {
			getLogger().error(
					"Failed to list code traces for "
							+ Arrays.toString(getApplicationId()));
			return false;
		}
		return true;
	}

}
