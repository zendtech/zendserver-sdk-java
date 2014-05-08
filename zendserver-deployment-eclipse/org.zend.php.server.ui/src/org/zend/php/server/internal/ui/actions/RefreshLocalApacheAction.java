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
package org.zend.php.server.internal.ui.actions;

import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.php.internal.server.core.Server;
import org.eclipse.php.internal.server.core.manager.ServersManager;
import org.eclipse.php.server.ui.types.IServerType;
import org.eclipse.php.server.ui.types.ServerTypesManager;
import org.zend.php.server.ui.ServersUI;
import org.zend.php.server.ui.types.LocalApacheType;

/**
 * @author Wojciech Galanciak, 2014
 * 
 */
@SuppressWarnings("restriction")
public class RefreshLocalApacheAction extends AbstractServerAction {

	public RefreshLocalApacheAction(ISelectionProvider provider) {
		super(Messages.RefreshLocalApacheAction_RefreshApacheLabel, ServersUI
				.getImageDescriptor(ServersUI.REFRESH_APACHE_ICON));
	}

	@Override
	public void run() {
		List<Server> toRefresh = getSelection();
		if (!toRefresh.isEmpty()) {
			final Server server = toRefresh.get(0);
			IServerType serverType = ServerTypesManager.getInstance().getType(
					server);
			if (LocalApacheType.ID.equals(serverType.getId())) {
				Job refreshJob = new Job(
						Messages.RefreshLocalApacheAction_JobName) {
					@Override
					protected IStatus run(IProgressMonitor monitor) {
						monitor.beginTask(
								Messages.RefreshLocalApacheAction_JobDesc,
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

}
