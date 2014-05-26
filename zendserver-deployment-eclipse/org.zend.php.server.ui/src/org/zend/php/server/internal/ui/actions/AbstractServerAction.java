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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.php.internal.server.core.Server;

/**
 * @author Wojciech Galanciak, 2014
 * 
 */
@SuppressWarnings("restriction")
public abstract class AbstractServerAction extends Action {

	private ISelectionProvider provider;

	public AbstractServerAction(String name, ImageDescriptor icon) {
		this(name, icon, null);
	}

	public AbstractServerAction(String name, ImageDescriptor icon,
			ISelectionProvider provider) {
		super(name, icon);
		this.provider = provider;
	}

	protected List<Server> getSelection() {
		List<Server> result = new ArrayList<Server>();
		if (this.provider != null) {
			ISelection selection = provider.getSelection();
			if (selection != null && !selection.isEmpty()) {
				List<?> list = ((IStructuredSelection) selection).toList();
				for (Object object : list) {
					if (object instanceof Server) {
						result.add((Server) object);
					}
				}
			}
		}
		return result;
	}

}
