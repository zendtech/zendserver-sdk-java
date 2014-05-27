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
package org.zend.php.server.internal.core;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.osgi.framework.BundleContext;

/**
 * @author Wojciech Galanciak, 2014
 *
 */
public class ServersCore extends Plugin {

	public static final String PLUGIN_ID = "org.zend.php.server.internal.core.ServersCore"; //$NON-NLS-1$

	private static BundleContext context;

	private static ServersCore plugin;

	static BundleContext getContext() {
		return context;
	}

	@Override
	public void start(BundleContext bundleContext) throws Exception {
		ServersCore.context = bundleContext;
		plugin = this;
	}

	@Override
	public void stop(BundleContext bundleContext) throws Exception {
		ServersCore.context = null;
	}

	public static void log(IStatus status) {
		getDefault().getLog().log(status);
	}

	public static void logError(Throwable e) {
		log(new Status(IStatus.ERROR, PLUGIN_ID, PLUGIN_ID, e));
	}

	public static void logError(String message) {
		log(new Status(IStatus.ERROR, PLUGIN_ID, message, null));
	}

	/**
	 * Returns the shared instance.
	 */
	public static ServersCore getDefault() {
		return plugin;
	}

}
