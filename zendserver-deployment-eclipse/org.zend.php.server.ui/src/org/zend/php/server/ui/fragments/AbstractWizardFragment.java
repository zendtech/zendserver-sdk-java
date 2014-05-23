/*******************************************************************************
 * Copyright (c) 2014 Zend Technologies.
 * All rights reserved. This program and the accompanying materials
 * are the copyright of Zend Technologies and is protected under
 * copyright laws of the United States.
 * You must not copy, adapt or redistribute this document for 
 * any use.
 *******************************************************************************/
package org.zend.php.server.ui.fragments;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.php.internal.server.core.Server;
import org.eclipse.php.internal.ui.wizards.CompositeFragment;
import org.eclipse.php.internal.ui.wizards.CompositeWizardFragment;
import org.eclipse.php.internal.ui.wizards.IWizardHandle;
import org.eclipse.php.internal.ui.wizards.WizardControlWrapper;
import org.eclipse.php.internal.ui.wizards.WizardFragment;
import org.eclipse.php.internal.ui.wizards.WizardModel;
import org.eclipse.swt.widgets.Composite;
import org.zend.php.server.internal.ui.ServersUI;

/**
 * Abstract wizard fragment with basic implementation. It is intended to extend
 * this class to provide different implementations of {@link WizardFragment}.
 * 
 * @author Wojciech Galanciak, 2014
 * 
 */
@SuppressWarnings("restriction")
public abstract class AbstractWizardFragment extends CompositeWizardFragment {

	protected CompositeFragment composite;
	protected Server server;

	public Composite getComposite() {
		return composite;
	}

	@Override
	public Composite createComposite(Composite parent, IWizardHandle wizard) {
		composite = createComposite(parent, new WizardControlWrapper(wizard));
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
				ServersUI.logError(e);
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
	public boolean performFinish(IProgressMonitor monitor) throws CoreException {
		boolean result = super.performFinish(monitor);
		if (composite != null && composite.isComplete()) {
			result = composite.performOk();
		}
		return result;
	}

	protected void setMessage(String message, int severity) {
		((AbstractCompositeFragment) composite).setMessage(message, severity);
	}

	/**
	 * Create composite fragment specific to particular implementation of
	 * {@link AbstractWizardFragment}.
	 * 
	 * @param parent
	 *            parent composite
	 * @param wrapper
	 *            {@link WizardControlWrapper}
	 * @return
	 */
	protected abstract CompositeFragment createComposite(Composite parent,
			WizardControlWrapper wrapper);

}
