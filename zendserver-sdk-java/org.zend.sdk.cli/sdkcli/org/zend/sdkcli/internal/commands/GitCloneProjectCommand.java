/*******************************************************************************
 * Copyright (c) Sep 6, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.sdkcli.internal.commands;

import java.io.File;
import java.io.PrintWriter;
import java.net.URISyntaxException;
import java.text.MessageFormat;

import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.JGitInternalException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.TextProgressMonitor;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.URIish;
import org.zend.sdkcli.internal.mapping.CliMappingLoader;
import org.zend.sdkcli.internal.options.Option;
import org.zend.sdklib.application.ZendProject;

/**
 * Clones repository based on provided remote repository URL.
 * 
 * @author Wojciech Galanciak, 2011
 * 
 */
public class GitCloneProjectCommand extends AbstractGitCommand {

	private static final String GITHUB_HOST = "github.com";
	private static final String REPO = "r";
	private static final String DIR = "d";
	private static final String BRANCH = "b";

	@Option(opt = REPO, required = true, description = "Repository to clone from", argName = "repository")
	public String getRepo() {
		final String value = getValue(REPO);
		return value;
	}

	@Option(opt = DIR, required = false, description = "The optional directory associated with the clone operation. If the directory isn't set, a name associated with the source uri will be used", argName = "directory")
	public String getDir() {
		final String value = getValue(DIR);
		return value;
	}

	@Option(opt = BRANCH, required = false, description = " the initial branch to check out when cloning the repository", argName = "branch")
	public String getBranch() {
		final String value = getValue(BRANCH);
		return value;
	}

	@Override
	protected boolean doExecute() {
		CloneCommand clone = new CloneCommand();
		String repo = getRepo();
		clone.setURI(repo);
		clone.setRemote(Constants.DEFAULT_REMOTE_NAME);
		File dir = null;
		try {
			dir = getDirectory(repo);
			if (dir.exists()) {
				File[] children = dir.listFiles();
				if (children != null && children.length > 0) {
					getLogger()
							.error(MessageFormat
									.format("Destination folder {0} already exists and is not an empty directory",
											dir.getName()));
					return false;
				}
			}
			clone.setDirectory(dir);
		} catch (URISyntaxException e) {
			getLogger().error(e);
			return false;
		}
		clone.setProgressMonitor(new TextProgressMonitor(new PrintWriter(
				System.out)));
		if (!askUsername()) {
			getLogger().info(
					MessageFormat.format("Cloning into {0}...", dir.getName()));
		}
		String branch = getBranch();
		if (branch != null) {
			clone.setBranch(Constants.R_HEADS + branch);
		}
		try {
			URIish uri = new URIish(repo);
			if (GITHUB_HOST.equals(uri.getHost())
					&& "git".equals(uri.getUser())) {
				if (!prepareSSHFactory()) {
					return false;
				}
			} else {
				CredentialsProvider credentials = getCredentials(repo);
				if (credentials != null) {
					clone.setCredentialsProvider(credentials);
				}
			}
			clone.call();
		} catch (JGitInternalException e) {
			delete(dir);
			getLogger().error(e);
			return false;
		} catch (URISyntaxException e) {
			delete(dir);
			getLogger().error(e);
			return false;
		} catch (InvalidRemoteException e) {
			delete(dir);
			getLogger().error(e);
			return false;
		} catch (TransportException e) {
			delete(dir);
			String repoUrl = getRepo();
			if (repoUrl != null && repoUrl.startsWith("https")) {
				if (e.getMessage().endsWith("not authorized")) {
					setAskUsername(true);
					return doExecute();
				}
			}
			getLogger().error(e);
			return false;
		} catch (GitAPIException e) {
			delete(dir);
			getLogger().error(e);
			return false;
		}
		updateProject(dir);
		getLogger().info(
				"The remote name used to keep track of the cloned repository is: "
						+ Constants.DEFAULT_REMOTE_NAME);
		return true;
	}

	private void updateProject(File directory) {
		ZendProject project = new ZendProject(directory, new CliMappingLoader());
		try {
			final boolean update = project.update(null);
			if (update) {
				getLogger()
						.debug("Project is updated with deployment descriptor and properties");
			}
		} catch (IllegalArgumentException e) {
			getLogger().error(e);
		}
	}

	protected File getDirectory(String repo) throws URISyntaxException {
		String dir = getDir();
		if (dir != null) {
			File dirFile = new File(dir);
			if (dirFile.isAbsolute()) {
				return dirFile;
			}
			return new File(getCurrentDirectory(), dir);
		}
		URIish uri = new URIish(repo);
		return new File(getCurrentDirectory(), uri.getHumanishName());
	}

	private boolean delete(File file) {
		if (file != null && file.exists() && file.isDirectory()) {
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
