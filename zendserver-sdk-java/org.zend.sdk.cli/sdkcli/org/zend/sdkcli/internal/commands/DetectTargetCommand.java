/*******************************************************************************
 * Copyright (c) May 24, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.sdkcli.internal.commands;

import java.io.IOException;
import java.text.MessageFormat;

import org.zend.sdkcli.internal.options.Option;
import org.zend.sdklib.SdkException;
import org.zend.sdklib.internal.target.ApiKeyDetector;
import org.zend.sdklib.internal.target.ZendTargetAutoDetect;
import org.zend.sdklib.internal.utils.EnvironmentUtils;
import org.zend.sdklib.manager.DetectionException;
import org.zend.sdklib.manager.PrivilegesException;
import org.zend.sdklib.manager.ServerVersionException;
import org.zend.sdklib.manager.TargetException;
import org.zend.sdklib.manager.TargetsManager;
import org.zend.sdklib.target.IZendTarget;
import org.zend.sdklib.target.InvalidCredentialsException;
import org.zend.sdklib.target.LicenseExpiredException;

/**
 * Detect localhost target.
 * 
 * @author Wojciech Galanciak, 2011
 */
public class DetectTargetCommand extends TargetAwareCommand {

	private static final String ID = "t";
	private static final String KEY = "k";
	private static final String USERNAME = "u";
	private static final String PASSWORD = "p";

	@Option(opt = ID, required = false, description = "Id of the new target", argName = "id")
	public String getId() {
		return getValue(ID);
	}

	@Option(opt = KEY, required = false, description = "Key of the new target (to be applied)", argName = "key")
	public String getKey() {
		return getValue(KEY);
	}

	@Option(opt = USERNAME, required = false, description = "Zend Server username. It is used during Zend Server 6 detection. If not provided user will be asked for it.", argName = "username")
	public String getUsername() {
		return getValue(USERNAME);
	}

	@Option(opt = PASSWORD, required = false, description = "Zend Server password. It is used during Zend Server 6 detection. If not provided user will be asked for it.", argName = "password")
	public String getPassword() {
		return getValue(PASSWORD);
	}

	@Override
	public boolean doExecute() {
		final String targetId = getId();
		final String key = getKey();

		// detect localhost
		return detectLocalhostTarget(targetId, key);
	}

	private boolean detectLocalhostTarget(String targetId, String key) {
		IZendTarget target = null;
		try {
			target = getTargetManager().detectLocalhostTarget(targetId, key);
		} catch (IllegalArgumentException e) {
			target = detectZendServer6(null);
		} catch (ServerVersionException e2) {
			getLogger()
					.error("Coudn't connect to localhost server, please make "
							+ "sure your server is up and running. This tool works with "
							+ "version 5.5 and up.");
			getLogger().error("More information provided by localhost server:");
			if (e2.getResponseCode() != -1) {
				getLogger().error("\tError code: " + e2.getResponseCode());
			}
			if (e2.getMessage() != null) {
				getLogger().error("\tError message: " + e2.getMessage());
			}
		} catch (PrivilegesException e3) {
			if (EnvironmentUtils.isUnderLinux()
					|| EnvironmentUtils.isUnderMaxOSX()) {

				ZendTargetAutoDetect detection = null;
				try {
					detection = new ZendTargetAutoDetect();
				} catch (IOException e) {
				}
				key = key != null ? key : TargetsManager.DEFAULT_KEY + "."
						+ System.getProperty("user.name");
				target = detection.createTemporaryLocalhost(targetId, key);
				try {
					// suppress connect cause the
					getTargetManager().add(target, true);
				} catch (TargetException e1) {
					// since the key is not registered yet, most probably there
					// will be a failure here
				} catch (LicenseExpiredException e) {
					getLogger()
							.error("Cannot detect local target. Check if license has not exipred.");
				}
			} else {
				getLogger().error(
						"Use administrator account with elevated privileges");
				getLogger().error("Please consider using:");
				getLogger().error("\t> elevate detect target");
			}
		} catch (DetectionException e4) {
			// not handled
		} catch (LicenseExpiredException e) {
			getLogger()
					.error("Cannot detect local target. Check if license has not exipred.");
		}

		if (target == null) {
			return false;
		}

		if (target.isTemporary()) {
			getLogger().error(
					"Localhost target was detected, to apply the secret key please "
							+ "consider running: ");
			getLogger().error(
					MessageFormat.format(
							"\t> sudo ./zend detect target -k {0} -s {1}",
							target.getKey(), target.getSecretKey()));
		}

		// announce target created
		getLogger().info(
				"The localhost target was detected " + target.getHost());
		getLogger().info("\tKey: " + target.getKey());
		getLogger().info("\tSecret key: " + target.getSecretKey());
		getLogger().info(
				"\tThis key must be kept secret and immediately revoked if "
						+ "there is any chance that it has been compromised");
		return true;
	}

	private IZendTarget detectZendServer6(String message) {
		ApiKeyDetector manager = new CliApiKeyDetector(getUsername(),
				getPassword());
		String key = getKey();
		if (key!= null) {
			manager.setKey(key);
		}
		try {
			manager.createApiKey(message);
			TargetsManager tm = getTargetManager();
			return tm.detectLocalhostTarget(null, manager.getKey(),
					manager.getSecretKey());
		} catch (InvalidCredentialsException e) {
			return detectZendServer6("Provided credentials are not valid."); //$NON-NLS-1$
		} catch (SdkException e) {
			getLogger().error(
					"Cannot detect local target. " + e.getMessage());
		} catch (LicenseExpiredException e) {
			getLogger().error(
					"Cannot detect local target. Check if license has not expired.");
		} catch (DetectionException e) {
			getLogger().error(
					"Cannot detect local target. " + e.getMessage());
		}
		return null;
	}

}
