/*******************************************************************************
 * Copyright (c) 2012 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.php.zendserver.deployment.ui.actions;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.zend.php.zendserver.deployment.core.DeploymentCore;
import org.zend.php.zendserver.deployment.core.database.ITargetDatabase;
import org.zend.php.zendserver.deployment.core.targets.TargetsManagerService;
import org.zend.php.zendserver.deployment.ui.Messages;
import org.zend.php.zendserver.deployment.ui.targets.ContainerPasswordDialog;
import org.zend.php.zendserver.monitor.core.Activator;
import org.zend.sdklib.manager.TargetsManager;

/**
 * Open database action for selected target.
 * 
 * @author Wojciech Galanciak, 2012
 * 
 */
public class OpenDatabaseConnectionAction extends Action {

	private static final String DATA_SOURCE_VIEW = "org.eclipse.datatools.connectivity.DataSourceExplorerNavigator"; //$NON-NLS-1$
	private ITargetDatabase targetConnection;
	private IWorkbenchWindow window;

	public OpenDatabaseConnectionAction(ITargetDatabase targetConnection) {
		this.targetConnection = targetConnection;
	}

	@Override
	public void run() {
		window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		Job createProfileJob = new Job(
				Messages.OpenDatabaseConnectionAction_OpenConnectionJobTitle) {

			@Override
			protected IStatus run(IProgressMonitor monitor) {
				return createProfile(monitor);
			}

		};
		createProfileJob.addJobChangeListener(new JobChangeAdapter() {
			@Override
			public void done(IJobChangeEvent event) {
				if (window != null
						&& event.getResult().getSeverity() == IStatus.OK) {
					window.getShell().getDisplay().asyncExec(new Runnable() {

						public void run() {
							IWorkbenchPage page = window.getActivePage();
							try {
								page.showView(DATA_SOURCE_VIEW);
							} catch (PartInitException e) {
								DeploymentCore.log(e);
							}
						}
					});
				}
			}
		});
		createProfileJob.setUser(true);
		createProfileJob.schedule();
	}

	private IStatus createProfile(IProgressMonitor monitor) {
		monitor.beginTask(
				Messages.OpenDatabaseConnectionAction_OpenConnectionTaskTitle,
				IProgressMonitor.UNKNOWN);
		boolean passwordProvided = false;
		if (targetConnection.createProfile()) {
			boolean hasPassword = targetConnection.hasPassword();
			if (!hasPassword) {
				String password = TargetsManagerService.INSTANCE
						.getContainerPassword(targetConnection.getTarget());
				if (password != null && password.length() > 0) {
					targetConnection.setPassword(password);
					targetConnection.setSavePassword(true);
					hasPassword = true;
				}
			}
			if (!hasPassword) {
				passwordProvided = true;
				Display.getDefault().syncExec(new Runnable() {

					public void run() {
						String title = null;
						if (TargetsManager.isPhpcloud(targetConnection.getTarget())) {
							title = Messages.OpenDatabaseConnectionAction_0;
						}
						if (TargetsManager.isOpenShift(targetConnection.getTarget())) {
							title = Messages.OpenDatabaseConnectionAction_1;
						}
						ContainerPasswordDialog dialog = new ContainerPasswordDialog(
								Display.getDefault().getActiveShell(), title);
						if (dialog.open() == Window.OK) {
							targetConnection.setPassword(dialog.getPassword());
							targetConnection.setSavePassword(dialog.getSave());
						} else {
							
						}
					}
				});
			}
			if (targetConnection.hasPassword()) {
				try {
					if (targetConnection.connect(monitor)) {
						monitor.done();
						passwordProvided = false;
						return Status.OK_STATUS;
					}
				} finally {
					if (passwordProvided) {
						targetConnection.setPassword(null);
					}
				}
				return Status.CANCEL_STATUS;
			} else {
				return Status.CANCEL_STATUS;
			}
		}
		return new Status(IStatus.ERROR, Activator.PLUGIN_ID,
				Messages.OpenDatabaseConnectionAction_OpenConnectionError);
	}

}