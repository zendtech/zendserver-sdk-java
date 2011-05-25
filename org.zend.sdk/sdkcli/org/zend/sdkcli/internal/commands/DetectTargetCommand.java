/*******************************************************************************
 * Copyright (c) May 24, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.sdkcli.internal.commands;

import java.io.IOException;

import org.zend.sdklib.internal.utils.EnvironmentUtils;

/**
 * Detect localhost target.
 * 
 * @author Wojciech Galanciak, 2011
 * 
 */
public class DetectTargetCommand extends TargetAwareCommand {

	private static final String ID = "t";
	private static final String KEY = "key";

	@Override
	public boolean doExecute() {
		final String key = getValue(KEY);
		final String targetId = getValue(ID);
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

	@Override
	protected void setupOptions() {
		// key name
		addArgumentOption(KEY, false, "use given key name");
		// target name
		addArgumentOption(ID, false, "use given target name");
	}
}
