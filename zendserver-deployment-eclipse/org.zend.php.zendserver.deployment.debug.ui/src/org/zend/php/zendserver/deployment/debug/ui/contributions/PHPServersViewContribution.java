/*******************************************************************************
 * Copyright (c) 2014 Zend Technologies.
 * All rights reserved. This program and the accompanying materials
 * are the copyright of Zend Technologies and is protected under
 * copyright laws of the United States.
 * You must not copy, adapt or redistribute this document for 
 * any use.
 *******************************************************************************/
package org.zend.php.zendserver.deployment.debug.ui.contributions;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.php.internal.core.project.PHPNature;
import org.eclipse.php.internal.server.core.Server;
import org.zend.php.server.core.utils.ServerUtils;
import org.zend.php.server.ui.actions.IDragAndDropContribution;
import org.zend.php.zendserver.deployment.core.DeploymentNature;
import org.zend.php.zendserver.deployment.core.descriptor.DescriptorContainerManager;
import org.zend.php.zendserver.deployment.core.descriptor.IDeploymentDescriptor;
import org.zend.php.zendserver.deployment.core.descriptor.IDescriptorContainer;
import org.zend.php.zendserver.deployment.core.descriptor.ProjectType;
import org.zend.php.zendserver.deployment.debug.ui.Activator;
import org.zend.php.zendserver.deployment.debug.ui.wizards.LibraryDeploymentUtils;
import org.zend.sdklib.target.IZendTarget;

/**
 * Drag and drop action contribution for PHP Servers view. This action is
 * performed only if selected project has deployment support and it is a library
 * project.
 * 
 * @author Wojciech Galanciak, 2014
 * 
 */
@SuppressWarnings("restriction")
public class PHPServersViewContribution implements IDragAndDropContribution {

	@Override
	public void performAction(final Server server, final IProject project) {
		LibraryDeploymentUtils handler = new LibraryDeploymentUtils();
		IZendTarget target = ServerUtils.getTarget(server);
		handler.openLibraryDeploymentWizard(project, target.getId());
	}

	@Override
	public boolean isAvailable(Server server) {
		return ServerUtils.getTarget(server) != null;
	}

	@Override
	public boolean isSupported(Server server, IProject project) {
		return hasPHPNature(project) && hasDeploymentNature(project) && isLibrary(project);
	}

	private boolean hasPHPNature(IProject project) {
		try {
			return project.getNature(PHPNature.ID) != null;
		} catch (CoreException e) {
			Activator.log(e);
		}
		return false;
	}

	private boolean hasDeploymentNature(IProject project) {
		try {
			String[] natures = project.getDescription().getNatureIds();
			for (String nature : natures) {
				if (DeploymentNature.ID.equals(nature)) {
					return true;
				}
			}
		} catch (CoreException e) {
			Activator.log(e);
		}
		return false;
	}

	private boolean isLibrary(IProject project) {
		IDescriptorContainer container = DescriptorContainerManager.getService().openDescriptorContainer(project);
		IDeploymentDescriptor desc = container.getDescriptorModel();
		return desc.getType() == ProjectType.LIBRARY;
	}

}
