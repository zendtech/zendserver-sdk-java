package org.zend.sdk.test.workflows;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

import org.junit.Test;
import org.zend.webapi.core.WebApiException;
import org.zend.webapi.core.connection.data.ApplicationInfo;
import org.zend.webapi.core.connection.data.ApplicationsList;

public class TestBasicWorkflows extends AbstractWorflowTest {

	private String key = "wojtek";
	private String secret = "7986042121bef4d57120921541ccf03e2c26d6621a55ec8e88a379136ae790a0";

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
	 * <li>remove 'test' target</li>
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
		execute("create", "target", "-t", targetId, "-h",
				"http://studio-linux1.zend.net:10081", "-k", key, "-s", secret);
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
		// remove target
		execute("remove", "target", "-t", targetId);
	}

	/**
	 * Steps:
	 * <ul>
	 * <li>create new target with id = 'test'</li>
	 * <li>create project 'helloworld' in a selected location</li>
	 * <li>deploy 'helloworld' application to 'test'</li>
	 * <li>remove 'helloworld' package from 'test'</li>
	 * <li>remove 'test' target</li>
	 * </ul>
	 * 
	 * @throws IOException
	 * @throws InterruptedException
	 * @throws WebApiException
	 */
	@Test
	public void deployWorkflow() throws IOException, InterruptedException,
			WebApiException {
		String projectName = "helloworld";
		String targetId = "test";
		String subfolder = "subfolder";
		File sub = new File(file, subfolder);
		if (!sub.exists()) {
			sub.mkdir();
		}
		// create target
		execute("create", "target", "-t", targetId, "-h",
				"http://studio-linux1.zend.net:10081", "-k", key, "-s", secret);
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
		// remove target
		execute("remove", "target", "-t", targetId);
	}

	/**
	 * Steps:
	 * <ul>
	 * <li>create new target with id = 'test'</li>
	 * <li>create project 'helloworld' in a selected location</li>
	 * <li>deploy 'helloworld' application to 'test'</li>
	 * <li>redeploy 'helloworld' to 'test'</li>
	 * <li>remove 'helloworld' application from 'test'</li>
	 * <li>remove 'test' target</li>
	 * </ul>
	 * 
	 * @throws IOException
	 * @throws InterruptedException
	 * @throws WebApiException
	 */
	// @Test
	public void redeployWorkflow() throws IOException, InterruptedException,
			WebApiException {
		String projectName = "helloworld";
		String targetId = "test";
		// create target
		execute("create", "target", "-t", targetId, "-h",
				"http://studio-linux1.zend.net:10081", "-k", key, "-s", secret);
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
		// remove target
		execute("remove", "target", "-t", targetId);
	}

	/**
	 * Steps:
	 * <ul>
	 * <li>create new target with id = 'test'</li>
	 * <li>create project 'helloworld' in a selected location</li>
	 * <li>deploy 'helloworld' application to 'test'</li>
	 * <li>update 'helloworld' on 'test'</li>
	 * <li>remove 'helloworld' application from 'test'</li>
	 * <li>remove 'test' target</li>
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
		execute("create", "target", "-t", targetId, "-h",
				"http://studio-linux1.zend.net:10081", "-k", key, "-s", secret);
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
		// remove target
		execute("remove", "target", "-t", targetId);
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
		execute("create", "target", "-t", targetId, "-h",
				"http://studio-linux1.zend.net:10081", "-k", key, "-s", secret);
		checkNoAppsDeployed(targetId);
		// list targets
		assertFalse(execute("list", "targets").length() == 0);
		// update target
		execute("update", "target", "-t", targetId, "-h",
				"http://differenthost");
		// remove target
		execute("remove", "target", "-t", targetId);
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
