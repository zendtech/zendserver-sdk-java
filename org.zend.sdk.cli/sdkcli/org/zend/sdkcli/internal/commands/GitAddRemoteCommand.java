/*******************************************************************************
 * Copyright (c) Dec 13, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.sdkcli.internal.commands;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepository;
import org.eclipse.jgit.transport.RefSpec;
import org.eclipse.jgit.transport.RemoteConfig;
import org.eclipse.jgit.transport.URIish;
import org.zend.sdkcli.GitHelper;
import org.zend.sdkcli.internal.options.Option;


/**
 * 
 * Adds remote for phpCloud git repository. It performs following operations:
 * <ul>
 * <li>add new remote ({@link GitHelper#ZEND_CLOUD_REMOTE}) for provided
 * repository.</li>
 * </ul>
 * 
 * @author Wojciech Galanciak, 2011
 * 
 */
public class GitAddRemoteCommand extends AbstractCommand {

	private static final String PROJECT = "a";
	private static final String REPO = "r";

	@Option(opt = PROJECT, required = true, description = "Application directory path", argName = "project")
	public File getProject() {
		String value = getValue(PROJECT);
		if (value == null) {
			value = getCurrentDirectory();
		}
		return new File(value, ".git");
	}

	@Option(opt = REPO, required = false, description = "Repository URL", argName = "repository")
	public String getRepo() {
		String value = getValue(REPO);
		return value;
	}

	@Override
	protected boolean doExecute() {
		Repository repo = null;
		try {
			File gitDir = getProject();
			if (!gitDir.exists()) {
				getLogger().error(
						"Git repository is not available in provided location");
				return false;
			}
			repo = new FileRepository(gitDir);
		} catch (IOException e) {
			getLogger().error(e);
			return false;
		}
		if (repo != null) {
			String repoName = getReposiotryName(getRepo());
			if (repoName == null) {
				getLogger().error("Invalid repository URL :" + getRepo());
				return false;
			}
			try {
				RemoteConfig config = new RemoteConfig(repo.getConfig(),
						repoName);
				config.addURI(new URIish(getRepo()));
				String dst = Constants.R_REMOTES + config.getName();
				RefSpec refSpec = new RefSpec();
				refSpec = refSpec.setForceUpdate(true);
				refSpec = refSpec.setSourceDestination(
						Constants.R_HEADS + "*", dst + "/*"); //$NON-NLS-1$ //$NON-NLS-2$
				config.addFetchRefSpec(refSpec);
				config.update(repo.getConfig());
				repo.getConfig().save();
			} catch (URISyntaxException e) {
				getLogger().error("Invalid repository URL :" + getRepo());
				return false;
			} catch (IOException e) {
				getLogger().error(e);
				return false;
			}
		}
		return true;
	}

	private String getReposiotryName(String url) {
		try {
			URL repoURL = new URL(url);
			String host = repoURL.getHost();
			host = host.substring(0, host.lastIndexOf("."));
			return host.substring(host.lastIndexOf(".") + 1);
		} catch (MalformedURLException e) {
			getLogger().error(e);
		}
		return null;
	}

}
