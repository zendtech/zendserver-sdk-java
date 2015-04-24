/*******************************************************************************
 * Copyright (c) May 18, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.sdkcli.internal.commands;

import java.text.MessageFormat;

import org.zend.sdkcli.internal.options.Option;
import org.zend.sdklib.target.IZendTarget;
import org.zend.sdklib.target.LicenseExpiredException;
import org.zend.webapi.core.WebApiException;
import org.zend.webapi.core.connection.data.values.ServerType;
import org.zend.webapi.core.connection.data.values.WebApiVersion;
import org.zend.webapi.core.connection.response.ResponseCode;
import org.zend.webapi.internal.core.connection.exception.UnexpectedResponseCode;
import org.zend.webapi.internal.core.connection.exception.WebApiCommunicationError;

public class ListTargetsCommand extends TargetAwareCommand {

	private static final String STATUS = "s";

	@Option(opt = STATUS, required = false, description = "show status line for targets")
	public boolean isStatus() {
		return hasOption(STATUS);
	}

	@Override
	public boolean doExecute() {
		final IZendTarget[] list = getTargetManager().getTargets();
		if (list.length == 0) {
			commandLine.getLog().info("No Available Zend Targets.");
			return true;
		}

		commandLine.getLog().info("Available Zend Targets:");
		for (IZendTarget target : list) {
			commandLine.getLog().info("id: " + target.getId());
			commandLine.getLog().info("\tHost: " + target.getHost());
			commandLine.getLog().info(
					"\tBase URL: " + target.getDefaultServerURL());
			commandLine.getLog().info("\tKey: " + target.getKey());

			if (isStatus()) {
				IZendTarget t = null;
				try {
				t = testTargetConnection(target);
				} catch (WebApiException e) {
				} catch (LicenseExpiredException e) {
					getLogger()
							.error(MessageFormat
									.format("Cannot connect with target {0}. Check if license has not exipred.",
											target.getId()));
				}
				commandLine.getLog()
						.error("\tStatus: "
								+ (t != null ? "connected" : "disconnected"));
			}

		}

		return true;
	}
	
	public IZendTarget testTargetConnection(IZendTarget target)
			throws WebApiException, LicenseExpiredException {
		try {
			if (target.connect(WebApiVersion.V1_3, ServerType.ZEND_SERVER)) {
				return target;
			}
		} catch (WebApiCommunicationError e) {
			throw e;
		} catch (UnexpectedResponseCode e) {
			ResponseCode code = e.getResponseCode();
			switch (code) {
			case INTERNAL_SERVER_ERROR:
			case AUTH_ERROR:
			case INSUFFICIENT_ACCESS_LEVEL:
				throw e;
			default:
				break;
			}
		}
		try {
			if (target.connect(WebApiVersion.UNKNOWN, ServerType.ZEND_SERVER)) {
				return target;
			}
		} catch (WebApiException ex) {
			if (target.connect()) {
				return target;
			}
		}
		return null;
	}
}
