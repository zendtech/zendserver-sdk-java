/*******************************************************************************
 * Copyright (c) 2014 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.php.server.internal.ui.actions;

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
import org.zend.php.server.internal.ui.Messages;
import org.zend.php.server.ui.ServersUI;
import org.zend.php.zendserver.deployment.core.DeploymentCore;
import org.zend.php.zendserver.deployment.core.database.ITargetDatabase;
import org.zend.php.zendserver.deployment.core.targets.TargetsManagerService;
import org.zend.sdklib.manager.TargetsManager;

/**
 * Open database for selected target action.
 * 
 * @author Wojciech Galanciak, 2014
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
				Messages.OpenDatabaseConnectionAction_JobTitle) {

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
		monitor.beginTask(Messages.OpenDatabaseConnectionAction_TaskName,
				IProgressMonitor.UNKNOWN);
		boolean passwordProvided = false;
		if (targetConnection.createProfile()) {
			boolean hasPassword = targetConnection.hasPassword();
			if (!hasPassword) {
				passwordProvided = true;
				Display.getDefault().syncExec(new Runnable() {

					public void run() {
						String title = null;
						boolean askUsername = false;
						if (TargetsManager.isOpenShift(targetConnection
								.getTarget())) {
							title = Messages.OpenDatabaseConnectionAction_DatabasePassword;
							askUsername = true;
						}
						ContainerPasswordDialog dialog = new ContainerPasswordDialog(
								Display.getDefault().getActiveShell(), title,
								askUsername);
						if (dialog.open() == Window.OK) {
							if (askUsername) {
								targetConnection.setUsername(dialog
										.getUsername());
							}
							targetConnection.setPassword(dialog.getPassword());
							targetConnection.setSavePassword(dialog.getSave());
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
		IStatus status = targetConnection.getResult();
		return status != null ? status : new Status(IStatus.ERROR,
				ServersUI.PLUGIN_ID,
				Messages.OpenDatabaseConnectionAction_CannotConnectError);
	}
}