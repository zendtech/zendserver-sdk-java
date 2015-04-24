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
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerDropAdapter;
import org.eclipse.php.internal.server.core.Server;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.TransferData;
import org.eclipse.ui.part.ResourceTransfer;
import org.zend.php.server.ui.ServersUI;
import org.zend.php.server.ui.actions.IDragAndDropContribution;

@SuppressWarnings("restriction")
public class DropTransferListener extends ViewerDropAdapter {

	private static List<IDragAndDropContribution> contributions;

	protected DropTransferListener(Viewer viewer) {
		super(viewer);
		init();
	}

	@Override
	public boolean performDrop(Object data) {
		IResource[] resources = (IResource[]) data;
		if (resources.length == 0) {
			return false;
		}
		Server server = (Server) getCurrentTarget();
		if (resources[0] instanceof IProject) {
			IProject project = (IProject) resources[0];
			for (IDragAndDropContribution contribution : contributions) {
				if (contribution.isSupported(server, project)) {
					contribution.performAction(server, project);
					break;
				}
			}
		}
		return true;
	}

	@Override
	public boolean validateDrop(Object target, int operation, TransferData type) {
		boolean support = ResourceTransfer.getInstance().isSupportedType(type);
		if (support && target instanceof Server) {
			Server server = (Server) target;
			for (IDragAndDropContribution contribution : contributions) {
				if (contribution.isAvailable(server)) {
					overrideOperation(DND.DROP_COPY);
					return true;
				}
			}
		}
		return false;
	}

	private void init() {
		if (contributions == null) {
			IConfigurationElement[] elements = Platform.getExtensionRegistry()
					.getConfigurationElementsFor(
							"org.zend.php.server.ui.dragAndDropContribution"); //$NON-NLS-1$
			contributions = new ArrayList<IDragAndDropContribution>();
			for (IConfigurationElement element : elements) {
				if ("contribution".equals(element.getName())) { //$NON-NLS-1$
					try {
						Object obj = element.createExecutableExtension("class"); //$NON-NLS-1$
						if (obj instanceof IDragAndDropContribution) {
							contributions.add((IDragAndDropContribution) obj);
						}
					} catch (CoreException e) {
						ServersUI.logError(e);
					}
				}
			}
		}
	}
}
