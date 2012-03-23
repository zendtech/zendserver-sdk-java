/*******************************************************************************
 * Copyright (c) Feb 20, 2012 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.sdkcli.internal.commands;

import java.util.List;

import org.zend.sdkcli.internal.options.Option;
import org.zend.sdklib.monitor.IZendIssue;
import org.zend.sdklib.monitor.ZendMonitor;
import org.zend.webapi.core.connection.data.GeneralDetails;
import org.zend.webapi.core.connection.data.Issue;
import org.zend.webapi.core.connection.data.RouteDetail;
import org.zend.webapi.core.connection.data.RouteDetails;

/**
 * Command to list issues on specified target.
 * 
 * @author Wojciech Galanciak, 2012
 * 
 */
public class ListIssuesCommand extends AbstractMonitorCommand {

	private static final String FILTER = "f";

	@Option(opt = FILTER, required = false, description = "Issue predefinied filter (possible values: all, open, critical, performance)", argName = "Filter name")
	public String getFilter() {
		return getValue(FILTER);
	}

	@Override
	public boolean doExecute() {
		String filter = getFilter();
		List<IZendIssue> result = null;
		ZendMonitor monitor = getMonitor();
		if ("open".equals(filter)) {
			result = monitor.getOpenIssues();
		} else if ("critical".equals(filter)) {
			result = monitor.getCriticalErrors();
		} else if ("performance".equals(filter)) {
			result = monitor.getPerformanceIssues();
		} else {
			result = monitor.getAllIssues();
		}
		if (result != null) {
			for (IZendIssue zendIssue : result) {
				Issue issue = zendIssue.getIssue();
				getLogger().info("id: " + issue.getId());
				getLogger().info(
						"\tlast occurance: " + issue.getLastOccurance());
				getLogger().info("\trule:           " + issue.getRule());
				getLogger().info("\tseverity:       " + issue.getSeverity());
				getLogger().info(
						"\tstatus:         " + issue.getStatus().getName());
				GeneralDetails generalDetails = issue.getGeneralDetails();
				if (generalDetails != null) {
					getLogger().info("\tgeneral details:");
					getLogger().info(
							"\t\turl:        " + generalDetails.getUrl());
					String errorType = generalDetails.getErrorType();
					if (errorType != null && errorType.length() > 0) {
						getLogger().info(
								"\t\terror:      "
										+ generalDetails.getErrorType() + ": "
										+ generalDetails.getErrorString());
					}
					getLogger().info(
							"\t\tfunction:   " + generalDetails.getFunction()
									+ "(" + generalDetails.getSourceFile()
									+ ":" + generalDetails.getSourceLine()
									+ ")");
				}
				RouteDetails routeDetails = issue.getRouteDetails();
				if (routeDetails != null) {
					List<RouteDetail> details = routeDetails.getDetails();
					if (details != null) {
						getLogger().info("\troute details:");
						for (RouteDetail routeDetail : details) {
							getLogger().info(
									"\t\t" + routeDetail.getKey() + " = "
											+ routeDetail.getValue());
						}
					}
				}
			}
			return true;
		}

		getLogger().error(
				"Failed to list issues on target '" + getTarget() + "'");
		return false;
	}

}
