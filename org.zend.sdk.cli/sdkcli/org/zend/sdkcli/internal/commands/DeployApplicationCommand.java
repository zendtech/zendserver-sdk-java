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
import org.zend.sdkcli.monitor.StatusChangeListener;
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

	@Option(opt = VHOST, required = false, description = "Specify the virtual host which should be used. If a virtual host with the specified name does not exist, it will be created. By default if virtual host is not specified then the default one will be used (marked as <default-server> in the application url)")
	public String getVhost() {
		return getValue(VHOST);
	}

	public boolean isVhost() {
		return getVhost() != null;
	}

	@Override
	public boolean doExecute() {
		getApplication().addStatusChangeListener(new StatusChangeListener());
		ApplicationInfo info = getApplication().deploy(getPath(),
				getBasePath(), getTargetId(), getParams(), getName(),
				isIgnoreFailures(), getVhost(), !isVhost());
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
