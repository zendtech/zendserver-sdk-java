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
import org.zend.php.zendserver.deployment.ui.targets.ContainerPasswordDialog;

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
		if (targetConnection.createProfile()) {
			if (!targetConnection.hasPassword()) {
				Display.getDefault().syncExec(new Runnable() {

					public void run() {
						ContainerPasswordDialog dialog = new ContainerPasswordDialog(
								Display.getDefault().getActiveShell());
						if (dialog.open() == Window.OK) {
							targetConnection.setPassword(dialog.getPassword(),
									dialog.getSave());
						} else {
							return;
						}
						connect();
					}
				});
			} else {
				connect();
			}
		}
	}

	private void connect() {
		Job connectJob = new Job("Connecting to Database") { //$NON-NLS-1$

			@Override
			protected IStatus run(IProgressMonitor monitor) {
				if (targetConnection.connect(monitor)) {
					return Status.OK_STATUS;
				}
				return Status.CANCEL_STATUS;
			}
		};
		connectJob.addJobChangeListener(new JobChangeAdapter() {
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
		connectJob.setUser(true);
		connectJob.schedule();
	}

}