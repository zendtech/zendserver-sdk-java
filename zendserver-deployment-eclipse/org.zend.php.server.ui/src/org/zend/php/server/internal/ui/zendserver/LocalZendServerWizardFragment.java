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
package org.zend.php.server.internal.ui.zendserver;

import org.eclipse.php.internal.server.core.Server;
import org.eclipse.php.internal.ui.wizards.IWizardHandle;
import org.eclipse.php.internal.ui.wizards.WizardControlWrapper;
import org.eclipse.php.internal.ui.wizards.WizardFragment;
import org.eclipse.php.internal.ui.wizards.WizardModel;
import org.eclipse.swt.widgets.Composite;
import org.zend.php.server.internal.ui.ServersUI;
import org.zend.php.server.internal.ui.apache.LocalApacheCompositeFragment;
import org.zend.php.server.ui.types.LocalApacheType;

/**
 * @author Wojciech Galanciak, 2014
 *
 */
public class LocalZendServerWizardFragment extends WizardFragment {

	protected LocalApacheCompositeFragment comp;
	protected Server server = null;

	public LocalZendServerWizardFragment() {
	}

	public Composite getComposite() {
		return comp;
	}

	public Composite createComposite(Composite parent, IWizardHandle wizard) {
		comp = new LocalApacheCompositeFragment(parent,
				new WizardControlWrapper(wizard), false);
		return comp;
	}

	/**
	 * Called when entering the page
	 */
	public void enter() {
		if (comp != null) {
			try {
				server = (Server) getWizardModel()
						.getObject(WizardModel.SERVER);
				if (server != null) {
					comp.setData(server);
				}
			} catch (Exception e) {
				ServersUI.logError(e);
			}
		} else {
			// TODO handle it
		}
	}
	
	@Override
	public boolean hasComposite() {
		return true;
	}

	public boolean isComplete() {
		if (comp == null) {
			return super.isComplete();
		}
		return super.isComplete() && comp.isComplete();
	}

	public void exit() {
		try {
			if (comp != null) {
				comp.performOk();
				LocalApacheType.parseAttributes(comp.getServer());
			}
		} catch (Exception e) {
		}
	}

}
