/*******************************************************************************
 * Copyright (c) 2013 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.php.zendserver.deployment.ui;

import java.io.IOException;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.IBuildpathEntry;
import org.zend.php.zendserver.deployment.core.DeploymentNature;
import org.zend.php.zendserver.deployment.core.descriptor.DescriptorContainerManager;
import org.zend.php.zendserver.deployment.core.descriptor.IDeploymentDescriptor;
import org.zend.php.zendserver.deployment.core.descriptor.IDescriptorContainer;
import org.zend.sdklib.mapping.IMappingEntry.Type;
import org.zend.sdklib.mapping.IMappingModel;
import org.zend.sdklib.mapping.MappingModelFactory;

/**
 * Listener responsible for updating deployment.properties file according to
 * changes in a project structure.
 * 
 * @author Wojciech Galanciak, 2013
 * 
 */
public class MappingChangeListener implements IResourceChangeListener {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.core.resources.IResourceChangeListener#resourceChanged(org
	 * .eclipse.core.resources.IResourceChangeEvent)
	 */
	public void resourceChanged(IResourceChangeEvent event) {
		// on close event - exit
		IResourceDelta delta = event.getDelta();
		if (delta == null) {
			return;
		}
		// finally - go over the changed files and submit changes
		ChangedFilesVisitor visitor = new ChangedFilesVisitor();
		try {
			delta.accept(visitor);
		} catch (CoreException e) {
			Activator.log(e);
			return;
		}
		IProject[] allProjects = visitor.getAllProjects();
		for (IProject project : allProjects) {
			if (!project.isAccessible() || !hasDeploymentNature(project)) {
				continue;
			}
			IMappingModel model = MappingModelFactory
					.createDefaultModel(project.getLocation().toFile());
			if (model == null) {
				continue;
			}
			IDescriptorContainer container = DescriptorContainerManager
					.getService().openDescriptorContainer(project);
			IDeploymentDescriptor desc = container.getDescriptorModel();
			String scriptsDir = desc.getScriptsRoot();
			if (scriptsDir == null) {
				if (container.getFile().getParent().findMember("scripts") != null) { //$NON-NLS-1$
					scriptsDir = "scripts"; //$NON-NLS-1$
				}
			}
			IResourceDelta[] changedFiles = visitor.getChangedFiles(project);
			boolean isDirty = false;
			for (IResourceDelta d : changedFiles) {
				if (d.getResource().getProjectRelativePath()
						.segmentCount() == 0) {
					// means that this is more complex operation, e.g. project creation
					return;
				}
				if (isExcluded(d, project)) {
					continue;
				}
				switch (d.getKind()) {
				case IResourceDelta.REMOVED:
					if (handleRemoved(model, scriptsDir, d) && !isDirty) {
						isDirty = true;
					}
					break;
				case IResourceDelta.ADDED:
					if (handleAdded(model, scriptsDir, d) && !isDirty) {
						isDirty = true;
					}
					break;
				case IResourceDelta.CHANGED:
					break;
				}
			}
			if (isDirty) {
				try {
					model.store();
				} catch (IOException e) {
					Activator.log(e);
				}
			}
		}
	}

	private boolean isExcluded(IResourceDelta d, IProject project) {
		IResource res = d.getResource();
		if (res != null) {
			return isExcludedFromBuildpath(project,
					res.getProjectRelativePath());
		}
		return false;
	}

	private boolean handleAdded(IMappingModel model, String scriptsDir,
			IResourceDelta delta) {
		IResource resource = delta.getResource();
		if (checkIfValid(resource, model, scriptsDir)) {
			String relativePath = resource.getProjectRelativePath().toString();
			if (!relativePath.isEmpty()) {
				return model.addMapping(IMappingModel.APPDIR, Type.INCLUDE,
						relativePath, false);
			}
		}
		return false;
	}

	private boolean handleRemoved(IMappingModel model, String scriptsDir,
			IResourceDelta delta) {
		IResource resource = delta.getResource();
		if (checkIfValid(resource, model, scriptsDir)) {
			String relativePath = resource.getProjectRelativePath().toString();
			return model.removeMapping(IMappingModel.APPDIR, Type.INCLUDE,
					relativePath);
		}
		return false;
	}

	private boolean checkIfValid(IResource resource, IMappingModel model,
			String scriptsDir) {
		if (resource != null) {
			String name = resource.getName();
			if (checkHiddenFile(resource)) {
				return false;
			}
			if (name.equals(MappingModelFactory.DEPLOYMENT_PROPERTIES)) {
				return false;
			}
			if (name.equals(DescriptorContainerManager.DESCRIPTOR_PATH)) {
				return false;
			}

			try {
				String[] folders = model.getFolders(resource
						.getProjectRelativePath().toString());
				if (folders != null && folders.length > 0) {
					return false;
				}
			} catch (IOException e) {
				// should not appear
			}

			IProject project = resource.getProject();
			if (project != null) {
				IPath resPath = resource.getProjectRelativePath();
				if (resPath.segmentCount() > 0
						&& (resource.getName().equals(scriptsDir) || resPath
								.segment(0).equals(scriptsDir))) {
					return false;
				}
			}
			return true;
		}
		return false;
	}

	private boolean checkHiddenFile(IResource resource) {
		String name = resource.getName();
		if (name.startsWith(".")) { //$NON-NLS-1$
			return true;
		} else {
			IResource parent = resource.getParent();
			if (parent == resource.getProject()) {
				return false;
			} else {
				return checkHiddenFile(parent);
			}
		}
	}

	private boolean hasDeploymentNature(IProject project) {
		String[] natures = null;
		try {
			natures = project.getDescription().getNatureIds();
		} catch (CoreException e) {
			return false;
		}
		for (String nature : natures) {
			if (DeploymentNature.ID.equals(nature)) {
				return true;
			}
		}
		return false;
	}

	public static boolean isExcludedFromBuildpath(IProject project, IPath path) {
		IBuildpathEntry[] buildpathEntries = DLTKCore.create(project)
				.readRawBuildpath();
		for (int i = 0; i < buildpathEntries.length; i++) {
			IBuildpathEntry curr = buildpathEntries[i];
			if (curr.getEntryKind() == IBuildpathEntry.BPE_SOURCE
					&& curr.getPath()
							.equals(project.getProject().getFullPath())) {
				IPath[] exclusionPatterns = curr.getExclusionPatterns();
				for (IPath p : exclusionPatterns) {
					if (path.matchingFirstSegments(p) == p.segmentCount()) {
						return true;
					}
				}
				break;
			}
		}
		return false;
	}

}
