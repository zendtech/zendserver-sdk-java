/*******************************************************************************
 * Copyright (c) May 24, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.sdkcli.internal.commands;

import java.text.MessageFormat;

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
	private static final String BASE_PATH = "b";
	private static final String PARAMS = "m";
	private static final String NAME = "n";
	private static final String IGNORE_FAILURES = "f";
	private static final String VHOST = "h";
	private static final String DEFAULT_SERVER = "d";

	@Option(opt = PATH, required = false, description = "Application package location or project's directory, if not provided current directory is considered", argName = "path")
	public String getPath() {
		final String value = getValue(PATH);
		if (value == null) {
			return getCurrentDirectory();
		}
		return value;
	}

	@Option(opt = BASE_PATH, required = false, description = "Base path of this application (relative to hostname), if not provided project name is considered", argName = "base-path")
	public String getBasePath() {
		return getValue(BASE_PATH);
	}

	@Option(opt = PARAMS, required = false, description = "Properties file path of the parameters given to this application", argName = "parameters")
	public String getParams() {
		return getValue(PARAMS);
	}

	@Option(opt = NAME, required = false, description = "Application name", argName = "name")
	public String getName() {
		return getValue(NAME);
	}

	@Option(opt = IGNORE_FAILURES, required = false, description = "Ignore failures")
	public boolean isIgnoreFailures() {
		return hasOption(IGNORE_FAILURES);
	}

	/**
	 * Validates that both create + default are not present together. If none is
	 * chosen then the default option should be considered. Else just return the
	 * one that is turned on.
	 * 
	 * @return true iff default server
	 */
	@Option(opt = DEFAULT_SERVER, required = false, description = "Use default server for this application")
	public boolean isDefaultServer() {
		final boolean defaultServer = hasOption(DEFAULT_SERVER);
		final boolean vhost = isVhost();

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

	@Option(opt = VHOST, required = false, description = "The name of the vhost to create or use if exists")
	public String getVhost() {
		return getValue(VHOST);
	}

	public boolean isVhost() {
		return getVhost() != null;
	}

	@Override
	public boolean doExecute() {
		ApplicationInfo info = getApplication().deploy(getPath(),
				getBasePath(), getTargetId(), getParams(), getName(),
				isIgnoreFailures(), getVhost(), isDefaultServer());
		if (info == null) {
			return false;
		}
		getLogger().info(
				MessageFormat.format(
						"Application {0} (id {1}) is deployed to {2}",
						info.getAppName(), info.getId(), info.getBaseUrl()));
		return true;
	}
}
