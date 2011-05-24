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

/**
 * Creating a new target
 * 
 * @author Roy, 2011
 * 
 */
public class CreateTargetCommand extends TargetAwareCommand {

	private static final String ID = "t";
	private static final String KEY = "key";
	private static final String SECRETKEY = "secretKey";
	private static final String HOST = "host";

	public CreateTargetCommand(CommandLine commandLine) throws ParseError {
		super(commandLine);
	}

	@Override
	public boolean execute() {
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
		IZendTarget target = targetId == null ? getTargetManager()
				.createTarget(host, key, secretKey) : getTargetManager()
				.createTarget(targetId, host, key, secretKey);
		if (target == null) {
			return false;
		}
		return true;
	}

	@Override
	protected void setupOptions() {
		// key name
		addArgumentOption(KEY, true, "use given key name");
		// target name
		addArgumentOption(ID, false, "use given target name");
		// secret key name
		addArgumentOption(SECRETKEY, true, "use given secret key");
		// host name
		addArgumentOption(HOST, true, "use given host name");
	}
}
