/*******************************************************************************
 * Copyright (c) Feb 19, 2012 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.sdkcli.internal.commands;

import java.io.File;

import org.zend.sdkcli.internal.options.Option;

/**
 * Command to download code trace with specified id.
 * 
 * @author Wojciech Galanciak, 2012
 * 
 */
public class ExportCodetraceCommand extends AbstractCodetracingCommand {

	private static final String TRACE_ID = "i";
	private static final String DESTINATION = "d";

	@Option(opt = TRACE_ID, required = true, description = "Id of trace which should be downloaded", argName = "trace id")
	public String getTraceId() {
		return getValue(TRACE_ID);
	}

	@Option(opt = DESTINATION, required = false, description = "Trace file destination", argName = "path")
	public File getDestination() {
		final String value = getValue(DESTINATION);
		return new File(value == null ? getCurrentDirectory() : value);
	}

	@Override
	public boolean doExecute() {
		File traceFile = getCodeTracing().get(getTraceId(), getDestination());
		if (traceFile != null) {
			getLogger().info("Code trace file downloaded successfuly:");
			getLogger().info(traceFile.getAbsolutePath());
		} else {
			getLogger().error("Failed to download code trace " + getTraceId());
			return false;
		}
		return true;
	}

}
