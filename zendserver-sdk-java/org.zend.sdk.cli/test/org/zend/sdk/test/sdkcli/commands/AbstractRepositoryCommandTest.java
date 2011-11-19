package org.zend.sdk.test.sdkcli.commands;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

import java.io.File;
import java.util.Random;

import org.junit.After;
import org.junit.Before;
import org.zend.sdk.test.AbstractTest;
import org.zend.sdkcli.CommandFactory;
import org.zend.sdkcli.ICommand;
import org.zend.sdkcli.internal.commands.CommandLine;
import org.zend.sdkcli.internal.commands.RepositoryAwareCommand;
import org.zend.sdklib.internal.repository.UserBasedRepositoryLoader;
import org.zend.sdklib.manager.RepositoryManager;
import org.zend.sdklib.repository.IRepositoryLoader;

public class AbstractRepositoryCommandTest extends AbstractTest {

	protected static final String FOLDER = "test/config/apps/";

	private IRepositoryLoader loader;
	private File file;

	@Before
	public void startup() {
		final String tempDir = System.getProperty("java.io.tmpdir");
		file = new File(tempDir + File.separator + new Random().nextInt());
		file.mkdir();
		loader = new UserBasedRepositoryLoader(file);
	}

	@After
	public void shutdown() {
		delete(file);
	}

	public RepositoryManager getRepositoryManager() {
		return new RepositoryManager(loader);
	}
	
	public ICommand getCommand(CommandLine cmdLine) {
		RepositoryAwareCommand command = spy((RepositoryAwareCommand) CommandFactory
				.createCommand(cmdLine));
		doReturn(getRepositoryManager()).when(command).getRepositoryManager();
		return command;
	}

}
