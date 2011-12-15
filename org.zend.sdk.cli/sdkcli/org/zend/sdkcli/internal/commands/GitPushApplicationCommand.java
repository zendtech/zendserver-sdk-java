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

import org.eclipse.jgit.api.AddCommand;
import org.eclipse.jgit.api.CommitCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.PushCommand;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.JGitInternalException;
import org.eclipse.jgit.api.errors.NoFilepatternException;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.TextProgressMonitor;
import org.eclipse.jgit.storage.file.FileRepository;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.URIish;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
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
public class GitPushApplicationCommand extends AbstractCommand {

	private static final String REPO = "a";
	private static final String USER = "u";
	private static final String PASSWD = "p";
	private static final String MESSAGE = "m";
	private static final String AUTHOR = "a";

	@Option(opt = REPO, required = true, description = "Application directory path", argName = "repository")
	public File getRepo() {
		String value = getValue(REPO);
		if (value == null) {
			value = getCurrentDirectory();
		}
		return new File(value, ".git");
	}

	@Option(opt = USER, required = false, description = "User name", argName = "user")
	public String getUser() {
		String value = getValue(USER);
		return value;
	}

	@Option(opt = PASSWD, required = false, description = "Password", argName = "password")
	public String getPassword() {
		String value = getValue(PASSWD);
		return value;
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
			repo = new FileRepository(getRepo());
		} catch (IOException e) {
			getLogger().error(e);
			return false;
		}
		if (repo != null) {
			Git git = new Git(repo);

			// perform operation only if it is clone of phpCloud repository
			String repoUrl = git.getRepository().getConfig()
					.getString("remote", GitHelper.ZEND_CLOUD_REMOTE, "url");
			if (!GitHelper.ZEND_CLOUD_REMOTE.equals(GitHelper
					.getRemote(repoUrl))) {
				getLogger()
						.error("This command is available only for phpCloud git repositories.");
			}

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
			pushCommand.setRemote(GitHelper.ZEND_CLOUD_REMOTE);
			pushCommand.setProgressMonitor(new TextProgressMonitor(
					new PrintWriter(System.out)));
			CredentialsProvider credentials = getCredentials(repoUrl);
			if (credentials != null) {
				pushCommand.setCredentialsProvider(credentials);
			}
			try {
				pushCommand.call();
			} catch (JGitInternalException e) {
				getLogger().error(e);
				return false;
			} catch (InvalidRemoteException e) {
				// should not occur because phpCloud remote is available
				getLogger().error(e);
				return false;
			}

		}
		return true;
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

	private CredentialsProvider getCredentials(String repo) {
		String username = getUser();
		if (username == null) {
			URIish uri;
			try {
				uri = new URIish(repo);
				username = uri.getUser();
			} catch (URISyntaxException e) {
				// just continue
			}
			if (username == null) {
				return null;
			}
		}
		String password = getPassword();
		if (password == null) {
			password = String.valueOf(System.console().readPassword(
					"Password: "));
		}
		return new UsernamePasswordCredentialsProvider(username, password);
	}

}
