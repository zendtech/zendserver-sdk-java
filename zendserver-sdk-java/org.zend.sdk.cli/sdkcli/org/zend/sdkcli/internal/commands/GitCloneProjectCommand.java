package org.zend.sdkcli.internal.commands;

import java.io.File;
import java.io.PrintWriter;
import java.net.URISyntaxException;
import java.text.MessageFormat;

import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.errors.JGitInternalException;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.TextProgressMonitor;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.SshSessionFactory;
import org.eclipse.jgit.transport.URIish;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.zend.sdkcli.GitHelper;
import org.zend.sdkcli.internal.mapping.CliMappingLoader;
import org.zend.sdkcli.internal.options.Option;
import org.zend.sdkcli.internal.ssh.GithubSshSessionFactory;
import org.zend.sdklib.application.ZendProject;

public class GitCloneProjectCommand extends AbstractCommand {

	private static final String GITHUB_HOST = "github.com";
	private static final String REPO = "r";
	private static final String DIR = "d";
	private static final String USER = "u";
	private static final String PASSWD = "p";
	private static final String KEY = "k";
	private static final String BRANCH = "b";

	@Option(opt = REPO, required = true, description = "Repository to clone from", argName = "repository")
	public String getRepo() {
		final String value = getValue(REPO);
		return value;
	}

	@Option(opt = USER, required = false, description = "User name", argName = "user")
	public String getUser() {
		final String value = getValue(USER);
		return value;
	}

	@Option(opt = PASSWD, required = false, description = "Password", argName = "password")
	public String getPassword() {
		final String value = getValue(PASSWD);
		return value;
	}

	@Option(opt = DIR, required = false, description = "The optional directory associated with the clone operation. If the directory isn't set, a name associated with the source uri will be used", argName = "directory")
	public String getDir() {
		final String value = getValue(DIR);
		return value;
	}

	@Option(opt = KEY, required = false, description = "Path to SSH private key", argName = "key")
	public String getKey() {
		final String value = getValue(KEY);
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
		clone.setRemote(GitHelper.getRemote(repo));
		File dir = null;
		try {
			dir = getDirectory(repo);
			clone.setDirectory(dir);
		} catch (URISyntaxException e) {
			getLogger().error(e);
			return false;
		}
		clone.setProgressMonitor(new TextProgressMonitor(new PrintWriter(
				System.out)));
		getLogger().info(
				MessageFormat.format("Cloning into {0}...", dir.getName()));
		String branch = getBranch();
		if (branch != null) {
			clone.setBranch(Constants.R_HEADS + branch);
		}
		try {
			URIish uri = new URIish(repo);
			if (GITHUB_HOST.equals(uri.getHost())
					&& "git".equals(uri.getUser())) {
				GithubSshSessionFactory factory = new GithubSshSessionFactory();
				String key = getKey();
				if (key != null) {
					File privateKey = new File(key);
					if (privateKey.isDirectory() || !privateKey.exists()) {
						getLogger()
								.error(key
										+ " is not a valid path to SSH private key");
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
		}
		updateProject(dir);
		if (GitHelper.ZEND_CLOUD_REMOTE.equals(GitHelper.getRemote(repo))) {
			getLogger().info(
					"The remote name used to keep track of the phpCloud repository is: "
							+ GitHelper.ZEND_CLOUD_REMOTE);
		} else {
			getLogger().info(
					"The remote name used to keep track of the cloned repository is: "
							+ Constants.DEFAULT_REMOTE_NAME);
		}
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

	private CredentialsProvider getCredentials(String repo)
			throws URISyntaxException {
		String username = getUser();
		if (username == null) {
			URIish uri = new URIish(repo);
			username = uri.getUser();
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

	protected File getDirectory(String repo) throws URISyntaxException {
		String dir = getDir();
		if (dir != null) {
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
