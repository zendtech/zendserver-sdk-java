/*******************************************************************************
 * Copyright (c) 2014 Zend Technologies.
 * All rights reserved. This program and the accompanying materials
 * are the copyright of Zend Technologies and is protected under
 * copyright laws of the United States.
 * You must not copy, adapt or redistribute this document for 
 * any use.
 *******************************************************************************/
package org.zend.php.zendserver.deployment.ui.servers;

import org.eclipse.php.internal.server.core.Server;
import org.eclipse.php.internal.ui.wizards.CompositeFragment;
import org.eclipse.php.internal.ui.wizards.IControlHandler;
import org.eclipse.php.internal.ui.wizards.WizardFragment;
import org.eclipse.php.server.core.types.IServerType;
import org.eclipse.php.ui.wizards.ICompositeFragmentFactory;
import org.eclipse.swt.widgets.Composite;
import org.zend.php.server.ui.types.LocalZendServerType;
import org.zend.php.server.ui.types.OpenShiftServerType;
import org.zend.php.server.ui.types.ZendServerType;

/**
 * @author Wojciech Galanciak, 2014
 * 
 */
@SuppressWarnings("restriction")
public class DeploymentFragmentFactory implements ICompositeFragmentFactory {

	private static final String ID = "org.zend.php.zendserver.deployment.ui.deploymentFragmentFactory"; //$NON-NLS-1$

	public WizardFragment createWizardFragment() {
		return new DeploymentWizardFragment();
	}

	public CompositeFragment createComposite(Composite parent,
			IControlHandler controlHandler) {
		return new DeploymentCompositeFragment(parent, controlHandler, true);
	}

	public boolean isSupported(Object element) {
		String typeId = null;
		if (element instanceof IServerType) {
			typeId = ((IServerType) element).getId();
		}
		if (element instanceof Server) {
			Server server = (Server) element;
			typeId = server.getAttribute(IServerType.TYPE, null);
		}
		return typeId != null
				&& (ZendServerType.ID.equals(typeId)
						|| LocalZendServerType.ID.equals(typeId) || OpenShiftServerType.ID
							.equals(typeId));
	}

	public String getId() {
		return ID;
	}

}
