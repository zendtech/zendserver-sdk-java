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
import org.zend.sdklib.internal.target.ZendTargetAutoDetect;
import org.zend.sdklib.internal.utils.EnvironmentUtils;
import org.zend.sdklib.manager.DetectionException;
import org.zend.sdklib.manager.PrivilegesException;
import org.zend.sdklib.manager.ServerVersionException;
import org.zend.sdklib.manager.TargetException;
import org.zend.sdklib.manager.TargetsManager;
import org.zend.sdklib.target.IZendTarget;

/**
 * Detect localhost target.
 * 
 * @author Wojciech Galanciak, 2011
 */
public class DetectTargetCommand extends TargetAwareCommand {

	private static final String ID = "t";
	private static final String SECRET_KEY = "s";
	private static final String KEY = "k";

	@Option(opt = ID, required = false, description = "Id of the new target", argName = "id")
	public String getId() {
		return getValue(ID);
	}

	@Option(opt = SECRET_KEY, required = false, description = "Secret key of the new target (to be applied or used)", argName = "secret-key")
	public String getSecretKey() {
		return getValue(SECRET_KEY);
	}

	@Option(opt = KEY, required = false, description = "Key of the new target (to be applied)", argName = "key")
	public String getKey() {
		return getValue(KEY);
	}

	@Override
	public boolean doExecute() {
		final String targetId = getId();
		final String key = getKey();
		final String secretKey = getSecretKey();

		// users run this command to apply the generated key
		if (key != null && secretKey != null) {
			return applyKey(key, secretKey);
		}

		// detect localhost
		return detectLocalhostTarget(targetId, key);
	}

	private boolean detectLocalhostTarget(String targetId, String key) {
		IZendTarget target = null;
		try {
			target = getTargetManager().detectLocalhostTarget(
					targetId, key);
		} catch (ServerVersionException e2) {
			getLogger().error("Coudn't connect to localhost server, please make "
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
				key = key != null ? key : TargetsManager.DEFAULT_KEY + "." + System.getProperty("user.name");
				target = detection.createTemporaryLocalhost(targetId, key);
				try {
					// suppress connect cause the
					getTargetManager().add(target, true);
				} catch (TargetException e1) {
					// since the key is not registered yet, most probably there
					// will be a failure here
				}
			} else {
				getLogger().error("Use administrator account with elevated privileges");
				getLogger().error("Please consider using:");
				getLogger().error("\t> elevate detect target");
			}
		} catch (DetectionException e4) {
			// not handled
		}
		
		if (target == null) {
			return false;
		}
		
		if (target.isTemporary()) {
			getLogger().error("Localhost target was detected, to apply the secret key please "
					+ "consider running: ");
			getLogger().error(MessageFormat.format(
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

	private boolean applyKey(final String key, final String secretKey) {
		try {
			final String appliedSK = getTargetManager().applyKeyToLocalhost(
					key, secretKey);
			getLogger().info("Key was generated for localhost target. ");
			getLogger().info("\tKey: " + key);
			getLogger().info("\tSecret key: " + appliedSK);
			getLogger()
					.info("\tThis key must be kept secret and immediately revoked if "
							+ "there is any chance that it has been compromised");
			return true;
		} catch (IOException e) {
			getLogger().error(e);
			getLogger().error(
					"root privileges are required to run this command.");
			getLogger().error("Please consider using:");
			getLogger().error(
					"\t% sudo " + commandLine.getVerb() + " "
							+ commandLine.getDirectObject() + " ...");
		}
		return false;
	}
}
