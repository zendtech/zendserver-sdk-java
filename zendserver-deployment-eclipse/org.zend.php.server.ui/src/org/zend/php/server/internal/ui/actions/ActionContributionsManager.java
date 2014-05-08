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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.php.internal.server.core.Server;
import org.eclipse.php.server.ui.types.IServerType;
import org.zend.php.server.ui.ServersUI;
import org.zend.php.server.ui.actions.IActionContribution;

public class ActionContributionsManager {

	private class ActionWrapper extends Action {

		private IActionContribution contribution;

		public ActionWrapper(IActionContribution contribution) {
			super(contribution.getName(), contribution.getIcon());
			this.contribution = contribution;
		}

		@Override
		public void run() {
			contribution.run();
		}

	}

	private static ActionContributionsManager manager;
	private Map<IActionContribution, String> contributions;

	private ActionContributionsManager() {
	}

	public static synchronized ActionContributionsManager getInstance() {
		if (manager == null) {
			manager = new ActionContributionsManager();
			manager.init();
		}
		return manager;
	}

	public IAction[] getActions(IServerType type, ISelectionProvider provider) {
		List<Server> selection = getSelection(provider);
		List<IAction> result = new ArrayList<IAction>();
		Set<IActionContribution> actions = contributions.keySet();
		for (IActionContribution action : actions) {
			if (type.getId().equals(contributions.get(action))) {
				action.setSelection(selection);
				result.add(new ActionWrapper(action));
			}
		}
		return result.toArray(new IAction[result.size()]);
	}

	protected List<Server> getSelection(ISelectionProvider provider) {
		List<Server> result = new ArrayList<Server>();
		if (provider != null) {
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

	private void init() {
		if (contributions == null) {
			IConfigurationElement[] elements = Platform.getExtensionRegistry()
					.getConfigurationElementsFor(
							"org.zend.php.server.ui.actionContributions"); //$NON-NLS-1$
			contributions = new HashMap<IActionContribution, String>();
			for (IConfigurationElement element : elements) {
				if ("action".equals(element.getName())) { //$NON-NLS-1$
					try {
						Object contribution = element
								.createExecutableExtension("class"); //$NON-NLS-1$
						if (contribution instanceof IActionContribution) {
							contributions.put(
									(IActionContribution) contribution,
									element.getAttribute("serverType")); //$NON-NLS-1$
						}
					} catch (CoreException e) {
						ServersUI.logError(e);
					}
				}
			}
		}
	}

}