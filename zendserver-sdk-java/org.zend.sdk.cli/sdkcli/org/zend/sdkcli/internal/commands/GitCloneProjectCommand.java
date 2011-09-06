package org.zend.sdkcli.internal.commands;

import java.io.File;
import java.net.URISyntaxException;
import java.text.MessageFormat;

import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.errors.JGitInternalException;
import org.eclipse.jgit.transport.URIish;
import org.zend.sdkcli.internal.options.Option;

public class GitCloneProjectCommand extends AbstractCommand {

	private static final String URI = "u";
	private static final String DIR = "d";

	@Option(opt = URI, required = true, description = "URI to clone from", argName = "uri")
	public String getURI() {
		final String value = getValue(URI);
		return value;
	}

	@Option(opt = DIR, required = false, description = "The optional directory associated with the clone operation. If the directory isn't set, a name associated with the source uri will be used.", argName = "dir")
	public String getDir() {
		final String value = getValue(DIR);
		return value;
	}

	@Override
	protected boolean doExecute() {
		CloneCommand clone = new CloneCommand();
		String uri = getURI();
		clone.setURI(uri);
		File dir = null;
		try {
			dir = getDirectory(uri);
			clone.setDirectory(dir);
		} catch (URISyntaxException e) {
			getLogger().error(e);
			return false;
		}
		try {
			clone.call();
		} catch (JGitInternalException e) {
			getLogger().error(e);
			return false;
		}
		getLogger()
				.info(MessageFormat
						.format("Project from {0} has been cloned to {1} successfully.",
								uri, dir.getName()));
		return true;
	}

	private File getDirectory(String uri2) throws URISyntaxException {
		String dir = getDir();
		if (dir != null) {
			return new File(getCurrentDirectory(), dir);
		}
		URIish uri = new URIish(getURI());
		return new File(getCurrentDirectory(), uri.getHumanishName());
	}

}
