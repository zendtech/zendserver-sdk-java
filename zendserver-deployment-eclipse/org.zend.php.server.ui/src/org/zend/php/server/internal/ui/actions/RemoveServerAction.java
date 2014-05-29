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

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.php.internal.server.core.Server;
import org.eclipse.php.internal.server.core.manager.ServersManager;
import org.zend.php.server.internal.ui.Messages;
import org.zend.php.server.internal.ui.ServersUI;

/**
 * @author Wojciech Galanciak, 2014
 * 
 */
@SuppressWarnings("restriction")
public class RemoveServerAction extends AbstractServerAction implements
		ISelectionChangedListener {

	private boolean isEnabled;

	public RemoveServerAction(ISelectionProvider provider) {
		super(Messages.RemoveServerAction_RemoveLabel, ServersUI
				.getImageDescriptor(ServersUI.REMOVE_ICON), provider);
		provider.addSelectionChangedListener(this);
	}

	@Override
	public void run() {
		List<Server> toRemove = getSelection();
		if (!toRemove.isEmpty()) {
			for (Server server : toRemove) {
				ServersManager.removeServer(server.getName());
			}
			ServersManager.save();
		}
	}

	@Override
	public boolean isEnabled() {
		return isEnabled;
	}

	@Override
	public void selectionChanged(SelectionChangedEvent event) {
		ISelection selection = event.getSelection();
		boolean newVal = !selection.isEmpty();
		if (newVal) {
			List<Server> servers = getSelection();
			if (servers.size() > 1) {
				newVal = false;
			} else {
				if (ServersManager.getDefaultServer(null).getName()
						.equals(servers.get(0).getName())) {
					newVal = false;
				}
			}

		}
		if (isEnabled != newVal) {
			isEnabled = newVal;
			firePropertyChange(ENABLED, !isEnabled, isEnabled);
		}
	}

}
