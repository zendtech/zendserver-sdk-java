/*******************************************************************************
 * Copyright (c) May 18, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.sdkcli.internal.commands;

import java.io.IOException;

import org.zend.sdkcli.ParseError;
import org.zend.sdklib.internal.target.ZendTargetAutoDetect;
import org.zend.sdklib.internal.utils.EnvironmentUtils;
import org.zend.sdklib.target.IZendTarget;
import org.zend.webapi.core.WebApiException;
import org.zend.webapi.core.connection.response.ResponseCode;

public class CreateTargetCommandLine extends TargetAwareCommand {

	private static final String LOCALHOST = "localhost";
	private static final String ID = "t";
	private static final String KEY = "k";

	public CreateTargetCommandLine(CommandLine commandLine) throws ParseError {
		super(commandLine);
	}

	@Override
	public boolean execute() {
		if (hasOption(LOCALHOST)) {
			final ZendTargetAutoDetect autoDetect = new ZendTargetAutoDetect();
			final String targetId = getValue(ID) == null ? LOCALHOST
					: getValue(ID);
			final String key = getValue(KEY) == null ? "sdk" : getValue(KEY);
			IZendTarget target = null;
			try {
				target = autoDetect.getLocalZendServer(targetId, key);
			} catch (IOException e1) {
				getLogger().error("Permission denied.");
				if (EnvironmentUtils.isUnderLinux()
						|| EnvironmentUtils.isUnderMaxOSX()) {
					getLogger().error(
							"You need root privileges to run this command.");
					getLogger().error("Consider using:");
					getLogger().error(
							"\t% sudo " + commandLine.getVerb() + " "
									+ commandLine.getDirectObject() + " ...");
				} else {
					getLogger()
							.error("Use administrator account with elevated privileges");
					getLogger().error("Consider using:");
					getLogger().error(
							"\t> elevate " + commandLine.getVerb() + " "
									+ commandLine.getDirectObject() + " ...");
				}
			}
			if (target != null) {
				try {
					getTargetManager().add(target);
				} catch (WebApiException e) {
					getLogger()
							.error("Coudn't connect to local host server, please make "
									+ "sure you the server is up and its version is 5.5 and up.");
					getLogger()
							.error("More information provided by localhost:");

					final ResponseCode responseCode = e.getResponseCode();
					if (responseCode != null) {
						getLogger().error("\tError code: " + responseCode);
					}
					final String message = e.getMessage();
					if (message != null) {
						getLogger().error("\tError message: " + message);
					}
				}
			}
		}

		return false;
	}

	@Override
	protected void setupOptions() {
		// auto detection mode
		addOption(LOCALHOST, false, "auto detect localhost target");

		// key name
		addOption(KEY, true, "use given key name");

		// key name
		addOption(ID, true, "use given target name");
	}

}
