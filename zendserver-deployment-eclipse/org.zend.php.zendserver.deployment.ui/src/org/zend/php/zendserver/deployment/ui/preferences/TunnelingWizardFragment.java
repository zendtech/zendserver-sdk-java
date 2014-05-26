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

import org.eclipse.php.internal.ui.wizards.CompositeFragment;
import org.eclipse.php.internal.ui.wizards.WizardControlWrapper;
import org.eclipse.swt.widgets.Composite;
import org.zend.php.server.ui.fragments.AbstractWizardFragment;

/**
 * Wizard fragment for SSH tunneling settings.
 * 
 * @author Wojciech Galanciak, 2014
 * 
 */
@SuppressWarnings("restriction")
public class TunnelingWizardFragment extends AbstractWizardFragment {

	@Override
	protected CompositeFragment createComposite(Composite parent,
			WizardControlWrapper wrapper) {
		return new TunnelingCompositeFragment(parent, wrapper, false);
	}

}
