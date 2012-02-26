/*******************************************************************************
 * Copyright (c) Feb 20, 2012 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.sdkcli.internal.commands;

import java.io.File;
import java.util.List;

import org.zend.sdkcli.internal.options.Option;
import org.zend.sdklib.monitor.IZendIssue;

/**
 * Command to export issue file(s).
 * 
 * @author Wojciech Galanciak, 2012
 * 
 */
public class ExportIssueCommand extends AbstractMonitorCommand {

	private static final String ID = "i";
	private static final String DESTINATION = "d";

	@Option(opt = ID, required = true, description = "Issue id", argName = "Issue id")
	public String getId() {
		return getValue(ID);
	}

	@Option(opt = DESTINATION, required = false, description = "Issue file destination", argName = "path")
	public File getDestination() {
		final String value = getValue(DESTINATION);
		return new File(value == null ? getCurrentDirectory() : value);
	}

	@Override
	public boolean doExecute() {
		IZendIssue zendIssue = getMonitor().get(Integer.valueOf(getId()));
		if (zendIssue != null) {
			List<File> files = zendIssue.export(getDestination());
			if (files != null && files.size() > 0) {
				getLogger().info("Issue exported successfully:");
				for (File file : files) {
					getLogger().info(file.getAbsolutePath());
				}
			}
			return true;
		} else {
			getLogger().info("There is no issue with id " + getId());
			return true;
		}
	}

}
