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

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.php.internal.server.core.Server;
import org.eclipse.php.internal.server.core.manager.ServersManager;
import org.zend.php.zendserver.deployment.core.database.TargetsDatabaseManager;
import org.zend.php.zendserver.deployment.core.targets.TargetsManagerService;
import org.zend.sdklib.internal.target.OpenShiftTarget;
import org.zend.sdklib.manager.TargetsManager;
import org.zend.sdklib.target.IZendTarget;

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
			return ServersManager.getServers();
		}
		return null;
	}

	public Object getParent(Object child) {
		return null;
	}

	public Object[] getChildren(Object parent) {
		if (parent instanceof Server) {
			IZendTarget target = getTarget(((Server) parent).getName());
			if (hasDatabaseSupport(target)) {
				return new Object[] { TargetsDatabaseManager.getManager()
						.getConnection((IZendTarget) target) };
			}
		}
		return new Object[0];
	}

	public boolean hasChildren(Object parent) {
		Object[] children = getChildren(parent);
		return children != null && children.length > 0;
	}

	private IZendTarget getTarget(String serverName) {
		TargetsManager manager = TargetsManagerService.INSTANCE
				.getTargetManager();
		IZendTarget[] targets = manager.getTargets();
		for (IZendTarget target : targets) {
			if (serverName.equals(target.getServerName())) {
				return target;
			}
		}
		return null;
	}

	private boolean hasDatabaseSupport(IZendTarget target) {
		if (target != null) {
			if (TargetsManager.isPhpcloud(target)) {
				return true;
			}
			if (TargetsManager.isOpenShift(target)
					&& OpenShiftTarget.hasDatabaseSupport(target)) {
				return true;
			}
		}
		return false;
	}

}