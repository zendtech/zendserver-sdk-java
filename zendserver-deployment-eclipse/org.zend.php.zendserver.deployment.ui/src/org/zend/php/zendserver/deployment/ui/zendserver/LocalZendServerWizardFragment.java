/*******************************************************************************
 * Copyright (c) 2014 Zend Technologies.
 * All rights reserved. This program and the accompanying materials
 * are the copyright of Zend Technologies and is protected under
 * copyright laws of the United States.
 * You must not copy, adapt or redistribute this document for 
 * any use. 
 *
 *******************************************************************************/
package org.zend.php.zendserver.deployment.ui.zendserver;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.php.internal.server.core.Server;
import org.eclipse.php.internal.ui.wizards.CompositeFragment;
import org.eclipse.php.internal.ui.wizards.WizardControlWrapper;
import org.eclipse.swt.widgets.Composite;
import org.zend.php.server.ui.fragments.AbstractWizardFragment;
import org.zend.php.zendserver.deployment.core.targets.ZendServerManager;
import org.zend.php.zendserver.deployment.ui.LocalTargetDetector;

/**
 * @author Wojciech Galanciak, 2014
 * 
 */
@SuppressWarnings("restriction")
public class LocalZendServerWizardFragment extends AbstractWizardFragment {

	@Override
	protected CompositeFragment createComposite(Composite parent,
			WizardControlWrapper wrapper) {
		return new LocalZendServerCompositeFragment(parent, wrapper, false);
	}

	@Override
	public boolean performFinish(IProgressMonitor monitor) throws CoreException {
		boolean result = super.performFinish(monitor);
		if (result) {
			monitor.beginTask(
					Messages.LocalZendServerCompositeFragment_DetectTitle,
					IProgressMonitor.UNKNOWN);
			final Server server = ZendServerManager.getInstance()
					.getLocalZendServer(this.server);
			if (server != null) {
				String location = server.getAttribute(
						ZendServerManager.ZENDSERVER_INSTALL_LOCATION, null);
				if (location == null || location.isEmpty()) {
					setMessage(
							Messages.LocalZendServerCompositeFragment_CannotDetectError,
							IMessageProvider.ERROR);
				} else {
					ZendServerManager.setupPathMapping(server);
					LocalTargetDetector detector = new LocalTargetDetector(
							this.server);
					detector.detect();
					if (detector.getStatus().getSeverity() != IStatus.OK) {
						setMessage(detector.getStatus().getMessage(),
								IMessageProvider.ERROR);
					}
				}
			} else {
				setMessage(
						Messages.LocalZendServerCompositeFragment_CannotDetectError,
						IMessageProvider.ERROR);
			}
			return composite.isComplete();
		}
		return result;
	}

}
