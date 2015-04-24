/*******************************************************************************
 * Copyright (c) Nov 28, 2011 Zend Technologies Ltd. 
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
import java.util.Random;

import org.eclipse.jgit.transport.URIish;
import org.zend.sdkcli.internal.monitor.StatusChangeListener;
import org.zend.sdkcli.internal.options.Option;
import org.zend.sdklib.SdkException;
import org.zend.sdklib.internal.repository.UserBasedRepositoryLoader;
import org.zend.sdklib.manager.RepositoryManager;
import org.zend.sdklib.repository.IRepository;
import org.zend.sdklib.repository.site.Application;
import org.zend.sdklib.target.IZendTarget;
import org.zend.webapi.core.connection.data.ApplicationInfo;

/**
 * Abstract class which represents command related to transferring package
 * during operation execution. It supports three kind of application sources:
 * <ul>
 * <li>local deployment package or application folder</li>
 * <li>git repository</li>
 * <li>Zend Reposiotry</li>
 * </ul>
 * 
 * @author Wojciech Galanciak, 2011
 * 
 */
public abstract class AbstractDeploymentCommand extends ApplicationAwareCommand {

	private class DeploymentCloneCommand extends GitCloneProjectCommand {

		private AbstractDeploymentCommand command;
		private File directory;

		private DeploymentCloneCommand(AbstractDeploymentCommand command) {
			this.command = command;
		}

		@Override
		public String getDir() {
			try {
				URIish uri = new URIish(command.getGitRepository());
				return uri.getHumanishName();
			} catch (URISyntaxException e) {
				getLogger().error(e);
			}
			return null;
		}

		@Override
		public String getRepo() {
			return command.getGitRepository();
		}

		@Override
		public String getUser() {
			return command.getUser();
		}

		@Override
		public String getPassword() {
			return command.getPassword();
		}
		
		@Override
		public String getKey() {
			return command.getKey();
		}

		@Override
		public String getBranch() {
			return command.getBranch();
		}

		@Override
		public boolean isVerbose() {
			return command.isVerbose();
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

		@Override
		protected File getDirectory(String repo) throws URISyntaxException {
			if (directory == null) {
				String dir = getDir();
				if (dir != null) {
					directory = new File(getTempFile(dir), dir);
				} else {
					URIish uri = new URIish(repo);
					String name = uri.getHumanishName();
					directory = new File(getTempFile(name), name);
				}
			}
			return directory;
		}

		private File getTempFile(String path) {
			String tempDir = System.getProperty("java.io.tmpdir");
			File tempFile = new File(tempDir + File.separator + path
					+ new Random().nextInt());
			if (!tempFile.exists()) {
				tempFile.mkdir();
			}
			return tempFile;
		}
	}

	private static final String PATH = "p";
	private static final String PARAMS = "m";
	private static final String IGNORE_FAILURES = "f";

	private static final String REPO = "r";
	private static final String USER = "u";
	private static final String PASSWD = "d";
	private static final String KEY = "k";
	private static final String BRANCH = "g";

	private static final String APP_ID = "i";
	private static final String APP_REPO = "z";

	private DeploymentCloneCommand cloneCommand = new DeploymentCloneCommand(
			this);

	@Option(opt = PATH, required = false, description = "Application package location or project's directory, if not provided current directory is considered", argName = "path")
	public String getPath() {
		final String value = getValue(PATH);
		if (value == null) {
			if (getGitRepository() != null
					&& cloneCommand.getProjectFile() != null) {
				return cloneCommand.getProjectFile().getAbsolutePath();
			}
			return getCurrentDirectory();
		}
		return value;
	}

	@Option(opt = PARAMS, required = false, description = "Properties file path of the parameters given to this application or a list of key=value separated by a comma", argName = "parameters")
	public String getParams() {
		return getValue(PARAMS);
	}

	@Option(opt = IGNORE_FAILURES, required = false, description = "Ignore failures")
	public boolean isIgnoreFailures() {
		return hasOption(IGNORE_FAILURES);
	}

	@Option(opt = REPO, required = false, description = "Repository to clone from", argName = "git repository")
	public String getGitRepository() {
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

	@Option(opt = APP_ID, required = false, description = "Application id in Zend Reposiotry", argName = "application id")
	public String getAppName() {
		final String value = getValue(APP_ID);
		return value;
	}

	@Option(opt = APP_REPO, required = false, description = "URL of Zend Repository from which application should be downloaded", argName = "zend repository")
	public String getRepositoryName() {
		final String value = getValue(APP_REPO);
		return value;
	}

	@Override
	public boolean doExecute() {
		ApplicationInfo info = null;
		String targetId = getTargetId();
		if (targetId != null) {
			IZendTarget target = getTargetManager().getTargetById(targetId);
			if (target == null) {
				getLogger().error(
						MessageFormat.format(
								"Target with id {0} does not exist.", targetId));
				return false;
			}
		}
		getApplication().addStatusChangeListener(new StatusChangeListener());
		if (getRepositoryName() != null && getAppName() != null) {
			info = repositoryOperation();
		} else if (getRepositoryName() != null || getAppName() != null) {
			getLogger().error("Missing application id or Zend Repository URL.");
			return true;
		} else if (getGitRepository() != null) {
			info = gitOperation();
		} else {
			info = doOperation();
		}
		return resolveResult(info);
	}

	protected abstract boolean resolveResult(ApplicationInfo info);

	protected abstract ApplicationInfo doRepositoryOperation(
			InputStream stream, Application app);

	protected abstract ApplicationInfo doGitOperation();

	protected abstract ApplicationInfo doOperation();

	private ApplicationInfo repositoryOperation() {
		RepositoryManager manager = new RepositoryManager(
				new UserBasedRepositoryLoader());
		IRepository repository = manager.getRepositoryById(getRepositoryName());
		if (repository == null) {
			getLogger().error("Incorrect Zend Repository URL");
			return null;
		}
		try {
			Application app = getApplicationFromRepository(repository);
			if (app != null) {
				InputStream stream = repository.getPackage(app);
				return doRepositoryOperation(stream, app);
			} else {
				getLogger()
						.error(MessageFormat
								.format("There is no {0} application in {1} respository",
										getAppName(), getRepositoryName()));
			}
		} catch (SdkException e) {
			getLogger().error(e);
		} catch (IOException e) {
			getLogger().error(e);
		}
		return null;
	}

	private ApplicationInfo gitOperation() {
		try {
			if (cloneCommand.doExecute()) {
				return doGitOperation();
			}
		} finally {
			if (cloneCommand.getProjectFile() != null) {
				delete(cloneCommand.getProjectFile().getParentFile());
			}
		}
		return null;
	}

	protected Application getApplicationFromRepository(IRepository repository)
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
