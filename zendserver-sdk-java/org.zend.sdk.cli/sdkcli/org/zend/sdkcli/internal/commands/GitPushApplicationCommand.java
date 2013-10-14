/*******************************************************************************
 * Copyright (c) Dec 11, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.sdkcli.internal.commands;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URISyntaxException;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.Set;

import org.eclipse.jgit.api.AddCommand;
import org.eclipse.jgit.api.CommitCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.PushCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.JGitInternalException;
import org.eclipse.jgit.api.errors.NoFilepatternException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.TextProgressMonitor;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.PushResult;
import org.eclipse.jgit.transport.RemoteRefUpdate;
import org.eclipse.jgit.transport.TrackingRefUpdate;
import org.eclipse.jgit.transport.URIish;
import org.zend.sdkcli.GitHelper;
import org.zend.sdkcli.internal.options.Option;


/**
 * 
 * Pushes all changes to phpCloud git repository. It performs following
 * operations:
 * <ul>
 * <li>add all new files to the local git repository,</li>
 * <li>commit all changes (removed and added files, modifications in existing
 * files) to the local repository,</li>
 * <li>push local repository changes to the phpCloud remote (called
 * {@link GitHelper#ZEND_CLOUD_REMOTE}).</li>
 * </ul>
 * 
 * @author Wojciech Galanciak, 2011
 * 
 */
public class GitPushApplicationCommand extends AbstractGitCommand {

	private static final String GITHUB_HOST = "github.com";
	private static final String REPO = "a";
	private static final String MESSAGE = "m";
	private static final String AUTHOR = "i";
	private static final String REMOTE = "r";

	@Option(opt = REPO, required = false, description = "Application directory path", argName = "repository")
	public File getRepo() {
		String value = getValue(REPO);
		if (value == null) {
			value = getCurrentDirectory();
		}
		return new File(value, ".git");
	}

	@Option(opt = MESSAGE, required = false, description = "Message which will be used for commit operation", argName = "message")
	public String getMessage() {
		String value = getValue(MESSAGE);
		return value != null ? value
				: "Commit changes to phpCloud git repository";
	}

	@Option(opt = AUTHOR, required = false, description = "Author information which will be used for commit operation", argName = "message")
	public String getAuthor() {
		String value = getValue(AUTHOR);
		return value;
	}

	@Option(opt = REMOTE, required = false, description = "Remote", argName = "remote")
	public String getRemote() {
		String value = getValue(REMOTE);
		return value;
	}

	@Override
	protected boolean doExecute() {
		Repository repo = null;
		try {
			File gitDir = getRepo();
			if (!gitDir.exists()) {
				getLogger().error(
						"Git repository is not available in provided location");
				return false;
			}
			repo = FileRepositoryBuilder.create(getRepo());
		} catch (IOException e) {
			getLogger().error(e);
			return false;
		}
		if (repo != null) {
			Git git = new Git(repo);

			String remote = doGetRemote(repo);
			if (remote == null) {
				getLogger().error("Invalid remote value: " + getRemote());
				return false;
			}

			// perform operation only if it is clone of phpCloud repository
			String repoUrl = git.getRepository().getConfig()
					.getString("remote", remote, "url");

			AddCommand addCommand = git.add();
			addCommand.setUpdate(false);
			// add all new files
			addCommand.addFilepattern(".");
			try {
				addCommand.call();
			} catch (NoFilepatternException e) {
				// should not occur because '.' is used
				getLogger().error(e);
				return false;
			} catch (GitAPIException e) {
				getLogger().error(e);
				return false;
			}

			CommitCommand commitCommand = git.commit();
			// automatically stage files that have been modified and deleted
			commitCommand.setAll(true);
			PersonIdent ident = getPersonalIdent(repoUrl);
			if (ident == null) {
				getLogger().error(
						"Invalid author information provided: " + getAuthor());
				return false;
			}
			commitCommand.setAuthor(ident);
			commitCommand.setInsertChangeId(true);
			commitCommand.setMessage(getMessage());
			try {
				commitCommand.call();
			} catch (Exception e) {
				getLogger().error(e);
				return false;
			}

			// at the end push all changes
			PushCommand pushCommand = git.push();
			pushCommand.setPushAll();
			pushCommand.setRemote(remote);
			pushCommand.setProgressMonitor(new TextProgressMonitor(
					new PrintWriter(System.out)));

			try {
				URIish uri = new URIish(repoUrl);
				if (GITHUB_HOST.equals(uri.getHost())
						&& "git".equals(uri.getUser())) {
					if (!prepareSSHFactory()) {
						return false;
					}
				} else {
					CredentialsProvider credentials = getCredentials(repoUrl);
					if (credentials != null) {
						pushCommand.setCredentialsProvider(credentials);
					}
				}
				Iterable<PushResult> result = pushCommand.call();
				for (PushResult pushResult : result) {
					pushResult.getAdvertisedRefs();
					Collection<RemoteRefUpdate> updates = pushResult
							.getRemoteUpdates();
					for (RemoteRefUpdate remoteRefUpdate : updates) {
						TrackingRefUpdate trackingRefUpdate = remoteRefUpdate
								.getTrackingRefUpdate();
						getLogger()
								.info(MessageFormat.format(
										"Remote name: {0}, status: {1}",
										remoteRefUpdate.getRemoteName(),
										remoteRefUpdate.getStatus().toString()));
						getLogger().info(
								MessageFormat.format(
										"Remote name: {0}, result: {1}",
										trackingRefUpdate.getRemoteName(),
										trackingRefUpdate.getResult()
												.toString()));
					}
				}
			} catch (JGitInternalException e) {
				getLogger().error(e);
				return false;
			} catch (InvalidRemoteException e) {
				// should not occur because selected remote is available
				getLogger().error(e);
				return false;
			} catch (URISyntaxException e) {
				getLogger().error(e);
				return false;
			} catch (TransportException e) {
				getLogger().error(e);
				return false;
			} catch (GitAPIException e) {
				getLogger().error(e);
				return false;
			}

		}
		return true;
	}

	private String doGetRemote(Repository repo) {
		String remote = getRemote();
		if (remote == null) {
			remote = GitHelper.ZEND_CLOUD_REMOTE;
		}
		Set<String> remotes = repo.getConfig().getSubsections("remote");
		if (remotes.contains(remote)) {
			return remote;
		}
		return null;
	}

	private PersonIdent getPersonalIdent(String repo) {
		String name = null;
		String email = null;
		String value = getValue(AUTHOR);
		if (value != null) {
			String[] parts = value.split(":");
			if (parts.length == 2) {
				name = parts[0];
				email = parts[1];
			} else if (parts.length == 1) {
				name = parts[0];
			} else {
				return null;
			}
		} else {
			name = getUser();
			if (name == null) {
				URIish uri;
				try {
					uri = new URIish(repo);
					name = uri.getUser();
				} catch (URISyntaxException e) {
					// just continue
				}
				if (name == null) {
					name = "unknown";
				}
			}
		}
		return new PersonIdent(name, email);
	}

}
