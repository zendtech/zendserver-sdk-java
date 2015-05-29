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
import org.eclipse.php.internal.server.core.Server;
import org.eclipse.php.internal.server.core.types.IServerType;
import org.zend.php.server.ui.ServersUI;
import org.zend.php.server.ui.actions.IActionContribution;

/**
 * Service class responsible for managing actions contributed for different
 * server types. Contributions are provided by <code>actionContributions</code>
 * extension point.
 * 
 * @author Wojciech Galanciak, 2014
 * @see IServerType
 * @see IActionContribution
 * 
 */
@SuppressWarnings("restriction")
public class ActionContributionsManager {

	private class ActionWrapper extends Action {

		private IActionContribution contribution;

		public ActionWrapper(IActionContribution contribution, Server server) {
			super(contribution.getLabel(), contribution.getIcon());
			this.contribution = contribution;
		}

		@Override
		public void run() {
			contribution.run();
		}

	}

	private static ActionContributionsManager manager;
	private Map<IActionContribution, List<String>> contributions;

	private ActionContributionsManager() {
		init();
	}

	/**
	 * @return {@link ActionContributionsManager} instance
	 */
	public static synchronized ActionContributionsManager getInstance() {
		if (manager == null) {
			manager = new ActionContributionsManager();
		}
		return manager;
	}

	/**
	 * Get list of actions which were contributed for specified server type.
	 * 
	 * @param type
	 *            {@link IServerType} instance
	 * @param server
	 *            {@link Server} instance
	 * @return array of {@link IAction}
	 */
	public IAction[] getActions(IServerType type, Server server) {
		List<IAction> result = new ArrayList<IAction>();
		Set<IActionContribution> actions = contributions.keySet();
		for (IActionContribution action : actions) {
			List<String> types = contributions.get(action);
			if (types.contains(type.getId()) && action.isAvailable(server)) {
				action.setServer(server);
				result.add(new ActionWrapper(action, server));
			}
		}
		return result.toArray(new IAction[result.size()]);
	}

	private void init() {
		if (contributions == null) {
			IConfigurationElement[] elements = Platform.getExtensionRegistry()
					.getConfigurationElementsFor(
							"org.zend.php.server.ui.actionContributions"); //$NON-NLS-1$
			contributions = new HashMap<IActionContribution, List<String>>();
			for (IConfigurationElement element : elements) {
				if ("action".equals(element.getName())) { //$NON-NLS-1$
					try {
						Object obj = element.createExecutableExtension("class"); //$NON-NLS-1$
						if (obj instanceof IActionContribution) {
							contributions.put((IActionContribution) obj,
									getTypes(element));
						}
					} catch (CoreException e) {
						ServersUI.logError(e);
					}
				}
			}
		}
	}

	private List<String> getTypes(IConfigurationElement element) {
		List<String> result = new ArrayList<String>();
		IConfigurationElement[] fragments = element.getChildren();
		for (IConfigurationElement fragment : fragments) {
			if ("serverType".equals(fragment.getName())) { //$NON-NLS-1$
				String id = fragment.getAttribute("id"); //$NON-NLS-1$
				result.add(id);
			}
		}
		return result;
	}

}