/*******************************************************************************
 * Copyright (c) May 24, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.sdkcli.internal.commands;

import org.zend.sdkcli.internal.options.Option;
import org.zend.webapi.core.connection.data.ApplicationInfo;

/**
 * Deploys package to specified target.
 * 
 * @author Wojciech Galanciak, 2011
 * 
 */
public class DeployApplicationCommand extends ApplicationAwareCommand {

	private static final String PATH = "p";
	private static final String BASE_URL = "b";
	private static final String PARAMS = "m";
	private static final String NAME = "n";
	private static final String IGNORE_FAILURES = "f";
	private static final String CREATE_VHOST = "c";
	private static final String DEFAULT_SERVER = "d";

	@Option(opt = PATH, required = false, description = "The path to the project or application package", argName = "path")
	public String getPath() {
		final String value = getValue(PATH);
		if (value == null) {
			return getCurrentDirectory();
		}
		return value;
	}

	@Option(opt = BASE_URL, required = false, description = "The base URL of the application", argName = "url")
	public String getBaseUrl() {
		return getValue(BASE_URL);
	}

	@Option(opt = PARAMS, required = false, description = "The path to parameters properties file", argName = "parameters")
	public String getParams() {
		return getValue(PARAMS);
	}

	@Option(opt = NAME, required = false, description = "The application name", argName = "name")
	public String getName() {
		return getValue(NAME);
	}

	@Option(opt = IGNORE_FAILURES, required = false, description = "Ignore failures")
	public boolean isIgnoreFailures() {
		return hasOption(IGNORE_FAILURES);
	}

	@Option(opt = CREATE_VHOST, required = false, description = "Create vhost")
	public boolean isCreateVhost() {
		return !isDefaultServer();
	}

	/**
	 * Validates that both create + default are not present together. If none is
	 * chosen then the default option should be considered. Else just return the
	 * one that is turned on.
	 * 
	 * @return true iff default server
	 */
	@Option(opt = DEFAULT_SERVER, required = false, description = "Use default server")
	public boolean isDefaultServer() {
		final boolean defaultServer = hasOption(DEFAULT_SERVER);
		final boolean vhost = hasOption(CREATE_VHOST);

		// both turned on -> error
		if (defaultServer && vhost) {
			final IllegalArgumentException e = new IllegalArgumentException(
					"Error: both create host and default "
							+ "server options are provided. Only one option "
							+ "should be present at a time.");
			getLogger().error(e);
			throw e;
		}

		// both turned off -> default
		if (!defaultServer && !vhost) {
			return true;
		}

		// only one is enabled
		return defaultServer;
	}

	@Override
	public boolean doExecute() {
		ApplicationInfo info = getApplication().deploy(getPath(), getBaseUrl(),
				getTargetId(), getParams(), getName(), isIgnoreFailures(),
				isCreateVhost(), isDefaultServer());
		if (info == null) {
			return false;
		}
		return true;
	}
}
