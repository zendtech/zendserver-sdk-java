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
import org.zend.php.zendserver.deployment.core.targets.TargetsManagerService;
import org.zend.php.zendserver.deployment.core.targets.ZendServerManager;
import org.zend.sdklib.manager.TargetsManager;
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
		if (target != null) {
			pref.put("targetId", targetId); //$NON-NLS-1$
			pref.put("targetHost", target.getHost().toString()); //$NON-NLS-1$
		} else {
			pref.remove("targetId"); //$NON-NLS-1$
			pref.remove("targetHost"); //$NON-NLS-1$
		}
		if (applicationURL != null) {
			pref.put("applicationURL", applicationURL); //$NON-NLS-1$
		} else {
			pref.remove("applicationURL"); //$NON-NLS-1$
		}
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
		return true;
	}

	@SuppressWarnings("restriction")
	public static Server findExistingServer(IZendTarget target) {
		try {
			Server existingServer = ServersManager.getServer(target
					.getServerName());
			if (existingServer != null) {
				return existingServer;
			}
			URL baseURL = target.getDefaultServerURL();
			Server[] servers = ServersManager.getServers();
			for (Server server : servers) {
				String zsPort = server.getAttribute(
						ZendServerManager.ZENDSERVER_PORT_KEY, "-1"); //$NON-NLS-1$
				URL serverBaseURL = new URL(server.getBaseURL());
				if (serverBaseURL.getHost().equals(baseURL.getHost())
						&& Integer.valueOf(zsPort) != -1) {
					return server;
				}
			}
			return null;
		} catch (MalformedURLException e) {
			DeploymentCore.log(e);
			// do nothing and return null
		}
		return null;
	}

	/**
	 * @param target
	 * @return base URL of PHP server associated with specified target
	 */
	@SuppressWarnings("restriction")
	public static URL getServerBaseURL(IZendTarget target) {
		Server server = findExistingServer(target);
		try {
			return new URL(server.getBaseURL());
		} catch (MalformedURLException e) {
			DeploymentCore.log(e);
		}
		return null;
	}

	/**
	 * Sets base URL of PHP server associated with specified target.
	 * 
	 * @param target
	 * @param baseUrl
	 */
	@SuppressWarnings("restriction")
	public static void setServerBaseURL(IZendTarget target, String baseUrl) {
		Server server = findExistingServer(target);
		if (server != null) {
			try {
				server.setBaseURL(baseUrl);
				ServersManager.save();
			} catch (MalformedURLException e) {
				DeploymentCore.log(e);
			}
		}
	}

	@SuppressWarnings("restriction")
	protected static Server createPHPServer(URL baseURL, IZendTarget target) {
		try {
			URL url = new URL(baseURL.getProtocol(), baseURL.getHost(),
					baseURL.getPort(), ""); //$NON-NLS-1$
			String urlString = url.toString();
			Server server = new Server(
					"Zend Target (id: " + target.getId() + " host: " + url.getHost() //$NON-NLS-1$ //$NON-NLS-2$
							+ ")", target.getDefaultServerURL().toString(), urlString, ""); //$NON-NLS-1$ //$NON-NLS-2$
			int zsPort = 10081;
			if (TargetsManager.isOpenShift(target)) {
				zsPort = 80;
			}
			server.setAttribute(ZendServerManager.ZENDSERVER_PORT_KEY,
					String.valueOf(zsPort));
			server.setAttribute(ZendServerManager.ZENDSERVER_ENABLED_KEY,
					"true"); //$NON-NLS-1$
			server.setAttribute(ZendServerManager.DEFAULT_URL_KEY,
					"/ZendServer"); //$NON-NLS-1$
			ServersManager.addServer(server);
			ServersManager.save();
			return server;
		} catch (MalformedURLException e) {
			DeploymentCore.log(e);
			// ignore, verified earlier
		}
		return null;
	}

}
