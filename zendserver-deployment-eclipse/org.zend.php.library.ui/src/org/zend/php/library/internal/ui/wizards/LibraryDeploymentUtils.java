/*******************************************************************************
 * Copyright (c) 2013 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.php.library.internal.ui.wizards;

import java.io.File;

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
import org.zend.php.library.core.deploy.AbstractLibraryJob;
import org.zend.php.library.core.deploy.DeployLibraryJob;
import org.zend.php.library.core.deploy.LibraryDeployData;
import org.zend.php.library.core.deploy.SynchronizeLibraryJob;
import org.zend.php.library.internal.ui.LibraryUI;
import org.zend.php.library.internal.ui.Messages;
import org.zend.php.zendserver.deployment.debug.ui.listeners.DeployJobChangeListener;
import org.zend.webapi.core.connection.response.ResponseCode;

/**
 * @author Wojciech Galanciak, 2013
 * 
 */
public class LibraryDeploymentUtils {

	private AbstractLibraryJob job;

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
			return verifyJobResult(job.getData());
		} catch (InterruptedException e) {
			LibraryUI.log(e);
		}
		return IStatus.OK;
	}

	public int openLibraryDeploymentWizard(File root) {
		job = null;
		try {
			doOpenLibraryDeploymentWizard(root);
			if (job == null) {
				return IStatus.OK;
			}
			job.setUser(true);
			job.schedule();
			job.join();
			return verifyJobResult(job.getData());
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

	private int verifyJobResult(LibraryDeployData data)
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
					return handleConflict(data, deploymentJob.getResponseCode());
				default:
					break;
				}
			}
		}
		if (data.isAddPHPLibrary()) {
			LibraryManager.addDeployableLibrary(data);
		}
		return IStatus.OK;
	}

	private void doOpenLibraryDeploymentWizard(final IProject project) {
		LibraryDeploymentWizard wizard = new LibraryDeploymentWizard(project);
		doOpenLibraryDeploymentWizard(wizard);
	}

	private void doOpenLibraryDeploymentWizard(File root) {
		LibraryDeploymentWizard wizard = new LibraryDeploymentWizard(root);
		doOpenLibraryDeploymentWizard(wizard);
	}

	private void doOpenLibraryDeploymentWizard(
			final LibraryDeploymentWizard wizard) {
		Display.getDefault().syncExec(new Runnable() {

			public void run() {
				Shell shell = PlatformUI.getWorkbench().getDisplay()
						.getActiveShell();
				WizardDialog dialog = new WizardDialog(shell, wizard);
				dialog.setPageSize(550, 350);
				dialog.create();
				if (dialog.open() == Window.OK) {
					LibraryDeployData data = wizard.getData();
					job = new DeployLibraryJob(data);
					job.addJobChangeListener(new JobChangeAdapter() {
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

	private int handleConflict(LibraryDeployData data, ResponseCode code)
			throws InterruptedException {
		if (data.isWarnSynchronize()) {
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
			job = new SynchronizeLibraryJob(data);
			job.setUser(true);
			job.schedule();
			job.join();
			return verifyJobResult(job.getData());
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
