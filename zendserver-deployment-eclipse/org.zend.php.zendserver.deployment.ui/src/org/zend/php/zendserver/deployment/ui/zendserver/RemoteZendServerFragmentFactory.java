/*******************************************************************************
 * Copyright (c) 2015 Zend Technologies.
 * All rights reserved. This program and the accompanying materials
 * are the copyright of Zend Technologies and is protected under
 * copyright laws of the United States.
 * You must not copy, adapt or redistribute this document for 
 * any use.
 *******************************************************************************/
package org.zend.php.zendserver.deployment.ui.zendserver;

import org.eclipse.php.internal.ui.wizards.CompositeFragment;
import org.eclipse.php.internal.ui.wizards.IControlHandler;
import org.eclipse.php.internal.ui.wizards.WizardFragment;
import org.eclipse.php.ui.wizards.ICompositeFragmentFactory;
import org.eclipse.swt.widgets.Composite;

/**
 * @author Bartlomiej Laczkowski, 2015
 * 
 *         Remote Zend Server wizard fragment factory.
 */
@SuppressWarnings("restriction")
public class RemoteZendServerFragmentFactory implements
		ICompositeFragmentFactory {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.php.internal.server.apache.ui.wizard.ICompositeFragmentFactory
	 * #createWizardFragment()
	 */
	public WizardFragment createWizardFragment() {
		return new RemoteZendServerWizardFragment();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.php.internal.server.apache.ui.wizard.ICompositeFragmentFactory
	 * #createComposite(org.eclipse.swt.widgets.Composite,
	 * org.eclipse.php.internal.server.apache.ui.IControlHandler)
	 */
	public CompositeFragment createComposite(Composite parent,
			IControlHandler controlHandler) {
		return new RemoteZendServerCompositeFragment(parent, controlHandler,
				true);
	}

}
