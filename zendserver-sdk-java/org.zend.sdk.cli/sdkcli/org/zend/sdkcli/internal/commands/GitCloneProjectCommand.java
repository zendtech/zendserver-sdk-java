package org.zend.sdkcli.internal.commands;

import java.io.File;
import java.io.PrintWriter;
import java.net.URISyntaxException;
import java.text.MessageFormat;

import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.errors.JGitInternalException;
import org.eclipse.jgit.lib.TextProgressMonitor;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.URIish;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.zend.sdkcli.internal.mapping.CliMappingLoader;
import org.zend.sdkcli.internal.options.Option;
import org.zend.sdklib.application.ZendProject;

public class GitCloneProjectCommand extends AbstractCommand {

	private static final String REPO = "r";
	private static final String DIR = "d";
	private static final String USER = "u";
	private static final String PASSWD = "p";

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

	@Option(opt = DIR, required = false, description = "The optional directory associated with the clone operation. If the directory isn't set, a name associated with the source uri will be used.", argName = "directory")
	public String getDir() {
		final String value = getValue(DIR);
		return value;
	}

	@Override
	protected boolean doExecute() {
		CloneCommand clone = new CloneCommand();
		String repo = getRepo();
		clone.setURI(repo);
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
		CredentialsProvider credentials = getCredentials();
		if (credentials != null) {
			clone.setCredentialsProvider(credentials);
		}
		try {
			clone.call();
		} catch (JGitInternalException e) {
			getLogger().error(e);
			return false;
		}
		updateProject(dir);
		return true;
	}

	private void updateProject(File directory) {
		ZendProject project = new ZendProject(directory, new CliMappingLoader());
		try {
			final boolean update = project.update(null);
			if (update) {
				getLogger()
						.info("Project is updated with deployment descriptor and properties");
			}
		} catch (IllegalArgumentException e) {
			getLogger().error(e.getMessage());
		}
	}

	private CredentialsProvider getCredentials() {
		String username = getUser();
		if (username == null) {
			return null;
		}
		String password = getPassword();
		if (password == null) {
			password = String.valueOf(System.console().readPassword(
					"Password: "));
		}
		return new UsernamePasswordCredentialsProvider(username, password);
	}

	private File getDirectory(String repo) throws URISyntaxException {
		String dir = getDir();
		if (dir != null) {
			return new File(getCurrentDirectory(), dir);
		}
		URIish uri = new URIish(repo);
		return new File(getCurrentDirectory(), uri.getHumanishName());
	}

}
