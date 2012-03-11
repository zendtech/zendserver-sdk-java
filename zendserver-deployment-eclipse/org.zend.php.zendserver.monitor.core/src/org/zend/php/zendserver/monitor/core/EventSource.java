/*******************************************************************************
 * Copyright (c) 2012 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.php.zendserver.monitor.core;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

/**
 * Represents problem source. It provides interface to get project resource
 * which is related to particular event.
 * 
 * @author Wojciech Galanciak, 2012
 * 
 */
public class EventSource {

	private String projectName;
	private String basePath;
	private long line;
	private String sourceFile;

	public EventSource(String projectName, String basePath, long line,
			String sourceFile) {
		super();
		this.projectName = projectName;
		this.basePath = basePath;
		this.line = line;
		this.sourceFile = sourceFile;
	}

	/**
	 * @return project name
	 */
	public String getProjectName() {
		return projectName;
	}

	/**
	 * @return full path of source file on a server
	 */
	public String getSourceFile() {
		return sourceFile;
	}

	/**
	 * @return line number
	 */
	public long getLine() {
		return line;
	}

	/**
	 * Creates project relative path based on source file path and project name.
	 * 
	 * @return project relative path to the source file
	 */
	public String getProjectRelativePath() {
		// TODO consider to change the method of extracting project relative
		// path
		int index = sourceFile.indexOf(basePath);
		if (index > -1) {
			String location = sourceFile.substring(index + basePath.length());
			IPath path = new Path(location);
			IPath subPath = path.removeFirstSegments(1);
			return subPath.toString();
		}
		return null;
	}

	/**
	 * Returns resource for a source file for particular project in the
	 * workspace.
	 * 
	 * @return workspace resource
	 */
	public IFile getResource() {
		IResource project = ResourcesPlugin.getWorkspace().getRoot()
				.findMember(projectName);
		if (project instanceof IContainer) {
			IResource file = ((IContainer) project)
					.findMember(getProjectRelativePath());
			if (file instanceof IFile) {
				return (IFile) file;
			}
		}
		return null;
	}

}
