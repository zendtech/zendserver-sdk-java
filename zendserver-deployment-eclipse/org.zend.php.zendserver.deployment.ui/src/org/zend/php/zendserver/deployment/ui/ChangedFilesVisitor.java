/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.zend.php.zendserver.deployment.ui;

import java.util.*;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;

/**
 * A visitor which collects changed class files.
 */
public class ChangedFilesVisitor implements IResourceDeltaVisitor {

	private static final IProject[] ZERO_PROJECTS = new IProject[0];

	/**
	 * The collection of changed class files.
	 */
	final protected Map<IProject, List<IResourceDelta>> map = new HashMap<IProject, List<IResourceDelta>>();

	/**
	 * Answers whether children should be visited.
	 * <p>
	 * If the associated resource is a file which has been changed, record it.
	 */
	public boolean visit(IResourceDelta delta) {
		if (delta == null)
			return false;

		if (delta.getKind() == IResourceDelta.REMOVED
				|| delta.getKind() == IResourceDelta.ADDED) {
			addResourceDelta(delta);
			return true;
		}

		if (0 == (delta.getKind() & IResourceDelta.CHANGED)) {
			return false;
		}
		IResource resource = delta.getResource();
		if (resource != null) {
			switch (resource.getType()) {

			case IResource.FOLDER:
				// ignore configuration folders (.settings,.svn)
				String name = resource.getName();
				if (name.startsWith(".")) { //$NON-NLS-1$
					return false;
				}
				break;

			case IResource.FILE:
				if (0 == (delta.getFlags() & IResourceDelta.CONTENT))
					return false;

				addResourceDelta(delta);
				return false;

			default:
				return true;
			}
		}
		return true;
	}

	private void addResourceDelta(IResourceDelta delta) {
		IResource resource = delta.getResource();
		IProject project = resource.getProject();
		List<IResourceDelta> deltas = map.get(project);
		if (deltas == null) {
			deltas = new ArrayList<IResourceDelta>();
		}
		deltas.add(delta);
		map.put(project, deltas);
	}

	public IProject[] getAllProjects() {
		Set<IProject> keySet = map.keySet();
		if (keySet.size() == 0) {
			return ZERO_PROJECTS;
		}
		return (IProject[]) keySet.toArray(new IProject[keySet.size()]);
	}

	/**
	 * Answers a collection of changed files
	 */
	public IResourceDelta[] getChangedFiles(IProject project) {
		List<IResourceDelta> list = map.get(project);
		if (list == null) {
			return null;
		}
		return (IResourceDelta[]) list.toArray(new IResourceDelta[list.size()]);
	}
}