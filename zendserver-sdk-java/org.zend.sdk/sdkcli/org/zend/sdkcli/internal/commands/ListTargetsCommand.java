/*******************************************************************************
 * Copyright (c) May 18, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.sdkcli.internal.commands;

import org.zend.sdkcli.ParseError;
import org.zend.sdklib.target.IZendTarget;
import org.zend.webapi.core.WebApiException;

public class ListTargetsCommand extends TargetAwareCommand {

	private static final String STATUS = "status";

	public ListTargetsCommand(CommandLine commandLine) throws ParseError {
		super(commandLine);
	}

	@Override
	public boolean execute() {
		final IZendTarget[] list = getTargetManager().list();
		if (list.length == 0) {
			commandLine.getLog().info("No Available Zend Targets.");
			return true;
		}

		commandLine.getLog().info("Available Android targets:");
		for (IZendTarget target : list) {
			commandLine.getLog().info("id: " + target.getId());
			commandLine.getLog().info("\tHost: " + target.getHost());
			commandLine.getLog().info("\tKey: " + target.getKey());
			commandLine.getLog().info("\tSecret Key: " + target.getSecretKey());

			if (hasOption(STATUS)) {
				boolean connect = false;
				try {
					connect = target.connect();
				} catch (WebApiException e) {
					connect = false;
				}
				commandLine.getLog()
						.info("\tStatus: "
								+ (connect ? "connected" : "disconnected"));
			}

		}

		return true;
	}

	@Override
	protected void setupOptions() {
		addOption(STATUS, false, "show status line for targets");
	}

}
