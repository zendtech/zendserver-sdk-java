/*******************************************************************************
 * Copyright (c) May 24, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.sdklib.application;

//import java.io.File;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.text.MessageFormat;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Random;

import org.zend.sdklib.internal.library.AbstractChangeNotifier;
import org.zend.sdklib.internal.target.SSLContextInitializer;
import org.zend.sdklib.internal.target.UserBasedTargetLoader;
import org.zend.sdklib.manager.TargetsManager;
import org.zend.sdklib.mapping.IMappingLoader;
import org.zend.sdklib.target.ITargetLoader;
import org.zend.sdklib.target.IZendTarget;
import org.zend.webapi.core.WebApiClient;
import org.zend.webapi.core.WebApiException;
import org.zend.webapi.core.connection.auth.BasicCredentials;
import org.zend.webapi.core.connection.auth.WebApiCredentials;
import org.zend.webapi.core.connection.data.ApplicationInfo;
import org.zend.webapi.core.connection.data.ApplicationsList;
import org.zend.webapi.core.connection.request.NamedInputStream;
import org.zend.webapi.core.progress.BasicStatus;
import org.zend.webapi.core.progress.StatusCode;

/**
 * Utility class which provides methods to perform operations on application.
 * 
 * @author Wojciech Galanciak, 2011
 * 
 */
public class ZendApplication extends AbstractChangeNotifier {

	private final TargetsManager manager;
	private IMappingLoader mappingLoader;

	public ZendApplication() {
		super();
		manager = new TargetsManager(new UserBasedTargetLoader());
	}

	public ZendApplication(IMappingLoader mappingLoader) {
		this();
		this.mappingLoader = mappingLoader;
	}

	public ZendApplication(ITargetLoader loader) {
		super();
		manager = new TargetsManager(loader);
	}

	public ZendApplication(ITargetLoader loader, IMappingLoader mappingLoader) {
		this(loader);
		this.mappingLoader = mappingLoader;
	}

	/**
	 * Provides information about status of specified application(s) in selected
	 * target.
	 * 
	 * @param targetId
	 * @param applicationIds
	 *            - array of application id(s) for which status should be
	 *            checked
	 * @return instance of {@link ApplicationsList} or <code>null</code> if
	 *         there where problems with connections or target with specified id
	 *         does not exist
	 */
	public ApplicationsList getStatus(String targetId, String... applicationIds) {
		try {
			WebApiClient client = getClient(targetId);
			applicationIds = applicationIds == null ? new String[0]
					: applicationIds;
			notifier.statusChanged(new BasicStatus(StatusCode.STARTING, "Application Status",
					"Retrieving Application status(es) from selected target...", -1));
			ApplicationsList result = client.applicationGetStatus(applicationIds);
			notifier.statusChanged(new BasicStatus(StatusCode.STOPPING, "Application Status",
					"Application status(es) retrievied successfully. "));
			return result;
		} catch (MalformedURLException e) {
			notifier.statusChanged(new BasicStatus(StatusCode.ERROR, "Application Status",
					"Error duirng retrieving application status from '" + targetId
							+ "'", e));
			log.error(e);
		} catch (WebApiException e) {
			notifier.statusChanged(new BasicStatus(StatusCode.ERROR, "Application Status",
					"Error duirng retrieving application status from '" + targetId
							+ "'", e));
			log.error("Error duirng retrieving application status from '"
					+ targetId + "'.");
			log.error("\tpossible error: " + e.getMessage());
		}
		return null;
	}

	/**
	 * Deploys a new application to the specified target.
	 * 
	 * @param path
	 *            - path to project location or application package
	 * @param basePath
	 *            - base path to deploy the application to. relative to the
	 *            host/vhost
	 * @param targetId
	 *            - target id
	 * @param propertiesFile
	 *            - path to properties file which consists user deployment
	 *            parameters
	 * 
	 * @param appName
	 *            - application name
	 * @param ignoreFailures
	 *            - ignore failures during staging if only some servers reported
	 *            failures
	 * @param vhostName
	 *            - the name of the vhost to use, if such a virtual host wasn't
	 *            already created by Zend Server it will be created
	 * @param defaultServer
	 *            - deploy the application on the default server; the base URL
	 *            host provided will be ignored and replaced with
	 *            <default-server>.
	 * @return instance of {@link ApplicationInfo} or <code>null</code> if there
	 *         where problems with connections or target with specified id does
	 *         not exist or there is no package/project in specified path
	 */
	public ApplicationInfo deploy(String path, String basePath,
			String targetId, String propertiesFile, String appName,
			Boolean ignoreFailures, String vhostName, Boolean defaultServer) {
		Map<String, String> userParams = null;
		if (propertiesFile != null) {
			notifier.statusChanged(new BasicStatus(StatusCode.STARTING,
					"Deploying", "Reading user parameters properites file...",
					-1));
			File propsFile = new File(propertiesFile);
			if (propsFile.exists()) {
				userParams = getUserParameters(propsFile);
			}
		}
		notifier.statusChanged(new BasicStatus(StatusCode.STOPPING,
				"Deploying", "Reading user parametes is completed."));
		return deploy(path, basePath, targetId, userParams, appName,
				ignoreFailures, vhostName, defaultServer);
	}

	/**
	 * Deploys a new application to the specified target.
	 * 
	 * @param path
	 *            - path to project location or application package
	 * @param basePath
	 *            - base path to deploy the application to. relative to
	 *            host/vhost
	 * @param targetId
	 *            - target id
	 * @param userParams
	 *            - map with user parameters (key and value)
	 * 
	 * @param appName
	 *            - application name
	 * @param ignoreFailures
	 *            - ignore failures during staging if only some servers reported
	 *            failures
	 * @param vhostName
	 *            - The virtual host to use, if such a virtual host wasn't
	 *            already created by Zend Server - it is created.
	 * @param defaultServer
	 *            - deploy the application on the default server; the base URL
	 *            host provided will be ignored and replaced with
	 *            <default-server>.
	 * @return instance of {@link ApplicationInfo} or <code>null</code> if there
	 *         where problems with connections or target with specified id does
	 *         not exist or there is no package/project in specified path
	 */
	public ApplicationInfo deploy(String path, String basePath,
			String targetId, Map<String, String> userParams, String appName,
			Boolean ignoreFailures, String vhostName, Boolean defaultServer) {
		File file = new File(path);
		if (!file.exists()) {
			log.error("Path does not exist: " + file);
			return null;
		}
		File zendPackage = null;
		// check if it is a package or a folder (project)
		File tempFile = null;
		if (file.isDirectory()) {
			final String tempDir = System.getProperty("java.io.tmpdir");
			tempFile = new File(tempDir + File.separator
					+ new Random().nextInt());
			tempFile.mkdir();
			PackageBuilder builder = null;
			builder = mappingLoader == null ? new PackageBuilder(new File(path))
					: new PackageBuilder(new File(path), mappingLoader, this);
			zendPackage = builder.createDeploymentPackage(tempFile);
		} else {
			zendPackage = file;
		}
		if (zendPackage != null) {
			try {
				String baseUrl = resolveBaseUrl(file, basePath, defaultServer,
						vhostName);
				WebApiClient client = getClient(targetId);
				notifier.statusChanged(new BasicStatus(StatusCode.STARTING,
						"Deploying", "Deploying application to the target...",
						-1));
				ApplicationInfo result = client.applicationDeploy(
						new NamedInputStream(zendPackage), baseUrl,
						ignoreFailures, userParams, appName, vhostName != null,
						defaultServer);
				notifier.statusChanged(new BasicStatus(StatusCode.STOPPING, "Deploying",
						"Application deployed successfully"));
				return result;
			} catch (MalformedURLException e) {
				notifier.statusChanged(new BasicStatus(StatusCode.ERROR, "Deploying",
						"Error during deploying application to '" + targetId + "'", e));
				log.error(e);
			} catch (WebApiException e) {
				notifier.statusChanged(new BasicStatus(StatusCode.ERROR, "Deploying",
						"Error during deploying application to '" + targetId + "'", e));
				log.error("Error during deploying application to '" + targetId
						+ "':");
				log.error("\tpossible error: " + e.getMessage());
			}
		}
		if (tempFile != null) {
			tempFile.deleteOnExit();
		}
		return null;
	}

	private String resolveBaseUrl(File path, String basePath,
			Boolean defaultServer, String vhostName)
			throws MalformedURLException {

		if (basePath == null) {
			basePath = path.getName();
		}
		if (basePath.startsWith("/")) {
			basePath = basePath.substring(1);
		}

		String url = MessageFormat.format("http://{0}/{1}",
				vhostName == null ? "default-server" : vhostName, basePath);
		log.debug("resolved url " + url);
		return url;
	}

	/**
	 * Redeploys an existing application, whether in order to fix a problem or
	 * to reset an installation.
	 * 
	 * @param targetId
	 *            - target id
	 * @param appId
	 *            - application id
	 * @param servers
	 *            - array of server id(s) on which application should be
	 *            redeployed
	 * @param ignoreFailures
	 *            - ignore failures during staging if only some servers reported
	 *            failures
	 * @return instance of {@link ApplicationInfo} or <code>null</code> if there
	 *         where problems with connections or target with specified id does
	 *         not exist or there is no application with specified id in the
	 *         target
	 */
	public ApplicationInfo redeploy(String targetId, String appId,
			String[] servers, boolean ignoreFailures) {
		try {
			WebApiClient client = getClient(targetId);
			int appIdint = Integer.parseInt(appId);
			return client
					.applicationSynchronize(appIdint, ignoreFailures, servers);
		} catch (MalformedURLException e) {
			log.error(e);
		} catch (NumberFormatException e) {
			log.error(e.getMessage());
		} catch (WebApiException e) {
			log.error("Error during redeploying application to '" + targetId
					+ "':");
			log.error("\tpossible error: " + e.getMessage());
		}
		return null;
	}

	/**
	 * Removes/undeploys an existing application.
	 * 
	 * @param targetId
	 *            - target id
	 * @param appId
	 *            - application id
	 * @return instance of {@link ApplicationInfo} or <code>null</code> if there
	 *         where problems with connections or target with specified id does
	 *         not exist or there is no application with specified id in the
	 *         target
	 */
	public ApplicationInfo remove(String targetId, String appId) {
		try {
			WebApiClient client = getClient(targetId);
			int appIdint = Integer.parseInt(appId);
			return client.applicationRemove(appIdint);
		} catch (MalformedURLException e) {
			log.error(e);
		} catch (NumberFormatException e) {
			log.error(e.getMessage());
		} catch (WebApiException e) {
			log.error("Error during removing application from '" + targetId
					+ "':");
			log.error("\tpossible error: " + e.getMessage());
		}
		return null;
	}
	
	/**
	 * Updates/redeploys an existing application.
	 * 
	 * @param path
	 *            - path to project location or application package
	 * @param targetId
	 *            - target id
	 * @param appId
	 *            - application id
	 * @param propertiesFile
	 *            - path to properties file which consists user deployment
	 *            parameters
	 * @param ignoreFailures
	 *            - ignore failures during staging if only some servers reported
	 *            failures
	 * @return instance of {@link ApplicationInfo} or <code>null</code> if there
	 *         where problems with connections or target with specified id does
	 *         not exist or there is no package/project in specified path
	 */
	public ApplicationInfo update(String path, String targetId, String appId,
			String propertiesFile, Boolean ignoreFailures) {
		Map<String, String> userParams = null;
		if (propertiesFile != null) {
			notifier.statusChanged(new BasicStatus(StatusCode.STARTING, "Updating",
					"Reading user parameters properites file...", -1));
			File propsFile = new File(propertiesFile);
			if (propsFile.exists()) {
				userParams = getUserParameters(propsFile);
			}
		}
		notifier.statusChanged(new BasicStatus(StatusCode.STOPPING, "Updating",
				"Reading user parametes is completed."));
		return update(path, targetId, appId, userParams, ignoreFailures);
	}

	/**
	 * Updates/redeploys an existing application.
	 * 
	 * @param path
	 *            - path to project location or application package
	 * @param targetId
	 *            - target id
	 * @param appId
	 *            - application id
	 * @param userParams
	 *            - map with user parameters (key and value)
	 * @param ignoreFailures
	 *            - ignore failures during staging if only some servers reported
	 *            failures
	 * @return instance of {@link ApplicationInfo} or <code>null</code> if there
	 *         where problems with connections or target with specified id does
	 *         not exist or there is no package/project in specified path
	 */
	public ApplicationInfo update(String path, String targetId, String appId,
			Map<String, String> userParams, Boolean ignoreFailures) {
		File file = new File(path);
		if (!file.exists()) {
			log.error("Path does not exist: " + file);
			return null;
		}
		File zendPackage = null;
		// check if it is a package or a folder (project)
		File tempFile = null;
		if (file.isDirectory()) {
			final String tempDir = System.getProperty("java.io.tmpdir");
			tempFile = new File(tempDir + File.separator
					+ new Random().nextInt());
			tempFile.mkdir();
			PackageBuilder builder = null;
			builder = mappingLoader == null ? new PackageBuilder(new File(path))
					: new PackageBuilder(new File(path), mappingLoader, this);
			zendPackage = builder.createDeploymentPackage(tempFile);
		} else {
			zendPackage = file;
		}
		try {
			int appIdint = Integer.parseInt(appId);
			WebApiClient client = getClient(targetId);
			notifier.statusChanged(new BasicStatus(StatusCode.STARTING, "Updating",
					"Updating application on the target...", -1));
			ApplicationInfo result = client.applicationUpdate(appIdint, new NamedInputStream(
					zendPackage), ignoreFailures, userParams);
			notifier.statusChanged(new BasicStatus(StatusCode.STOPPING, "Updating",
					"Application updated successfully"));
			return result;
		} catch (MalformedURLException e) {
			notifier.statusChanged(new BasicStatus(StatusCode.ERROR, "Updating",
					"Error during updating application on '" + targetId + "'", e));
			log.error(e);
		} catch (WebApiException e) {
			notifier.statusChanged(new BasicStatus(StatusCode.ERROR, "Updating",
					"Error during updating application on '" + targetId + "'", e));
			log.error("Error during updating application on '" + targetId
					+ "':");
			log.error("\tpossible error: " + e.getMessage());
		}
		if (tempFile != null) {
			tempFile.deleteOnExit();
		}
		return null;
	}

	/**
	 * @param targetId
	 *            - target id
	 * @return instance of a WebAPI client for specified target id. If target
	 *         does not exist, it returns <code>null</code>
	 * @throws MalformedURLException
	 */
	public WebApiClient getClient(String targetId) throws MalformedURLException {
		IZendTarget target = manager.getTargetById(targetId);
		if (target == null) {
			final String er = "Target with id '" + targetId
					+ "' does not exist.";
			log.error(er);
			throw new IllegalArgumentException(er);
		}
		WebApiCredentials credentials = new BasicCredentials(target.getKey(),
				target.getSecretKey());
		String hostname = target.getHost().toString();
		return new WebApiClient(credentials, hostname,
				SSLContextInitializer.instance.getRestletContext(), notifier);
	}

	private Map<String, String> getUserParameters(File propsFile) {
		Map<String, String> result = null;
		Properties p = new Properties();
		try {
			p.load(new FileInputStream(propsFile));
			Enumeration<?> e = p.propertyNames();
			if (e.hasMoreElements()) {
				result = new HashMap<String, String>();
			}
			while (e.hasMoreElements()) {
				String key = (String) e.nextElement();
				result.put(key, p.getProperty(key));
			}
		} catch (FileNotFoundException e) {
			log.error(e);
		} catch (IOException e) {
			log.error(e);
		}
		return result;
	}

}
