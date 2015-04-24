package org.zend.sdk.test.workflows;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

import org.junit.Test;
import org.zend.sdklib.internal.target.UserBasedTargetLoader;
import org.zend.sdklib.manager.TargetsManager;
import org.zend.sdklib.target.IZendTarget;
import org.zend.webapi.core.WebApiException;
import org.zend.webapi.core.connection.data.ApplicationInfo;
import org.zend.webapi.core.connection.data.ApplicationsList;

public class TestBasicWorkflows extends AbstractWorflowTest {

	/**
	 * Steps:
	 * <ul>
	 * <li>create project 'helloworld' in a current location</li>
	 * <li>create package for 'helloworld' project</li>
	 * <li>update 'helloworld' project</li>
	 * </ul>
	 * 
	 * @throws IOException
	 * @throws InterruptedException
	 * @throws WebApiException
	 */
	@Test
	public void projectWorkflow() throws IOException, InterruptedException,
			WebApiException {
		String projectName = "helloworld";
		// create project
		execute("create", "project", "-n", projectName);
		checkIfFileExists(projectName);
		// create package
		execute("create", "package", "-p", "./" + projectName);
		checkIfFileExists(projectName + "-1.0.0.0.zpk");
		// update project
		execute("update", "project", "-n", projectName);
	}

	/**
	 * Steps:
	 * <ul>
	 * <li>create new target with id = 'test'</li>
	 * <li>create project 'helloworld' in a current location</li>
	 * <li>create package for 'helloworld' project</li>
	 * <li>deploy 'helloworld' application to 'test'</li>
	 * <li>list applications on 'test'</li>
	 * <li>remove 'helloworld' application from 'test'</li>
	 * </ul>
	 * 
	 * @throws IOException
	 * @throws InterruptedException
	 * @throws WebApiException
	 */
	@Test
	public void deployWorkflow1() throws IOException, InterruptedException,
			WebApiException {
		String projectName = "helloworld";
		String targetId = "test";
		// create target
		execute("create", "target", "-t", targetId, "-h", host, "-k", key,
				"-s", secret);
		checkNoAppsDeployed(targetId);
		// create project
		execute("create", "project", "-n", projectName);
		checkIfFileExists(projectName);
		// create package
		execute("create", "package", "-p", "./" + projectName);
		checkIfFileExists(projectName + "-1.0.0.0.zpk");
		// deploy application
		execute("deploy", "application", "-t", targetId, "-p",
				"./helloworld-1.0.0.0.zpk", "-b", "http://myhost/helloworld",
				"-c");
		assertFalse("No applications were found".equals(execute("list",
				"applications", "-t", targetId)));
		checkAppsDeployed(targetId);
		removeApplications(targetId);
	}

	/**
	 * Steps:
	 * <ul>
	 * <li>create new target with id = 'test'</li>
	 * <li>create project 'helloworld' in a selected location</li>
	 * <li>deploy 'helloworld' application to 'test'</li>
	 * <li>remove 'helloworld' package from 'test'</li>
	 * </ul>
	 * 
	 * @throws IOException
	 * @throws InterruptedException
	 * @throws WebApiException
	 */
	@Test
	public void deployWorkflow2() throws IOException, InterruptedException,
			WebApiException {
		String projectName = "helloworld";
		String targetId = "test";
		String subfolder = "subfolder";
		File sub = new File(file, subfolder);
		if (!sub.exists()) {
			sub.mkdir();
		}
		// create target
		execute("create", "target", "-t", targetId, "-h", host, "-k", key,
				"-s", secret);
		checkNoAppsDeployed(targetId);
		// create project
		execute("create", "project", "-n", projectName, "-d", "./" + subfolder);
		checkIfFileExists(subfolder + File.separator + projectName);
		// deploy application
		execute("deploy", "application", "-t", targetId, "-p", "./" + subfolder
				+ File.separator + projectName, "-b",
				"http://myhost/helloworld", "-c");
		checkAppsDeployed(targetId);
		removeApplications(targetId);
	}
	
	/**
	 * Steps:
	 * <ul>
	 * <li>create new target with id = 'test'</li>
	 * <li>deploy drupal application to 'test'</li>
	 * <li>remove drupal package from 'test'</li>
	 * </ul>
	 * 
	 * @throws IOException
	 * @throws InterruptedException
	 * @throws WebApiException
	 */
	@Test
	public void deployExistingAppWorkflow() throws IOException, InterruptedException,
			WebApiException {
		String targetId = "test";
		File drupalPackage = new File("test/config/apps/drupal-6.19.zpk");
		File params = new File("test/config/apps/drupal.properties");
		// create target
		execute("create", "target", "-t", targetId, "-h", host, "-k", key,
				"-s", secret);
		checkNoAppsDeployed(targetId);
		// deploy application
		execute("deploy", "application", "-t", targetId, "-p", drupalPackage.getCanonicalPath(), "-b",
				"http://myhost/helloworld", "-c", "-m", params.getCanonicalPath());
		checkAppsDeployed(targetId);
		removeApplications(targetId);
	}

	/**
	 * Steps:
	 * <ul>
	 * <li>create new target with id = 'test'</li>
	 * <li>create project 'helloworld' in a selected location</li>
	 * <li>deploy 'helloworld' application to 'test'</li>
	 * <li>redeploy 'helloworld' to 'test'</li>
	 * <li>remove 'helloworld' application from 'test'</li>
	 * </ul>
	 * 
	 * @throws IOException
	 * @throws InterruptedException
	 * @throws WebApiException
	 */
	@Test
	public void redeployWorkflow() throws IOException, InterruptedException,
			WebApiException {
		String projectName = "helloworld";
		String targetId = "test";
		// create target
		execute("create", "target", "-t", targetId, "-h", host, "-k", key,
				"-s", secret);
		checkNoAppsDeployed(targetId);
		// create project
		execute("create", "project", "-n", projectName);
		// deploy application
		execute("deploy", "application", "-t", targetId, "-p", "./helloworld",
				"-b", "http://myhost/helloworld", "-c");
		// get helloworld id
		int id = getId(targetId, projectName);
		assertTrue(id != -1);
		// redeploy application
		execute("redeploy", "application", "-t", targetId, "-a",
				String.valueOf(id));
		checkAppsDeployed(targetId);
		removeApplications(targetId);
	}

	/**
	 * Steps:
	 * <ul>
	 * <li>create new target with id = 'test'</li>
	 * <li>create project 'helloworld' in a selected location</li>
	 * <li>deploy 'helloworld' application to 'test'</li>
	 * <li>update 'helloworld' on 'test'</li>
	 * <li>remove 'helloworld' application from 'test'</li>
	 * </ul>
	 * 
	 * @throws IOException
	 * @throws InterruptedException
	 * @throws WebApiException
	 */
	@Test
	public void updateWorkflow() throws IOException, InterruptedException,
			WebApiException {
		String projectName = "helloworld";
		String targetId = "test";
		// create target
		execute("create", "target", "-t", targetId, "-h", host, "-k", key,
				"-s", secret);
		checkNoAppsDeployed(targetId);
		// create project
		execute("create", "project", "-n", projectName);
		// deploy application
		execute("deploy", "application", "-t", targetId, "-p", "./helloworld",
				"-b", "http://myhost/helloworld", "-c");
		// get helloworld id
		int id = getId(targetId, projectName);
		assertTrue(id != -1);
		System.out.println(id);
		// update application
		execute("update", "application", "-t", targetId, "-a", "-p",
				"./helloworld", "-a", String.valueOf(id));
		checkAppsDeployed(targetId);
		removeApplications(targetId);
	}

	/**
	 * Steps:
	 * <ul>
	 * <li>create new target with id = 'test'</li>
	 * <li>list targets</li>
	 * <li>update 'test' target</li>
	 * <li>remove 'test' target</li>
	 * </ul>
	 * 
	 * @throws IOException
	 * @throws InterruptedException
	 * @throws WebApiException
	 */
	@Test
	public void targetWorkflow() throws IOException, InterruptedException,
			WebApiException {
		String targetId = "test";
		// create target
		execute("create", "target", "-t", targetId, "-h", host, "-k", key,
				"-s", secret);
		checkNoAppsDeployed(targetId);
		// list targets
		assertFalse(execute("list", "targets").length() == 0);
		// update target
		execute("update", "target", "-t", targetId, "-k", "sdk", "-s",
				"e3afe53934138d398a8a6e6b2ff7fb151929d150355e49773d461d207ec9e698");
		TargetsManager manager = new TargetsManager(new UserBasedTargetLoader());
		IZendTarget[] targets = manager.getTargets();
		assertTrue(targets.length == 1);
		assertEquals("sdk", targets[0].getKey());
		// remove target
		execute("delete", "target", "-t", targetId);
	}

	/**
	 * Steps:
	 * <ul>
	 * <li>detect localhost target, id = 'test'</li>
	 * <li>list targets</li>
	 * <li>remove 'test' target</li>
	 * </ul>
	 * 
	 * @throws IOException
	 * @throws InterruptedException
	 * @throws WebApiException
	 */
	//@Test
	public void targetDetectonWorkflow() throws IOException,
			InterruptedException, WebApiException {
		String targetId = "test";
		// detect target
		execute("detect", "target", "-t", targetId);
		// list targets
		assertFalse(execute("list", "targets").equals("No Available Zend Targets."));
		TargetsManager manager = new TargetsManager(new UserBasedTargetLoader());
		IZendTarget[] targets = manager.getTargets();
		assertTrue(targets.length == 1);
		assertEquals("sdk", targets[0].getKey());
		// remove target
		execute("delete", "target", "-t", targetId);
	}

	private void removeApplications(String targetId) throws WebApiException,
			InterruptedException, IOException {
		ApplicationsList list = getClient(targetId).applicationGetStatus();
		assertNotNull(list.getApplicationsInfo());
		List<ApplicationInfo> apps = list.getApplicationsInfo();
		for (ApplicationInfo applicationInfo : apps) {
			int id = applicationInfo.getId();
			execute("remove", "application", "-t", targetId, "-id",
					String.valueOf(id));
		}
	}

	private int getId(String targetId, String projectName)
			throws MalformedURLException, WebApiException {
		ApplicationsList list = getClient(targetId).applicationGetStatus();
		assertNotNull(list.getApplicationsInfo());
		List<ApplicationInfo> apps = list.getApplicationsInfo();
		for (ApplicationInfo applicationInfo : apps) {
			if (applicationInfo.getAppName().equals(projectName))
				return applicationInfo.getId();
		}
		return -1;
	}

	private void checkNoAppsDeployed(String targetId) throws WebApiException,
			MalformedURLException {
		// check if there is not any application already deployed
		assertNull(getClient(targetId).applicationGetStatus()
				.getApplicationsInfo());
	}

	private void checkAppsDeployed(String targetId) throws WebApiException,
			MalformedURLException {
		// check if there is any application already deployed
		assertNotNull(getClient(targetId).applicationGetStatus()
				.getApplicationsInfo());
	}

	private void checkIfFileExists(String projectName) {
		File project = new File(file, projectName);
		assertTrue(project.exists());
	}

}
