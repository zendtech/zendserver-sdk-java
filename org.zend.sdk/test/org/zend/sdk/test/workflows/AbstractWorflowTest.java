package org.zend.sdk.test.workflows;

import static org.junit.Assert.fail;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.Random;

import org.junit.After;
import org.junit.Before;
import org.zend.sdk.test.AbstractTest;
import org.zend.sdklib.internal.target.UserBasedTargetLoader;
import org.zend.sdklib.internal.utils.EnvironmentUtils;
import org.zend.sdklib.manager.TargetsManager;
import org.zend.sdklib.target.IZendTarget;
import org.zend.webapi.core.WebApiClient;
import org.zend.webapi.core.connection.auth.PropertiesCredentials;
import org.zend.webapi.core.connection.auth.WebApiCredentials;

public class AbstractWorflowTest extends AbstractTest {

	private String zend;
	private WebApiCredentials credentials;

	protected File file;
	protected String host;
	protected String key;
	protected String secret;

	@Before
	public void startup() throws IOException, InterruptedException {
		final String tempDir = System.getProperty("java.io.tmpdir");
		file = new File(tempDir + File.separator + new Random().nextInt());
		file.mkdir();
		if (EnvironmentUtils.isUnderLinux() || EnvironmentUtils.isUnderMaxOSX()) {
			zend = new File("tools/zend").getCanonicalPath();
		} else {
			zend = new File("tools/zend.bat").getCanonicalPath();
		}
		credentials = readWorkflowConfiguration();
		removeAllTargets();
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

	public WebApiClient getClient(String targetId) throws MalformedURLException {
		return new WebApiClient(credentials, host);
	}

	private WebApiCredentials readWorkflowConfiguration()
			throws MalformedURLException {
		WebApiCredentials credentials = null;
		Properties p = new Properties();
		try {
			InputStream stream = new BufferedInputStream(new FileInputStream(
					new File("test/config/workflow.properties")));
			p.load(stream);
			stream.close();
			stream = new BufferedInputStream(new FileInputStream(new File(
					"test/config/workflow.properties")));
			credentials = new PropertiesCredentials(stream);
			key = credentials.getKeyName();
			secret = credentials.getSecretKey();
			stream.close();
		} catch (Exception e) {
			fail("Error during reading configuration file");
		}
		host = (String) p.get("host");
		if (host == null) {
			fail("missing entry host in configuration file");
		}
		URL hostUrl = new URL(host);
		if (hostUrl.getPort() == -1) {
			host += ":10081";
		}
		return credentials;
	}

	private void removeAllTargets() throws InterruptedException, IOException {
		TargetsManager manager = new TargetsManager(new UserBasedTargetLoader());
		IZendTarget[] targets = manager.getTargets();
		for (IZendTarget target : targets) {
			execute("delete", "target", "-t", target.getId());
		}
	}
}
