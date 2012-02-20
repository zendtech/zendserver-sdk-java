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
public class GetIssueCommand extends AbstractMonitorCommand {

	private static final String ID = "i";

	@Option(opt = ID, required = true, description = "Issue id", argName = "Issue id")
	public String getId() {
		return getValue(ID);
	}

	@Override
	public boolean doExecute() {
		IZendIssue zendIssue = getMonitor().get(Integer.valueOf(getId()));
		if (zendIssue != null) {
			Issue issue = zendIssue.getIssue();
			getLogger().info("last occurance: " + issue.getLastOccurance());
			getLogger().info("rule:           " + issue.getRule());
			getLogger().info("severity:       " + issue.getSeverity());
			getLogger().info("status:         " + issue.getStatus().getName());
			GeneralDetails generalDetails = issue.getGeneralDetails();
			if (generalDetails != null) {
				getLogger().info("general details:");
				getLogger().info("\turl:        " + generalDetails.getUrl());
				String errorType = generalDetails.getErrorType();
				if (errorType != null && errorType.length() > 0) {
					getLogger().info(
							"\terror:      " + generalDetails.getErrorType()
									+ ": " + generalDetails.getErrorString());
				}
				getLogger().info(
						"\tfunction:   " + generalDetails.getFunction() + "("
								+ generalDetails.getSourceFile() + ":"
								+ generalDetails.getSourceLine() + ")");
			}
			RouteDetails routeDetails = issue.getRouteDetails();
			if (routeDetails != null) {
				List<RouteDetail> details = routeDetails.getDetails();
				if (details != null) {
					getLogger().info("route details:");
					for (RouteDetail routeDetail : details) {
						getLogger().info(
								"\t" + routeDetail.getKey() + " = "
										+ routeDetail.getValue());
					}
				}
			}
			return true;
		} else {
			getLogger().info("There is no issue with id " + getId());
			return true;
		}
	}

}
