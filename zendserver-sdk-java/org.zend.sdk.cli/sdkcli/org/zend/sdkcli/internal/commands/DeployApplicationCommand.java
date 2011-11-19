/*******************************************************************************
 * Copyright (c) May 24, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.sdkcli.internal.commands;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.text.MessageFormat;
import java.util.List;

import org.eclipse.jgit.transport.URIish;
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
public class DeployApplicationCommand extends ApplicationAwareCommand {

	private class DeployCloneCommand extends GitCloneProjectCommand {
		@Override
		public String getDir() {
			try {
				URIish uri = new URIish(DeployApplicationCommand.this.getRepo());
				return uri.getHumanishName();
			} catch (URISyntaxException e) {
				getLogger().error(e);
			}
			return null;
		}

		@Override
		public String getRepo() {
			return DeployApplicationCommand.this.getRepo();
		}

		@Override
		public String getUser() {
			return DeployApplicationCommand.this.getUser();
		}

		@Override
		public String getPassword() {
			return DeployApplicationCommand.this.getPassword();
		}
		
		@Override
		public String getKey() {
			return DeployApplicationCommand.this.getKey();
		}

		@Override
		public String getBranch() {
			return DeployApplicationCommand.this.getBranch();
		}

		@Override
		public boolean isVerbose() {
			return DeployApplicationCommand.this.isVerbose();
		}
		
		public File getProjectFile() {
			try {
				if (getRepo() != null) {
					return getDirectory(getRepo());
				}
			} catch (URISyntaxException e) {
				getLogger().error(e);
			}
			return null;
		}
	}

	private static final String PATH = "p";
	private static final String BASE_PATH = "b";
	private static final String PARAMS = "m";
	private static final String NAME = "n";
	private static final String IGNORE_FAILURES = "f";
	private static final String VHOST = "h";
	private static final String REPO = "l";
	private static final String USER = "u";
	private static final String PASSWD = "p";
	private static final String KEY = "k";
	private static final String BRANCH = "g";
	private static final String APP_ID = "i";
	private static final String APP_REPO = "r";

	private DeployCloneCommand cloneCommand = new DeployCloneCommand();

	@Option(opt = PATH, required = false, description = "Application package location or project's directory, if not provided current directory is considered", argName = "path")
	public String getPath() {
		final String value = getValue(PATH);
		if (value == null) {
			if (getRepo() != null && cloneCommand.getProjectFile() != null) {
				return cloneCommand.getProjectFile().getAbsolutePath();
			}
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

	@Option(opt = REPO, required = false, description = "Repository to clone from", argName = "repository")
	public String getRepo() {
		final String value = getValue(REPO);
		return value;
	}

	@Option(opt = USER, required = false, description = "User name for git repository access", argName = "user")
	public String getUser() {
		final String value = getValue(USER);
		return value;
	}

	@Option(opt = PASSWD, required = false, description = "Password for git repository access", argName = "password")
	public String getPassword() {
		final String value = getValue(PASSWD);
		return value;
	}

	@Option(opt = KEY, required = false, description = "Path to SSH private key for git repository access", argName = "key")
	public String getKey() {
		final String value = getValue(KEY);
		return value;
	}

	@Option(opt = BRANCH, required = false, description = "Initial branch to check out project from git repository", argName = "branch")
	public String getBranch() {
		final String value = getValue(BRANCH);
		return value;
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

	public boolean isVhost() {
		return getVhost() != null;
	}

	@Override
	public boolean doExecute() {
		boolean doDeploy = true;
		ApplicationInfo info = null;
		getApplication().addStatusChangeListener(new StatusChangeListener());
		try {
			if (getRepositoryName() != null && getAppName() != null) {
				doDeploy = false;
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
						info = getApplication().deploy(stream, app,
								getBasePath(), getTargetId(), getParams(),
								getName(), isIgnoreFailures(), getVhost(),
								!isVhost());
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
			} else if (getRepo() != null) {
				doDeploy = cloneCommand.doExecute();
			}
			if (doDeploy) {
				info = getApplication().deploy(getPath(), getBasePath(),
						getTargetId(), getParams(), getName(),
						isIgnoreFailures(), getVhost(), !isVhost());
			}
			if (info != null) {
				getLogger().info(
						MessageFormat.format(
								"Application {0} (id {1}) is deployed to {2}",
								info.getAppName(), info.getId(),
								info.getBaseUrl()));
				return true;
			}
		} finally {
			if (getRepo() != null && cloneCommand.getProjectFile() != null) {
				delete(cloneCommand.getProjectFile());
			}
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

	private boolean delete(File file) {
		if (file.isDirectory()) {
			String[] children = file.list();
			for (int i = 0; i < children.length; i++) {
				boolean result = delete(new File(file, children[i]));
				if (!result) {
					return false;
				}
			}
		}
		return file.delete();
	}
}
