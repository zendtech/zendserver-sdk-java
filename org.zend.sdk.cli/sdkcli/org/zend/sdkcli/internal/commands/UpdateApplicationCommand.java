/*******************************************************************************
 * Copyright (c) May 24, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.sdkcli.internal.commands;

import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.List;

import org.zend.sdkcli.internal.options.Option;
import org.zend.sdkcli.monitor.StatusChangeListener;
import org.zend.sdklib.SdkException;
import org.zend.sdklib.internal.repository.UserBasedRepositoryLoader;
import org.zend.sdklib.manager.RepositoryManager;
import org.zend.sdklib.repository.IRepository;
import org.zend.sdklib.repository.site.Application;
import org.zend.webapi.core.connection.data.ApplicationInfo;

/**
 * Deploys package to specified target.
 * 
 * @author Wojciech Galanciak, 2011
 * 
 */
public class UpdateApplicationCommand extends ApplicationAwareCommand {

	private static final String PATH = "p";
	private static final String APPID = "a";
	private static final String PARAMS = "m";
	private static final String IGNORE_FAILURES = "f";
	private static final String APP_ID = "i";
	private static final String APP_REPO = "r";

	@Option(opt = PATH, required = false, description = "The path to the project or application package", argName="path")
	public String getPath() {
		final String value = getValue(PATH);
		if (value == null) {
			return getCurrentDirectory();
		}
		return value;
	}

	@Option(opt = APPID, required = true, description = "The application id", argName="app-id")
	public String getApplicationId() {
		return getValue(APPID);
	}
	
	@Option(opt = PARAMS, required = false, description = "The path to parameters properties file", argName="parameters")
	public String getParams() {
		return getValue(PARAMS);
	}

	@Option(opt = IGNORE_FAILURES, required = false, description = "Ignore failures")
	public boolean isIgnoreFailures() {
		return hasOption(IGNORE_FAILURES);
	}

	@Option(opt = APP_ID, required = false, description = "Application id from reposiotry", argName = "key")
	public String getAppName() {
		final String value = getValue(APP_ID);
		return value;
	}

	@Option(opt = APP_REPO, required = false, description = "URL of repository from which application should be deployed", argName = "branch")
	public String getRepositoryName() {
		final String value = getValue(APP_REPO);
		return value;
	}

	@Override
	public boolean doExecute() {
		ApplicationInfo info = null;
		getApplication().addStatusChangeListener(new StatusChangeListener());
		if (getRepositoryName() != null && getAppName() != null) {
			RepositoryManager manager = new RepositoryManager(
					new UserBasedRepositoryLoader());
			IRepository repository = manager
					.getRepositoryById(getRepositoryName());
			if (repository == null) {
				getLogger().error("Incorrect repository URL");
				return false;
			}
			try {
				Application app = getApplicationFromReposiotry(repository);
				if (app != null) {
					InputStream stream = repository.getPackage(app);
					info = getApplication().update(stream, app,
							getTargetId(),
							getApplicationId(), getParams(), isIgnoreFailures());
				} else {
					getLogger().error(
							MessageFormat.format(
									"There is no {0} application in {1} respository",
											getAppName(),
											getRepositoryName()));
				}
			} catch (SdkException e) {
				getLogger().error(e);
			} catch (IOException e) {
				getLogger().error(e);
			}
		} else {
			info = getApplication().update(getPath(),
				getTargetId(),
				getApplicationId(), getParams(), isIgnoreFailures());
		}
		if (info != null) {
			getLogger()
					.info(MessageFormat.format(
							"Application {0} (id {1}) was updated to {2}",
							info.getAppName(), info.getId(), info.getBaseUrl()));
			return true;
		}
		return false;
	}

	private Application getApplicationFromReposiotry(IRepository repository)
			throws SdkException {
		List<Application> apps = repository.getSite().getApplication();
		if (apps != null) {
			String name = getAppName();
			for (Application app : apps) {
				if (app.getName().equals(name)) {
					return app;
				}
			}
		}
		return null;
	}

}
