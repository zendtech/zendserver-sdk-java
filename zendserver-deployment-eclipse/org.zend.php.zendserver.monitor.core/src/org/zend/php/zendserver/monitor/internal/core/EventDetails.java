/*******************************************************************************
 * Copyright (c) 2012 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.php.zendserver.monitor.internal.core;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.IBuildpathContainer;
import org.eclipse.dltk.core.IBuildpathEntry;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.dltk.core.environment.EnvironmentPathUtils;
import org.eclipse.php.internal.core.includepath.IncludePath;
import org.eclipse.php.internal.core.includepath.IncludePathManager;
import org.eclipse.php.internal.debug.core.pathmapper.PathEntry;
import org.eclipse.php.internal.debug.core.pathmapper.PathMapper;
import org.eclipse.php.internal.debug.core.pathmapper.PathMapperRegistry;
import org.eclipse.php.internal.server.core.Server;
import org.eclipse.php.internal.server.core.manager.ServersManager;
import org.zend.php.zendserver.deployment.core.DeploymentCore;
import org.zend.php.zendserver.deployment.core.utils.DeploymentUtils;
import org.zend.php.zendserver.deployment.debug.core.config.LaunchUtils;
import org.zend.php.zendserver.monitor.core.Activator;
import org.zend.php.zendserver.monitor.core.EventType;
import org.zend.php.zendserver.monitor.core.IEventDetails;
import org.zend.sdklib.target.IZendTarget;
import org.zend.webapi.core.connection.data.GeneralDetails;
import org.zend.webapi.core.connection.data.Issue;

/**
 * Default implementation of {@link IEventDetails}.
 * 
 * @author Wojciech Galanciak, 2012
 * 
 */
public class EventDetails implements IEventDetails {

	private static final String ZENDSERVER_PORT_KEY = "zendserver_default_port"; //$NON-NLS-1$

	private String projectName;
	private String basePath;
	private long line;
	private String sourceFile;
	private EventType type;
	private String requestURL;

	protected EventDetails(String requestURL, String projectName,
			String basePath, long line, String sourceFile, EventType type) {
		super();
		this.requestURL = requestURL;
		this.projectName = projectName;
		this.basePath = basePath;
		this.line = line;
		this.sourceFile = sourceFile;
		this.type = type;
	}

	/**
	 * Create {@link IEventDetails} instance based on provided arguments.
	 * 
	 * @param requestURL
	 * @param projectName
	 * @param basePath
	 * @param issue
	 * @return {@link IEventDetails} instance
	 */
	public static IEventDetails create(String requestURL, String projectName,
			String basePath, Issue issue) {
		GeneralDetails generalDetails = issue.getGeneralDetails();
		String sourceFile = generalDetails.getSourceFile();
		long line = generalDetails.getSourceLine();
		EventType type = EventType.byRule(issue.getRule());
		return new EventDetails(requestURL, projectName, basePath, line,
				sourceFile, type);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.zend.php.zendserver.monitor.core.IEventDetails#getProjectName()
	 */
	public String getProjectName() {
		return projectName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.zend.php.zendserver.monitor.core.IEventDetails#getSourceFile()
	 */
	public String getSourceFile() {
		return sourceFile;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.zend.php.zendserver.monitor.core.IEventDetails#getLine()
	 */
	public long getLine() {
		return line;
	}
	
	public String getLocalFile() {
		String local = convertToLocalFilename();
		if (local != null) {
			IResource resource = ResourcesPlugin.getWorkspace().getRoot()
					.findMember(local);
			if (resource != null) {
				return local;
			}
		}
		if (basePath != null && sourceFile != null) {
			int index = sourceFile.indexOf(basePath);
			if (index > -1) {
				String location = sourceFile.substring(index);
				IPath path = new Path(location);
				IResource project = ResourcesPlugin.getWorkspace().getRoot()
						.findMember(projectName);
				if (project instanceof IContainer) {
					while (true) {
						IResource res = ((IContainer) project).findMember(path);
						if (res != null) {
							path = res.getFullPath();
							break;
						} else {
							path = path.removeFirstSegments(1);
						}
						if (path.segmentCount() == 0) {
							break;
						}
					}
				}
				return path.segmentCount() > 0 ? path.toString() : null;
			}
		}
		return null;
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.zend.php.zendserver.monitor.core.IEventDetails#getType()
	 */
	public EventType getType() {
		return type;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.zend.php.zendserver.monitor.core.IEventDetails#isAvailable()
	 */
	public boolean isAvailable() {
		if (getLine() == 0 || getLine() == -1 || getSourceFile() == null
				|| getSourceFile().isEmpty()
				|| "Unknown".equals(getSourceFile()) //$NON-NLS-1$
				|| getProjectName() == null) {
			return false;
		}
		return true;
	}

	@SuppressWarnings("restriction")
	private String convertToLocalFilename() {
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		if (workspace.getRoot().findMember(sourceFile) != null) {
			return sourceFile;
		}

		IZendTarget target = LaunchUtils.getTargetFromPreferences(projectName);
		Server server = null;
		if (target == null) {
			server = getServerFromUrl();
		} else {
			server = DeploymentUtils.findExistingServer(target);
		}
		String localFile = null;
		if (server != null) {
			PathMapper pathMapper = PathMapperRegistry.getByServer(server);
			PathEntry pathEntry = pathMapper.getLocalFile(sourceFile);
			if (pathEntry != null) {
				localFile = pathEntry.getResolvedPath();
			}
		}
		if (localFile == null) {
			try {
				localFile = tryGuessMapping();
			} catch (ModelException e) {
				Activator.log(e);
			}
		}
		return localFile;
	}

	private String tryGuessMapping() throws ModelException {
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IProject project = workspace.getRoot().getProject(projectName);
		if (project != null) {
			projectName = project.getName();
			IPath remotePath = new Path(sourceFile);
			int size = remotePath.segmentCount();
			for (int j = 0; j < size; j++) {
				String segment = remotePath.segment(j);
				if (segment.equals(projectName)) {
					remotePath = remotePath.removeFirstSegments(j);
					size = remotePath.segmentCount();
					for (int i = 0; i < size; i++) {
						remotePath = remotePath.removeFirstSegments(1);
						if (remotePath.segmentCount() > 0) {
							IResource res = project.getFile(remotePath);
							if (res != null && res.exists()) {
								return project.getFullPath().append(remotePath)
										.toString();
							}
						}
					}
					List<IPath> includePaths = getIncludePaths(project);
					if (includePaths.size() > 0) {
						return checkIncludePaths(includePaths);
					}
					break;
				}
			}
		}
		return sourceFile;
	}

	private String checkIncludePaths(List<IPath> includePaths) {
		IPath remotePath = new Path(sourceFile);
		int size = remotePath.segmentCount();
		for (int i = 0; i < size; i++) {
			if (remotePath.segmentCount() > 0) {
				for (IPath includePath : includePaths) {
					File file = includePath.append(remotePath).toFile();
					if (file.exists()) {
						return file.toString();
					}
				}
				remotePath = remotePath.removeFirstSegments(1);
			}
		}
		return sourceFile;
	}

	@SuppressWarnings("restriction")
	private List<IPath> getIncludePaths(IProject project) throws ModelException {
		List<IPath> includePaths = new ArrayList<IPath>();
		IncludePath[] paths = IncludePathManager.getInstance().getIncludePaths(
				project);
		for (IncludePath includePath : paths) {
			if (includePath.getEntry() instanceof IBuildpathEntry) {
				IBuildpathEntry bPath = (IBuildpathEntry) includePath
						.getEntry();
				if (bPath.getEntryKind() == IBuildpathEntry.BPE_CONTAINER
						&& !bPath.getPath().toString()
								.equals("org.eclipse.php.core.LANGUAGE")) { //$NON-NLS-1$
					IBuildpathContainer buildpathContainer = DLTKCore
							.getBuildpathContainer(bPath.getPath(),
									DLTKCore.create(project));
					if (buildpathContainer != null) {
						final IBuildpathEntry[] buildpathEntries = buildpathContainer
								.getBuildpathEntries();
						for (IBuildpathEntry buildpathEntry : buildpathEntries) {
							IPath localPath = EnvironmentPathUtils
									.getLocalPath(buildpathEntry.getPath());
							includePaths.add(localPath);
						}
					}
				}
			}
		}
		return includePaths;
	}

	@SuppressWarnings("restriction")
	private Server getServerFromUrl() {
		try {
			URL baseURL = new URL(requestURL);
			Server[] servers = ServersManager.getServers();
			for (Server server : servers) {
				URL serverBaseURL = new URL(server.getBaseURL());
				if (serverBaseURL.getHost().equals(baseURL.getHost())) {
					if ((serverBaseURL.getPort() == baseURL.getPort())
							|| (isDefaultPort(serverBaseURL) && isDefaultPort(baseURL))) {
						return server;
					} else {
						String zsPort = server.getAttribute(
								ZENDSERVER_PORT_KEY, "-1"); //$NON-NLS-1$
						if (Integer.valueOf(zsPort) == baseURL.getPort()) {
							return server;
						}
					}
				}
			}
		} catch (MalformedURLException e) {
			DeploymentCore.log(e);
			// do nothing and return null
		}
		return null;
	}

	private boolean isDefaultPort(URL url) {
		int port = url.getPort();
		if (port == -1 || port == 80) {
			return true;
		}
		return false;
	}

}
