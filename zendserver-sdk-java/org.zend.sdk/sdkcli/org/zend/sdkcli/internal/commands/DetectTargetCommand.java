/*******************************************************************************
 * Copyright (c) May 24, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.sdkcli.internal.commands;

import java.io.IOException;

import org.zend.sdkcli.internal.options.Option;
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
		final IZendTarget target = getTargetManager().detectLocalhostTarget(
				targetId, key);
		if (target == null) {
			return false;
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
