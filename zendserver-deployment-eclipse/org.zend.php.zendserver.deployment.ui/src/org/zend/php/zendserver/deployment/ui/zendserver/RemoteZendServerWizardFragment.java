/*******************************************************************************
 * Copyright (c) 2015 Zend Technologies.
 * All rights reserved. This program and the accompanying materials
 * are the copyright of Zend Technologies and is protected under
 * copyright laws of the United States.
 * You must not copy, adapt or redistribute this document for 
 * any use.
 *******************************************************************************/
package org.zend.php.zendserver.deployment.ui.zendserver;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.php.internal.ui.wizards.CompositeFragment;
import org.eclipse.php.internal.ui.wizards.WizardControlWrapper;
import org.eclipse.swt.widgets.Composite;
import org.zend.php.server.ui.fragments.AbstractWizardFragment;

/**
 * @author Bartlomiej Laczkowski, 2015
 * 
 * Remote Zend Server wizard fragment.
 */
@SuppressWarnings("restriction")
public class RemoteZendServerWizardFragment extends AbstractWizardFragment {

	@Override
	protected CompositeFragment createComposite(Composite parent,
			WizardControlWrapper wrapper) {
		return new RemoteZendServerCompositeFragment(parent, wrapper, false);
	}

	@Override
	public boolean performFinish(IProgressMonitor monitor) throws CoreException {
		return super.performFinish(monitor);
	}

}
