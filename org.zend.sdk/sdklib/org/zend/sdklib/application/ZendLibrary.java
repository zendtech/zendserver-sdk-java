/*******************************************************************************
 * Copyright (c) 2013 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.sdklib.application;

import java.io.File;
import java.net.MalformedURLException;

import org.zend.sdklib.internal.application.ZendConnection;
import org.zend.sdklib.mapping.IMappingLoader;
import org.zend.sdklib.mapping.IVariableResolver;
import org.zend.sdklib.target.ITargetLoader;
import org.zend.webapi.core.WebApiClient;
import org.zend.webapi.core.WebApiException;
import org.zend.webapi.core.connection.data.ApplicationsList;
import org.zend.webapi.core.connection.data.LibraryList;
import org.zend.webapi.core.connection.request.NamedInputStream;
import org.zend.webapi.core.progress.BasicStatus;
import org.zend.webapi.core.progress.StatusCode;

/**
 * Utility class which provides methods to perform operations on library.
 * 
 * @author Wojciech Galanciak, 2013
 * 
 */
public class ZendLibrary extends ZendConnection {

	public static final String TEMP_PREFIX = "ZendStudioDeployment";

	private IVariableResolver variableResolver;

	public ZendLibrary() {
		super();
	}

	public ZendLibrary(IMappingLoader mappingLoader) {
		super(mappingLoader);
	}

	public ZendLibrary(ITargetLoader loader) {
		super(loader);
	}

	public ZendLibrary(ITargetLoader loader, IMappingLoader mappingLoader) {
		super(loader, mappingLoader);
	}

	public void setVariableResolver(IVariableResolver variableResolver) {
		this.variableResolver = variableResolver;
	}

	/**
	 * Provides status of specified library/libraries in selected target.
	 * 
	 * @param targetId
	 * @param libaryId
	 *            - array of library id(s) for which status should be checked
	 * @return instance of {@link ApplicationsList} or <code>null</code> if
	 *         there where problems with connections or target with specified id
	 *         does not exist
	 */
	public LibraryList getStatus(String targetId, String... libaryId) {
		try {
			WebApiClient client = getClient(targetId);
			libaryId = libaryId == null ? new String[0] : libaryId;
			notifier.statusChanged(new BasicStatus(StatusCode.STARTING,
					"Library Status",
					"Retrieving Library status(es) from selected target...", -1));
			LibraryList result = client.libraryGetStatus(libaryId);
			notifier.statusChanged(new BasicStatus(StatusCode.STOPPING,
					"Library Status",
					"Library status(es) retrievied successfully. "));
			return result;
		} catch (MalformedURLException e) {
			notifier.statusChanged(new BasicStatus(StatusCode.ERROR,
					"Library Status",
					"Error duirng retrieving library status from '" + targetId
							+ "'", e));
			log.error(e);
		} catch (WebApiException e) {
			notifier.statusChanged(new BasicStatus(StatusCode.ERROR,
					"Library Status",
					"Library duirng retrieving library status from '"
							+ targetId + "'", e));
			log.error("Error duirng retrieving library status from '"
					+ targetId + "'.");
			log.error("\tpossible error: " + e.getMessage());
		}
		return null;
	}

	public LibraryList deploy(String path, String configLocation,
			String targetId) {
		if (path != null) {
			File zendPackage = new File(path);
			if (!path.endsWith(".zpk")) {
				zendPackage = createPackage(path, configLocation);
			}
			try {
				if (zendPackage != null) {
					WebApiClient client = getClient(targetId);
					notifier.statusChanged(new BasicStatus(StatusCode.STARTING,
							"Deploying", "Deploying library to the target...",
							-1));
					LibraryList result = client
							.libraryVersionDeploy(new NamedInputStream(
									zendPackage));
					notifier.statusChanged(new BasicStatus(StatusCode.STOPPING,
							"Deploying", "Library deployed successfully"));
					deleteFile(getTempFile(path));
					return result;
				}
			} catch (MalformedURLException e) {
				notifier.statusChanged(new BasicStatus(StatusCode.ERROR,
						"Deploying", "Error during deploying library to '"
								+ targetId + "'", e));
				log.error(e);
				deleteFile(getTempFile(path));
			} catch (WebApiException e) {
				notifier.statusChanged(new BasicStatus(StatusCode.ERROR,
						"Deploying", "Error during deploying library to '"
								+ targetId + "'", e));
				log.error("Error during deploying library to '" + targetId
						+ "':");
				log.error("\tpossible error: " + e.getMessage());
			}
			return null;
		}
		return null;
	}

	public LibraryList deploy(String path, String targetId) {
		return deploy(path, path, targetId);
	}
	
	public LibraryList synchronize(String path, String configLocation, int id, String targetId) {
		if (path != null) {
			File zendPackage = createPackage(path, configLocation);
			try {
				if (zendPackage != null) {
					WebApiClient client = getClient(targetId);
					notifier.statusChanged(new BasicStatus(StatusCode.STARTING,
							"Synchronizing",
							"Synchronizing library to the target...", -1));
					LibraryList result = client.librarySynchronize(id,
							new NamedInputStream(zendPackage));
					notifier.statusChanged(new BasicStatus(StatusCode.STOPPING,
							"Synchronizing",
							"Library synchronized successfully"));
					deleteFile(getTempFile(path));
					return result;
				}
			} catch (MalformedURLException e) {
				notifier.statusChanged(new BasicStatus(StatusCode.ERROR,
						"Synchronizing",
						"Error during synchronizing library to '" + targetId
								+ "'", e));
				log.error(e);
				deleteFile(getTempFile(path));
			} catch (WebApiException e) {
				notifier.statusChanged(new BasicStatus(StatusCode.ERROR,
						"Synchronizing",
						"Error during synchronizing library to '" + targetId
								+ "'", e));
				log.error("Error during synchronizing library to '" + targetId
						+ "':");
				log.error("\tpossible error: " + e.getMessage());
			}
			return null;
		}
		return null;
	}

	public LibraryList synchronize(String path, int id, String targetId) {
		return synchronize(path, path, id, targetId);
	}

	private File createPackage(String path, String configLocation) {
		File file = new File(path);
		if (!file.exists()) {
			log.error("Path does not exist: " + file);
			return null;
		}
		if (file.isDirectory()) {
			File tempFile = getTempFile(path);
			if (tempFile.isDirectory()) {
				File[] children = tempFile.listFiles();
				if (children.length == 1
						&& children[0].getName().endsWith(".zpk")) {
					return children[0];
				}
			}
			return getPackageBuilder(path, configLocation, variableResolver)
					.createDeploymentPackage(tempFile);
		} else {
			return file;
		}
	}

	private File getTempFile(String path) {
		String tempDir = System.getProperty("java.io.tmpdir");
		path = path.replace("\\", "/");
		String suffix = path.substring(path.lastIndexOf("/") + 1);
		File tempFile = new File(tempDir + File.separator + TEMP_PREFIX
				+ suffix);
		if (!tempFile.exists()) {
			tempFile.mkdir();
		}
		return tempFile;
	}

	private boolean deleteFile(File file) {
		if (file == null || !file.exists()) {
			return true;
		}
		if (file.isDirectory()) {
			String[] children = file.list();
			for (int i = 0; i < children.length; i++) {
				boolean result = deleteFile(new File(file, children[i]));
				if (!result) {
					return false;
				}
			}
		}
		return file.delete();
	}

}
