/*******************************************************************************
 * Copyright (c) May 18, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.php.common.welcome;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.zend.php.common.Activator;


/**
 * Helps manage all listeners of the Welcome page
 * 
 * @author Roy, 2011
 * 
 */
public class WelcomeLinkListenerManager {

	private final Map<String, Runnable> runnables = new HashMap<String, Runnable>();

	public WelcomeLinkListenerManager() {
		try {
			initiateWelcomeListeners();
		} catch (CoreException e) {
			Activator.log(e);
		}
	}

	public void initiateWelcomeListeners() throws CoreException {
		IConfigurationElement[] elements = Platform.getExtensionRegistry()
				.getConfigurationElementsFor(
						"org.zend.php.ui.welcomeLinkListener");
		for (int i = 0; i < elements.length; i++) {
			IConfigurationElement element = elements[i];
			if (element.getName().equals("listener")) { //$NON-NLS-1$
				final String key = (String) element.getAttribute("name");
				final Runnable value = (Runnable) element
						.createExecutableExtension("class");
				runnables.put(key, value);
			}
		}
	}

	public Runnable getRunnable(String message) {
		return runnables.get(message);
	}

}
