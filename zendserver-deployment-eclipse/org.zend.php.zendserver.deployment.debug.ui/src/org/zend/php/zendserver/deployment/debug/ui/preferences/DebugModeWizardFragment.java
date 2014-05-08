/*******************************************************************************
 * Copyright (c) 2014 Zend Technologies.
 * All rights reserved. This program and the accompanying materials
 * are the copyright of Zend Technologies and is protected under
 * copyright laws of the United States.
 * You must not copy, adapt or redistribute this document for 
 * any use.
 *******************************************************************************/
package org.zend.php.zendserver.deployment.debug.ui.preferences;

import org.eclipse.php.internal.server.core.Server;
import org.eclipse.php.internal.ui.wizards.IWizardHandle;
import org.eclipse.php.internal.ui.wizards.WizardControlWrapper;
import org.eclipse.php.internal.ui.wizards.WizardFragment;
import org.eclipse.php.internal.ui.wizards.WizardModel;
import org.eclipse.swt.widgets.Composite;
import org.zend.php.zendserver.deployment.ui.Activator;

/**
 * @author Wojciech Galanciak, 2014
 *
 */
@SuppressWarnings("restriction")
public class DebugModeWizardFragment extends WizardFragment {

	protected DebugModeCompositeFragment composite;
	protected Server server = null;

	public Composite getComposite() {
		return composite;
	}

	@Override
	public Composite createComposite(Composite parent, IWizardHandle wizard) {
		composite = new DebugModeCompositeFragment(parent,
				new WizardControlWrapper(wizard), false);
		return composite;
	}

	@Override
	public void enter() {
		if (composite != null) {
			try {
				server = (Server) getWizardModel()
						.getObject(WizardModel.SERVER);
				if (server != null) {
					composite.setData(server);
				}
			} catch (Exception e) {
				Activator.log(e);
			}
		}
	}

	@Override
	public boolean hasComposite() {
		return true;
	}

	@Override
	public boolean isComplete() {
		if (composite == null) {
			return super.isComplete();
		}
		return super.isComplete() && composite.isComplete();
	}

	@Override
	public void exit() {
		try {
			if (composite != null) {
				composite.performOk();
			}
		} catch (Exception e) {
		}
	}

}