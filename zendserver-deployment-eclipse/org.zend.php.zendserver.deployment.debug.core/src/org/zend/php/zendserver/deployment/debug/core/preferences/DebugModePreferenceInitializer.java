/*******************************************************************************
 * Copyright (c) 2012 Zend Technologies Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Zend Technologies Ltd. - initial API and implementation
 *******************************************************************************/
package org.zend.php.zendserver.deployment.debug.core.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.php.internal.server.core.Server;
import org.zend.php.zendserver.deployment.core.targets.EclipseTargetsManager;
import org.zend.php.zendserver.deployment.core.targets.TargetsManagerService;
import org.zend.php.zendserver.deployment.debug.core.DebugModeManager;
import org.zend.sdklib.target.IZendTarget;

/**
 * Debug mode preferences initializer.
 * 
 * @author Wojciech Galanciak, 2012
 * 
 */
public class DebugModePreferenceInitializer extends
		AbstractPreferenceInitializer {

	public void initializeDefaultPreferences() {
		IEclipsePreferences prefs = DefaultScope.INSTANCE
				.getNode(DebugModeManager.DEBUG_MODE_NODE);
		IZendTarget[] targets = TargetsManagerService.INSTANCE
				.getTargetManager().getTargets();
		for (IZendTarget target : targets) {
			Server server = EclipseTargetsManager.findExistingServer(target);
			if (server != null) {
				String baseURL = server.getBaseURL();
				prefs.put(target.getId(), baseURL);
			}
		}
	}

}
