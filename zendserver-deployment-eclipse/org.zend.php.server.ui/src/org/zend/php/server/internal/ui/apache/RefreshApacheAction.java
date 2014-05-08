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
package org.zend.php.server.internal.ui.apache;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.php.internal.server.core.Server;
import org.eclipse.php.internal.server.core.manager.ServersManager;
import org.eclipse.php.server.ui.types.IServerType;
import org.eclipse.php.server.ui.types.ServerTypesManager;
import org.zend.php.server.ui.ServersUI;
import org.zend.php.server.ui.actions.IActionContribution;
import org.zend.php.server.ui.types.LocalApacheType;

/**
 * Contribution to action which is responsible for refreshing local Apache HTTP
 * Server configuration settings.
 * 
 * @author Wojciech Galanciak, 2014
 * 
 */
@SuppressWarnings("restriction")
public class RefreshApacheAction implements IActionContribution {

	private Server server;

	@Override
	public String getLabel() {
		return Messages.RefreshApacheAction_RefreshLabel;
	}

	@Override
	public ImageDescriptor getIcon() {
		return ServersUI.getImageDescriptor(ServersUI.REFRESH_APACHE_ICON);
	}
	
	@Override
	public boolean isAvailable(Server server) {
		return true;
	}

	@Override
	public void run() {
		if (server != null) {
			IServerType serverType = ServerTypesManager.getInstance().getType(
					server);
			if (LocalApacheType.ID.equals(serverType.getId())) {
				Job refreshJob = new Job(Messages.RefreshApacheAction_JobName) {
					@Override
					protected IStatus run(IProgressMonitor monitor) {
						monitor.beginTask(Messages.RefreshApacheAction_JobDesc,
								IProgressMonitor.UNKNOWN);
						LocalApacheType.parseAttributes(server);
						ServersManager.save();
						monitor.done();
						return Status.OK_STATUS;
					}
				};
				refreshJob.setUser(true);
				refreshJob.schedule();
			}
		}
	}

	@Override
	public void setServer(Server server) {
		this.server = server;
	}

}
