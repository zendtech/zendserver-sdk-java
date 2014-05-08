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
package org.zend.php.server.internal.ui.apache;

import org.eclipse.php.internal.server.core.Server;
import org.eclipse.php.internal.ui.wizards.IWizardHandle;
import org.eclipse.php.internal.ui.wizards.WizardControlWrapper;
import org.eclipse.php.internal.ui.wizards.WizardFragment;
import org.eclipse.php.internal.ui.wizards.WizardModel;
import org.eclipse.swt.widgets.Composite;
import org.zend.php.server.ui.ServersUI;
import org.zend.php.server.ui.types.LocalApacheType;

/**
 * @author Wojciech Galanciak, 2014
 *
 */
@SuppressWarnings("restriction")
public class LocalApacheWizardFragment extends WizardFragment {

	protected LocalApacheCompositeFragment composite;
	protected Server server;

	public Composite getComposite() {
		return composite;
	}

	@Override
	public Composite createComposite(Composite parent, IWizardHandle wizard) {
		composite = new LocalApacheCompositeFragment(parent,
				new WizardControlWrapper(wizard), false);
		return composite;
	}

	/**
	 * Called when entering the page.
	 */
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
				ServersUI.logError(e);
			}
		} else {
			ServersUI.logError(new Exception(
					Messages.LocalApacheWizardFragment_CompositeNoinitMessage));
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
				LocalApacheType.parseAttributes(composite.getServer());
			}
		} catch (Exception e) {
		}
	}

}
