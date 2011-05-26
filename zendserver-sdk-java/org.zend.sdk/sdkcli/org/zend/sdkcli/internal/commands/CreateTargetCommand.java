/*******************************************************************************
 * Copyright (c) May 18, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.sdkcli.internal.commands;

import org.zend.sdkcli.internal.options.Option;
import org.zend.sdklib.target.IZendTarget;

/**
 * Creating a new target
 * 
 * @author Roy, 2011
 * 
 */
public class CreateTargetCommand extends TargetAwareCommand {

	private static final String ID = "t";
	private static final String KEY = "k";
	private static final String SECRETKEY = "s";
	private static final String HOST = "h";

	@Option(opt = KEY, required = true, description = "Target environment API Key name")
	public String getKey() {
		return getValue(KEY);
	}

	@Option(opt = SECRETKEY, required = true, description = "Target environment API Key secret value")
	public String getSecretKey() {
		return getValue(SECRETKEY);
	}

	@Option(opt = ID, required = false, description = "Target id")
	public String getId() {
		return getValue(ID);
	}

	@Option(opt = HOST, required = true, description = "Hostname")
	public String getHost() {
		return getValue(HOST);
	}

	@Override
	public boolean doExecute() {
		final String targetId = getId();
		final String key = getKey();
		final String secretKey = getSecretKey();
		final String host = getHost();
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

}
