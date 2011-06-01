package org.zend.sdk.test.workflows;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.junit.After;
import org.junit.Before;
import org.zend.sdk.test.AbstractTest;
import org.zend.sdklib.ZendApplication;
import org.zend.sdklib.internal.utils.EnvironmentUtils;
import org.zend.webapi.core.WebApiClient;

public class AbstractWorflowTest extends AbstractTest {

	protected File file;
	private String zend;

	@Before
	public void startup() throws IOException {
		final String tempDir = System.getProperty("java.io.tmpdir");
		file = new File(tempDir + File.separator + new Random().nextInt());
		file.mkdir();
		if (EnvironmentUtils.isUnderLinux() || EnvironmentUtils.isUnderMaxOSX()) {
			zend = new File("tools/zend").getCanonicalPath();
		} else {
			zend = new File("tools/zend.bat").getCanonicalPath();
		}
	}

	@After
	public void shutdown() {
		file.deleteOnExit();
	}

	public String execute(String... args) throws InterruptedException,
			IOException {
		List<String> command = new ArrayList<String>();
		command.add(zend);
		command.addAll(Arrays.asList(args));
		ProcessBuilder builder = new ProcessBuilder(command);
		builder.directory(file);
		// Map<String, String> environ = builder.environment();

		final Process process = builder.start();
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				process.getInputStream()));
		StringBuilder result = new StringBuilder();
		String line = null;
		while ((line = reader.readLine()) != null) {
			result.append(line);
			result.append("\n");
		}
		return result.toString();
	}

	protected WebApiClient getClient(String id) throws MalformedURLException {
		ZendApplication app = new ZendApplication();
		return app.getClient(id);
	}
}
