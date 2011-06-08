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
import org.zend.sdklib.internal.utils.EnvironmentUtils;
import org.zend.sdklib.target.IZendTarget;

/**
 * Detect localhost target.
 * 
 * @author Wojciech Galanciak, 2011
 */
public class DetectTargetCommand extends TargetAwareCommand {

	private static final String ID = "t";
	private static final String ADD_KEY_ONLY = "a";
	private static final String KEY = "k";

	@Option(opt = ID, required = false, description = "The target id to create", argName="id")
	public String getId() {
		return getValue(ID);
	}

	@Option(opt = ADD_KEY_ONLY, required = false, description = "This operation will only add a valid key to the localhost server")
	public boolean isAddKeyOnly() {
		return hasOption(ADD_KEY_ONLY);
	}
	
	@Option(opt = KEY, required = false, description = "The key to use", argName="key")
	public String getKey() {
		return getValue(KEY);
	}

	@Override
	public boolean doExecute() {
		final String key = getKey();
		final String targetId = getId();
		final boolean addKeyOnly = isAddKeyOnly();
		try {
			final IZendTarget target = getTargetManager().detectLocalhostTarget(targetId, key, addKeyOnly);
			if (target != null) {
				if (addKeyOnly) {
					getLogger().info("Key " + target.getKey() + " is available");
				} else {
					getLogger().info("Target " + target.getId() + " is available");
				}
				return true;
			}
		} catch (IOException e) {
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
		}
		getLogger().error(
				"Operation failed.");
		return false;
	}
}
