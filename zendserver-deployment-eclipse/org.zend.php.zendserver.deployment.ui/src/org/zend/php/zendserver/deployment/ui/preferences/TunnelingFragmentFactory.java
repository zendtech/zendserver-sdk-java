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
package org.zend.php.zendserver.deployment.ui.preferences;

import org.eclipse.php.internal.server.core.Server;
import org.eclipse.php.internal.ui.wizards.CompositeFragment;
import org.eclipse.php.internal.ui.wizards.IControlHandler;
import org.eclipse.php.internal.ui.wizards.WizardFragment;
import org.eclipse.php.server.ui.types.IServerType;
import org.eclipse.php.ui.wizards.ICompositeFragmentFactory;
import org.eclipse.swt.widgets.Composite;

/**
 * @author Wojciech Galanciak, 2014
 * 
 */
@SuppressWarnings("restriction")
public class TunnelingFragmentFactory implements ICompositeFragmentFactory {

	private static final String ID = "org.zend.php.zendserver.deployment.ui.TunnelingFragmentFactory"; //$NON-NLS-1$

	public WizardFragment createWizardFragment() {
		return new TunnelingWizardFragment();
	}

	public CompositeFragment createComposite(Composite parent,
			IControlHandler controlHandler) {
		return new TunnelingCompositeFragment(parent, controlHandler, true);
	}

	public boolean isSupported(Object element) {
		return element instanceof IServerType || element instanceof Server;
	}

	public String getId() {
		return ID;
	}

}
