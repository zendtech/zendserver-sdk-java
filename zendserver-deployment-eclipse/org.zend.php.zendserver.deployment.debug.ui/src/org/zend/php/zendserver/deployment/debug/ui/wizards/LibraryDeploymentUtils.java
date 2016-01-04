/*******************************************************************************
 * Copyright (c) 2013 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.php.zendserver.deployment.debug.ui.wizards;

import java.text.MessageFormat;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.zend.php.zendserver.deployment.core.debugger.LibraryDeployData;
import org.zend.php.zendserver.deployment.core.targets.TargetsManagerService;
import org.zend.php.zendserver.deployment.core.utils.DeploymentUtils;
import org.zend.php.zendserver.deployment.debug.core.jobs.AbstractLibraryJob;
import org.zend.php.zendserver.deployment.debug.core.jobs.AddLibraryJob;
import org.zend.php.zendserver.deployment.debug.core.jobs.DeployLibraryJob;
import org.zend.php.zendserver.deployment.debug.core.jobs.MakeLibraryVersionDefaultJob;
import org.zend.php.zendserver.deployment.debug.ui.Messages;
import org.zend.sdklib.target.IZendTarget;
import org.zend.webapi.core.connection.response.ResponseCode;

/**
 * @author Wojciech Galanciak, 2013
 * 
 */
public class LibraryDeploymentUtils {

	private AbstractLibraryJob job;

	public void openLibraryDeploymentWizard(IProject project, String targetId) {
		AbstractLibraryWizard wizard = new LibraryDeploymentWizard(project, targetId);
		doOpenLibraryDeploymentWizard(wizard);
		runDeployment();
	}

	public void openLibraryDeploymentWizard(LibraryDeployData data) {
		AbstractLibraryWizard wizard = new LibraryDeploymentWizard(data);
		doOpenLibraryDeploymentWizard(wizard);
		runDeployment();
	}

	public void openLibraryDeploymentWizard(IZendTarget target) {
		AbstractLibraryWizard wizard = new DeployTargetWizard(target);
		doOpenLibraryDeploymentWizard(wizard);
		runDeployment();
	}

	private void runDeployment() {
		if (job == null)
			return;

		job.addJobChangeListener(new JobChangeAdapter() {

			@Override
			public void done(IJobChangeEvent event) {
				super.done(event);
				verifyJobResult(event);
			}

		});
		job.schedule();
	}

	private void verifyJobResult(IJobChangeEvent event) {
		if (event.getResult() == Status.CANCEL_STATUS)
			return;

		DeployLibraryJob job = (DeployLibraryJob) event.getJob();
		LibraryDeployData data = job.getData();

		if (data.makeDefault()) {
			Job makeDefaultJob = new MakeLibraryVersionDefaultJob(data);
			makeDefaultJob.schedule();
		}

		if (data.isAddPHPLibrary()) {
			Job addJob = new AddLibraryJob(data);
			addJob.schedule();
		}

		ResponseCode code = job.getResponseCode();
		if (code != null) {
			switch (code) {
			case LIBRARY_CONFLICT:
				handleConflict(data);
				return;
			default:
				break;
			}
		}

		if (data.getProject() != null) {
			DeploymentUtils.updatePreferences(data.getProject(), data.getTargetId(), null);
		}
	}

	private void doOpenLibraryDeploymentWizard(final AbstractLibraryWizard wizard) {
		Display.getDefault().syncExec(new Runnable() {

			public void run() {
				job = null;

				Shell shell = PlatformUI.getWorkbench().getDisplay().getActiveShell();
				WizardDialog dialog = new WizardDialog(shell, wizard);
				dialog.create();
				if (dialog.open() != Window.OK)
					return;

				LibraryDeployData data = wizard.getData();
				job = new DeployLibraryJob(data);
			}
		});
	}

	private void handleConflict(final LibraryDeployData data) {
		PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {

			public void run() {
				Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
				IZendTarget target = TargetsManagerService.INSTANCE.getTargetManager()
						.getTargetById(data.getTargetId());
				String message = MessageFormat.format(Messages.LibraryDeploymentUtils_ConflictMessage, data.getName(),
						data.getVersion());
				if (target != null)
					message = MessageFormat.format(Messages.LibraryDeploymentUtils_ConflictMessage2, data.getName(),
							data.getVersion(), target.getServerName());
				MessageDialog.openError(shell, Messages.LibraryDeploymentWizard_Title, message);
			}
		});
	}
}
