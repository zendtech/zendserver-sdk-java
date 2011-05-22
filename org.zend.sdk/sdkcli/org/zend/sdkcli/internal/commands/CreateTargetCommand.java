/*******************************************************************************
 * Copyright (c) May 18, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.sdkcli.internal.commands;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.zend.sdkcli.ParseError;
import org.zend.sdklib.internal.target.ZendTarget;
import org.zend.sdklib.internal.utils.EnvironmentUtils;
import org.zend.webapi.core.WebApiException;
import org.zend.webapi.core.connection.response.ResponseCode;

/**
 * Creating a new target
 * 
 * @author Roy, 2011
 * 
 */
public class CreateTargetCommand extends TargetAwareCommand {

	private static final String LOCALHOST = "localhost";
	private static final String ID = "t";
	private static final String KEY = "key";
	private static final String SECRETKEY = "secret";
	private static final String HOST = "host";

	public CreateTargetCommand(CommandLine commandLine) throws ParseError {
		super(commandLine);
	}

	@Override
	public boolean execute() {
		// guess the next target id if not provided
		final String targetId = getValue(ID) == null ? Integer
				.toString(getTargetManager().list().length) : getValue(ID);

		// install target
		return hasOption(LOCALHOST) ? installLocalhost(targetId)
				: installCustomTarget(targetId);
	}

	private boolean installCustomTarget(String targetId2) {
		final String targetId = getValue(ID);
		final String key = getValue(KEY);
		final String secretKey = getValue(SECRETKEY);
		final String host = getValue(HOST);

		if (key == null || secretKey == null || host == null) {
			getLogger().error("Mandatory arguments are missing.");
			getLogger().error("\tKey: " + key);
			getLogger().error("\tSecret Key: " + secretKey);
			getLogger().error("\tHost: " + host);
			return false;
		}

		ZendTarget zendTarget;
		try {
			zendTarget = new ZendTarget(targetId, new URL(host), key, secretKey);
			if (getTargetManager().add(zendTarget) == null) {
				getLogger().error("Error adding Zend Target " + targetId);
				return false;
			}

		} catch (MalformedURLException e) {
			getLogger().error("Error adding Zend Target " + targetId);
			getLogger().error("\tpossible error" + e.getMessage());
			return false;
		} catch (WebApiException e) {
			getLogger().error("Error adding Zend Target " + targetId);
			getLogger().error("\tpossible error" + e.getMessage());
			return false;
		}

		return true;
	}

	private boolean installLocalhost(String targetId) {
		final String key = getValue(KEY) == null ? "sdk" : getValue(KEY);
		// detect localhost
		try {
			getTargetManager().detectLocalhostTarget(targetId, key);
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
				getLogger().error(
						"Use administrator account with elevated privileges");
				getLogger().error("Consider using:");
				getLogger().error(
						"\t> elevate " + commandLine.getVerb() + " "
								+ commandLine.getDirectObject() + " ...");
			}
			return false;
		} catch (WebApiException e) {
			getLogger()
					.error("Coudn't connect to local host server, please make "
							+ "sure you the server is up and its version is 5.5 and up.");
			getLogger().error("More information provided by localhost:");

			final ResponseCode responseCode = e.getResponseCode();
			if (responseCode != null) {
				getLogger().error("\tError code: " + responseCode);
			}
			final String message = e.getMessage();
			if (message != null) {
				getLogger().error("\tError message: " + message);
			}
			return false;
		}
		return true;
	}

	@Override
	protected void setupOptions() {
		// auto detection mode
		addOption(LOCALHOST, false, "auto detect localhost target");
		// key name
		addOption(KEY, false, "use given key name");
		// target name
		addOption(ID, false, "use given target name");
		// secret key name
		addOption(SECRETKEY, false, "use given secret key");
		// host name
		addOption(HOST, false, "use given host name");
	}
}
