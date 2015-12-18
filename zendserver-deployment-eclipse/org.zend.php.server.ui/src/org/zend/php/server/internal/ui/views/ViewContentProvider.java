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
package org.zend.php.server.internal.ui.views;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.php.internal.server.core.Server;
import org.eclipse.php.internal.server.core.manager.ServersManager;

/**
 * Content provider for PHP Servers view.
 * 
 * @author Wojciech Galanciak, 2014
 * 
 */
@SuppressWarnings("restriction")
class ViewContentProvider implements IStructuredContentProvider,
		ITreeContentProvider {

	public void inputChanged(Viewer v, Object oldInput, Object newInput) {
	}

	public void dispose() {
	}

	public Object[] getElements(Object parent) {
		if (parent instanceof ServersManager) {
			Server[] servers = ServersManager.getServers();
			Arrays.sort(servers, new Comparator<Server>() {
				@Override
				public int compare(Server first, Server second) {
					return String.CASE_INSENSITIVE_ORDER.compare(
							first.getName(), second.getName());
				}
			});
			List<Server> result = new ArrayList<Server>();
			for (Server server : servers) {
				if (!ServersManager.isNoneServer(server)) {
					result.add(server);
				}
			}
			return result.toArray(new Server[result.size()]);
		}
		return null;
	}

	public Object getParent(Object child) {
		return null;
	}

	public Object[] getChildren(Object parent) {
		return null;
	}

	public boolean hasChildren(Object parent) {
		return false;
	}

}