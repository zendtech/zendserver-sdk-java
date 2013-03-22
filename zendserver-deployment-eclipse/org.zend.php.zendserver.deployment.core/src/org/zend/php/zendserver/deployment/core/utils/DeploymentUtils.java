/*******************************************************************************
 * Copyright (c) 2013 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.php.zendserver.deployment.core.utils;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.php.internal.server.core.Server;
import org.eclipse.php.internal.server.core.manager.ServersManager;
import org.osgi.service.prefs.BackingStoreException;
import org.zend.php.zendserver.deployment.core.DeploymentCore;
import org.zend.php.zendserver.deployment.core.targets.EclipseSSH2Settings;
import org.zend.php.zendserver.deployment.core.targets.TargetsManagerService;
import org.zend.sdklib.target.IZendTarget;

/**
 * Utility class which exposes some useful methods related to deployment.
 * 
 * 
 * @author Wojciech Galanciak, 2013
 * 
 */
public class DeploymentUtils {

	/**
	 * Returns application URL used for last deployment.
	 * 
	 * @param projectName
	 * @return application URL
	 */
	public static String getURLFromPreferences(String projectName) {
		IProject project = ResourcesPlugin.getWorkspace().getRoot()
				.getProject(projectName);
		IEclipsePreferences pref = new ProjectScope(project)
				.getNode(DeploymentCore.PLUGIN_ID);
		return pref.get("applicationURL", null); //$NON-NLS-1$
	}

	/**
	 * Returns target on which application was deployed recently.
	 * 
	 * @param projectName
	 * @return {@link IZendTarget} instance
	 */
	public static IZendTarget getTargetFromPreferences(String projectName) {
		IProject project = ResourcesPlugin.getWorkspace().getRoot()
				.getProject(projectName);
		IEclipsePreferences pref = new ProjectScope(project)
				.getNode(DeploymentCore.PLUGIN_ID);
		String targetId = pref.get("targetId", null); //$NON-NLS-1$
		return TargetsManagerService.INSTANCE.getTargetManager().getTargetById(
				targetId);
	}

	/**
	 * Returns list of project names which are banned. Name is banned if project
	 * is created on a local server in htdocs file. In such case this name
	 * cannot be used for deployment on a local server.
	 * 
	 * @return list of banned project names
	 */
	@SuppressWarnings("restriction")
	public static List<String> getBannedNames() {
		List<String> result = new ArrayList<String>();
		Server server = ServersManager.getDefaultServer(null);
		if (server != null) {
			String docRoot = server.getDocumentRoot();
			if (docRoot != null && !docRoot.isEmpty()) {
				File root = new File(docRoot);
				File[] files = root.listFiles();
				for (File file : files) {
					if (file.isDirectory()) {
						result.add(file.getName());
					}
				}
			}
		}
		return result;
	}

	/**
	 * Updates project properties related to the last deployment.
	 * 
	 * @param project
	 * @param targetId
	 * @param applicationURL
	 */
	public static void updatePreferences(IProject project, String targetId,
			String applicationURL) {
		IZendTarget target = TargetsManagerService.INSTANCE.getTargetManager()
				.getTargetById(targetId);
		IEclipsePreferences pref = new ProjectScope(project)
				.getNode(DeploymentCore.PLUGIN_ID);
		pref.put("targetId", targetId); //$NON-NLS-1$
		pref.put("targetHost", target.getHost().toString()); //$NON-NLS-1$
		pref.put("applicationURL", applicationURL); //$NON-NLS-1$
		try {
			pref.flush();
		} catch (BackingStoreException e) {
			DeploymentCore.log(e);
		}
	}

	/**
	 * Returns default target for specified project. It is returned base on
	 * default server associated with it. If project does not have default PHP
	 * server definied in its properties then this method returns
	 * <code>null</code>.
	 * 
	 * @param project
	 * @return {@link IZendTarget} instance or <code>null</code> if there is no
	 *         default target for this project
	 */
	@SuppressWarnings("restriction")
	public static IZendTarget getDefaultTarget(IProject project) {
		Server server = ServersManager.getDefaultServer(project);
		String serverHost = server.getHost();
		if (server != null) {
			IZendTarget[] targets = TargetsManagerService.INSTANCE
					.getTargetManager().getTargets();
			if (targets != null) {
				for (IZendTarget target : targets) {
					if (serverHost.equals(target.getHost().toString())) {
						return target;
					}
				}
			}
		}
		return null;
	}

	public static boolean configureTargetSSH(String targetId) {
		IZendTarget target = TargetsManagerService.INSTANCE.getTargetManager()
				.getTargetById(targetId);
		if (target == null) {
			return false;
		}
		EclipseSSH2Settings.registerDevCloudTarget(target, true);
		return true;
	}

	/**
	 * Creates new PHP server for specified target.
	 * 
	 * @param baseURL
	 * @param targetId
	 * @return {@link Server} instance
	 */
	@SuppressWarnings("restriction")
	public static Server createPHPServer(URL baseURL, String targetId) {
		try {
			URL url = new URL(baseURL.getProtocol(), baseURL.getHost(),
					baseURL.getPort(), ""); //$NON-NLS-1$
			String urlString = url.toString();
			Server server = new Server(
					"Zend Target (id: " + targetId + " host: " + url.getHost() //$NON-NLS-1$ //$NON-NLS-2$
							+ ")", urlString, urlString, ""); //$NON-NLS-1$ //$NON-NLS-2$
			ServersManager.addServer(server);
			ServersManager.save();
			return server;
		} catch (MalformedURLException e) {
			// ignore, verified earlier
		}
		return null;
	}

	@SuppressWarnings("restriction")
	public static Server findExistingServer(URL baseURL) {
		if (baseURL == null) {
			return null;
		}
		Server[] servers = ServersManager.getServers();
		for (Server server : servers) {
			try {
				URL serverBaseURL = new URL(server.getBaseURL());
				if (serverBaseURL.getHost().equals(baseURL.getHost())) {
					if ((serverBaseURL.getPort() == baseURL.getPort())
							|| (isDefaultPort(serverBaseURL) && isDefaultPort(baseURL))) {
						return server;
					}
				}
			} catch (MalformedURLException e) {
				// ignore and continue searching
			}
		}
		return null;
	}

	private static boolean isDefaultPort(URL url) {
		int port = url.getPort();
		if (port == -1 || port == 80) {
			return true;
		}
		return false;
	}

}
