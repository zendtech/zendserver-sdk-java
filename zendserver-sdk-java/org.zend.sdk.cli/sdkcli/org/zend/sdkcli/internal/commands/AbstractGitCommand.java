/*******************************************************************************
 * Copyright (c) Dec 15, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.sdkcli.internal.commands;

import java.io.File;
import java.net.URISyntaxException;

import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.SshSessionFactory;
import org.eclipse.jgit.transport.URIish;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.zend.sdkcli.internal.options.Option;
import org.zend.sdkcli.internal.ssh.GithubSshSessionFactory;

/**
 * 
 * Abstract git command which provides support for repository authentication.
 * 
 * @author Wojciech Galanciak, 2011
 * 
 */
public abstract class AbstractGitCommand extends AbstractCommand {

	protected static final String GITHUB_HOST = "github.com";

	private static final String USER = "u";
	private static final String PASSWD = "p";
	private static final String KEY = "k";

	private boolean askUsername;

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

	@Option(opt = KEY, required = false, description = "Path to SSH private key", argName = "key")
	public String getKey() {
		final String value = getValue(KEY);
		return value;
	}

	protected CredentialsProvider getCredentials(String repo) {
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
				if (askUsername()) {
					username = String.valueOf(System.console().readLine(
							"Username: "));
				}
				if (username == null) {
					return null;
				}
			}
		}
		String password = getPassword();
		return new UsernamePasswordCredentialsProvider(username, password);
	}

	protected boolean askUsername() {
		return askUsername;
	}

	protected void setAskUsername(boolean value) {
		askUsername = value;
	}

	protected boolean prepareSSHFactory() {
		GithubSshSessionFactory factory = new GithubSshSessionFactory();
		String key = getKey();
		if (key != null) {
			File privateKey = new File(key);
			if (privateKey.isDirectory() || !privateKey.exists()) {
				getLogger().error(
						key + " is not a valid path to SSH private key");
				return false;
			}
		}
		String password = getPassword();

		if (password == null) {
			password = String.valueOf(System.console().readPassword(
					"Passphrase for ssh private key: "));
		}
		factory.setPassphrase(password);
		factory.setKeyLocation(key);
		SshSessionFactory.setInstance(factory);
		return true;
	}

	private boolean shouldSavePassword() {
		String value = System.console().readLine(
				"Do you want to save a password (y|n): ");
		if (value.equals("y") || value.equals("Y")) {
			return true;
		}
		return false;
	}

}
