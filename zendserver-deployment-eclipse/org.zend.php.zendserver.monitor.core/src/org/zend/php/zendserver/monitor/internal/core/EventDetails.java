/*******************************************************************************
 * Copyright (c) 2012 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.php.zendserver.monitor.internal.core;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.zend.php.zendserver.monitor.core.EventType;
import org.zend.php.zendserver.monitor.core.IEventDetails;
import org.zend.webapi.core.connection.data.GeneralDetails;
import org.zend.webapi.core.connection.data.Issue;

/**
 * Default implementation of {@link IEventDetails}.
 * 
 * @author Wojciech Galanciak, 2012
 * 
 */
public class EventDetails implements IEventDetails {

	private String projectName;
	private String basePath;
	private long line;
	private String sourceFile;
	private EventType type;

	protected EventDetails(String projectName, String basePath, long line,
			String sourceFile, EventType type) {
		super();
		this.projectName = projectName;
		this.basePath = basePath;
		this.line = line;
		this.sourceFile = sourceFile;
		this.type = type;
	}

	/**
	 * Create {@link IEventDetails} instance based on provided arguments.
	 * 
	 * @param projectName
	 * @param basePath
	 * @param issue
	 * @return {@link IEventDetails} instance
	 */
	public static IEventDetails create(String projectName, String basePath,
			Issue issue) {
		GeneralDetails generalDetails = issue.getGeneralDetails();
		String sourceFile = generalDetails.getSourceFile();
		long line = generalDetails.getSourceLine();
		EventType type = EventType.byRule(issue.getRule());
		return new EventDetails(projectName, basePath, line, sourceFile, type);
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.zend.php.zendserver.monitor.core.IEventDetails#getProjectRelativePath
	 * ()
	 */
	public String getProjectRelativePath() {
		// TODO consider to change the method of extracting project relative
		// path
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
	 * @see org.zend.php.zendserver.monitor.core.IEventDetails#getResource()
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
		if (getLine() == -1 || getSourceFile() == null
				|| getSourceFile().isEmpty() || getProjectName() == null) {
			return false;
		}
		return true;
	}

}
