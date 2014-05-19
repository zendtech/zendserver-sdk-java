/*******************************************************************************
 * Copyright (c) 2014 Zend Technologies.
 * All rights reserved. This program and the accompanying materials
 * are the copyright of Zend Technologies and is protected under
 * copyright laws of the United States.
 * You must not copy, adapt or redistribute this document for 
 * any use.
 *******************************************************************************/
package org.zend.php.zendserver.deployment.ui.preferences;

import java.util.ArrayList;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.php.internal.ui.wizards.CompositeFragment;
import org.eclipse.php.internal.ui.wizards.WizardControlWrapper;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.zend.php.server.ui.fragments.AbstractWizardFragment;
import org.zend.php.zendserver.deployment.core.targets.TargetsManagerService;
import org.zend.sdklib.internal.target.ZendTarget;
import org.zend.sdklib.manager.TargetException;
import org.zend.sdklib.manager.TargetsManager;
import org.zend.sdklib.target.IZendTarget;
import org.zend.sdklib.target.LicenseExpiredException;

/**
 * @author Wojciech Galanciak, 2014
 * 
 */
@SuppressWarnings("restriction")
public class DeploymentWizardFragment extends AbstractWizardFragment {

	@Override
	protected CompositeFragment createComposite(Composite parent,
			WizardControlWrapper wrapper) {
		return new DeploymentCompositeFragment(parent, wrapper, false);
	}

	@Override
	public void performFinish(IProgressMonitor monitor) throws CoreException {
		super.performFinish(monitor);
		IZendTarget target = ((DeploymentCompositeFragment) getComposite())
				.getTarget();
		performTesting(target, monitor);
	}

	private void performTesting(IZendTarget target, IProgressMonitor monitor) {
		monitor.beginTask(
				Messages.DeploymentCompositeFragment_TestingConnection,
				IProgressMonitor.UNKNOWN);
		TargetsManager manager = TargetsManagerService.INSTANCE
				.getTargetManager();
		IStatus status = null;
		TargetConnectionTester tester = new TargetConnectionTester();
		IZendTarget[] targets = manager.getTargets();
		for (IZendTarget t : targets) {
			if (t.getHost().equals(target.getHost())) {
				ZendTarget oldTarget = (ZendTarget) copyTemp((ZendTarget) t);
				oldTarget.setServerName(server.getName());
				oldTarget.setDefaultServerURL(target.getDefaultServerURL());
				oldTarget.setHost(target.getHost());
				oldTarget.setKey(target.getKey());
				oldTarget.setSecretKey(target.getSecretKey());
				status = tester.testConnection(oldTarget, monitor);
				break;
			}
		}
		if (status == null) {
			if (target.isTemporary()) {
				status = tester.testConnection(target, monitor);
			} else {
				status = tester.testConnection(target, monitor);
			}
		}
		switch (status.getSeverity()) {
		case IStatus.OK:
			ArrayList<IZendTarget> finalTargets = tester.getFinalTargets();
			for (IZendTarget t : finalTargets) {
				if (manager.getTargetById(t.getId()) != null) {
					manager.updateTarget(t, true);
				} else {
					try {
						manager.add(t, true);
					} catch (TargetException e) {
						// cannot occur, suppress connection
					} catch (LicenseExpiredException e) {
						// cannot occur, suppress connection
					}
				}
			}
			break;
		case IStatus.WARNING:
			final String warning = status.getMessage();
			Display.getDefault().syncExec(new Runnable() {
				public void run() {
					setMessage(warning, IMessageProvider.WARNING);
				}
			});
			break;
		case IStatus.ERROR:
			final String error = status.getMessage();
			Display.getDefault().syncExec(new Runnable() {
				public void run() {
					setMessage(error, IMessageProvider.ERROR);
				}
			});
			break;
		default:
			break;
		}
	}

	private IZendTarget copyTemp(ZendTarget t) {
		ZendTarget target = new ZendTarget(t.getId(), t.getHost(),
				t.getDefaultServerURL(), t.getKey(), t.getSecretKey(), true);
		String[] keys = t.getPropertiesKeys();
		for (String key : keys) {
			target.addProperty(key, t.getProperty(key));
		}
		return target;
	}

}
