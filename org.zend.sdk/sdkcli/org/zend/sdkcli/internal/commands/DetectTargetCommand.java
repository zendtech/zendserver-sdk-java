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

/**
 * Detect localhost target.
 * 
 * @author Wojciech Galanciak, 2011
 */
public class DetectTargetCommand extends TargetAwareCommand {

	private static final String ID = "t";
	private static final String KEY = "k";

	@Option(opt = ID, required = false, description = "The target id to create", argName="id")
	public String getId() {
		return getValue(ID);
	}

	@Option(opt = KEY, required = false, description = "The key to use", argName="key")
	public String getKey() {
		return getValue(KEY);
	}

	@Override
	public boolean doExecute() {
		final String key = getKey();
		final String targetId = getId();
		// detect localhost
		try {
			if (getTargetManager().detectLocalhostTarget(targetId, key) != null) {
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
		return false;
	}
}
