/*******************************************************************************
 * Copyright (c) 2014 Zend Technologies.
 * All rights reserved. This program and the accompanying materials
 * are the copyright of Zend Technologies and is protected under
 * copyright laws of the United States.
 * You must not copy, adapt or redistribute this document for 
 * any use.
 *******************************************************************************/
package org.zend.php.zendserver.deployment.ui.preferences;

import org.eclipse.php.internal.server.core.Server;
import org.eclipse.php.internal.ui.wizards.CompositeFragment;
import org.eclipse.php.internal.ui.wizards.IControlHandler;
import org.eclipse.php.internal.ui.wizards.WizardFragment;
import org.eclipse.php.server.ui.types.IServerType;
import org.eclipse.php.ui.wizards.ICompositeFragmentFactory;
import org.eclipse.swt.widgets.Composite;
import org.zend.php.server.ui.types.PhpcloudServerType;

/**
 * @author Wojciech Galanciak, 2014
 * 
 */
@SuppressWarnings("restriction")
public class PhpcloudFragmentFactory implements ICompositeFragmentFactory {

	private static final String ID = "org.zend.php.zendserver.deployment.ui.preferences.PhpcloudFragmentFactory"; //$NON-NLS-1$

	public WizardFragment createWizardFragment() {
		return new PhpcloudWizardFragment();
	}

	public CompositeFragment createComposite(Composite parent,
			IControlHandler controlHandler) {
		return new PhpcloudCompositeFragment(parent, controlHandler, true);
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
		return typeId != null && PhpcloudServerType.ID.equals(typeId);
	}

	public String getId() {
		return ID;
	}

}
