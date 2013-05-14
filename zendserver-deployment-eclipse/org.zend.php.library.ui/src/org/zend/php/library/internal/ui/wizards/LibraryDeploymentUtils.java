/*******************************************************************************
 * Copyright (c) 2013 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.php.library.internal.ui.wizards;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.zend.php.library.core.LibraryManager;
import org.zend.php.library.core.deploy.DeployLibraryJob;
import org.zend.php.library.core.deploy.LibraryDeploymentAttributes;
import org.zend.php.library.core.deploy.SynchronizeLibraryJob;
import org.zend.php.library.internal.ui.LibraryUI;
import org.zend.php.library.internal.ui.Messages;
import org.zend.php.zendserver.deployment.core.debugger.IDeploymentHelper;
import org.zend.php.zendserver.deployment.debug.core.jobs.AbstractLaunchJob;
import org.zend.php.zendserver.deployment.debug.ui.listeners.DeployJobChangeListener;
import org.zend.php.zendserver.deployment.debug.ui.wizards.DeploymentWizard.Mode;
import org.zend.webapi.core.connection.response.ResponseCode;

/**
 * @author Wojciech Galanciak, 2013
 * 
 */
public class LibraryDeploymentUtils {

	private AbstractLaunchJob job;

	private boolean cancelled;

	private DeployJobChangeListener listener;

	private boolean dialogResult;

	public int openLibraryDeploymentWizard(IProject project) {
		job = null;
		try {
			doOpenLibraryDeploymentWizard(project);
			if (job == null) {
				return IStatus.OK;
			}
			job.setUser(true);
			job.schedule();
			job.join();
			return verifyJobResult(job.getHelper(), project);
		} catch (InterruptedException e) {
			LibraryUI.log(e);
		}
		return IStatus.OK;
	}

	private boolean isCancelled() {
		if (listener != null) {
			return listener.isCancelled() || cancelled;
		}
		return cancelled;
	}

	private int verifyJobResult(final IDeploymentHelper helper, IProject project)
			throws InterruptedException {
		if (isCancelled()) {
			return IStatus.CANCEL;
		}
		if (job instanceof DeployLibraryJob) {
			DeployLibraryJob deploymentJob = (DeployLibraryJob) job;
			ResponseCode code = deploymentJob.getResponseCode();
			if (code != null) {
				switch (deploymentJob.getResponseCode()) {
				// TODO change it for a proper errorCode when it will be
				// available
				case INTERNAL_SERVER_ERROR:
					return handleConflict(helper, project,
							deploymentJob.getResponseCode());
				default:
					break;
				}
			}
		}
		boolean addLib = Boolean.valueOf(helper.getExtraAttributes().get(
				LibraryDeploymentAttributes.ADD_LIBRARY.getName()));
		if (addLib) {
			LibraryManager.addDeployableLibrary(project);
		}
		return IStatus.OK;
	}

	private void doOpenLibraryDeploymentWizard(final IProject project) {
		Display.getDefault().syncExec(new Runnable() {

			public void run() {
				LibraryDeploymentWizard wizard = new LibraryDeploymentWizard(
						project, Mode.DEPLOY);
				Shell shell = PlatformUI.getWorkbench().getDisplay()
						.getActiveShell();
				WizardDialog dialog = new WizardDialog(shell, wizard);
				dialog.setPageSize(550, 350);
				dialog.create();
				if (dialog.open() == Window.OK) {
					IDeploymentHelper updatedHelper = wizard.getHelper();
					job = new DeployLibraryJob(updatedHelper, project);
					job.setHelper(updatedHelper);
					job.setProjectPath(project);
					job.addJobChangeListener(new JobChangeAdapter() {
						@Override
						public void done(IJobChangeEvent event) {
							if (event.getResult().getSeverity() == IStatus.CANCEL) {
								cancelled = true;
							}
						}
					});
				} else {
					cancelled = true;
					job = null;
				}
			}
		});
	}

	private int handleConflict(IDeploymentHelper helper, IProject project,
			ResponseCode code) throws InterruptedException {
		if (helper.isWarnUpdate()) {
			switch (code) {
			// TODO change it for a proper errorCode when it will be
			// available
			case INTERNAL_SERVER_ERROR:
				PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {

					public void run() {
						MessageDialog dialog = getUpdateExistingApplicationDialog(Messages.LibraryDeploymentUtils_ConflictMessage);
						if (dialog.open() == 0) {
							dialogResult = true;
						}
					}
				});
				break;
			default:
				break;
			}
		} else {
			dialogResult = true;
		}
		if (!dialogResult) {
			return IStatus.CANCEL;
		} else {
			dialogResult = false;
			job = new SynchronizeLibraryJob(helper, project);
			job.setUser(true);
			job.schedule();
			job.join();
			return verifyJobResult(job.getHelper(), project);
		}
	}

	private MessageDialog getUpdateExistingApplicationDialog(String message) {
		Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow()
				.getShell();
		return new MessageDialog(shell,
				Messages.LibraryDeploymentUtils_WarningTitle, null, message,
				MessageDialog.QUESTION, new String[] {
						Messages.LibraryDeploymentUtils_Yes,
						Messages.LibraryDeploymentUtils_No }, 1);
	}

}
