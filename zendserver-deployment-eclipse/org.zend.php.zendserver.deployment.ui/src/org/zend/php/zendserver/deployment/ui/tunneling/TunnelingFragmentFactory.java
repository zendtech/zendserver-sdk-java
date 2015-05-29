/*******************************************************************************
 * Copyright (c) 2014 Zend Technologies Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Zend Technologies Ltd. - initial API and implementation
 *******************************************************************************/
package org.zend.php.zendserver.deployment.ui.tunneling;

import org.eclipse.php.internal.ui.wizards.CompositeFragment;
import org.eclipse.php.internal.ui.wizards.IControlHandler;
import org.eclipse.php.internal.ui.wizards.WizardFragment;
import org.eclipse.php.ui.wizards.ICompositeFragmentFactory;
import org.eclipse.swt.widgets.Composite;

/**
 * @author Wojciech Galanciak, 2014
 * 
 */
@SuppressWarnings("restriction")
public class TunnelingFragmentFactory implements ICompositeFragmentFactory {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.php.ui.wizards.ICompositeFragmentFactory#createWizardFragment
	 * ()
	 */
	public WizardFragment createWizardFragment() {
		return new TunnelingWizardFragment();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.php.ui.wizards.ICompositeFragmentFactory#createComposite(org.
	 * eclipse.swt.widgets.Composite,
	 * org.eclipse.php.internal.ui.wizards.IControlHandler)
	 */
	public CompositeFragment createComposite(Composite parent, IControlHandler controlHandler) {
		return new TunnelingCompositeFragment(parent, controlHandler, true);
	}

}
