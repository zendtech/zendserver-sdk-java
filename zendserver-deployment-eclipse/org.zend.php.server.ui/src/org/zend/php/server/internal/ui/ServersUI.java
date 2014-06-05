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
package org.zend.php.server.internal.ui;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 * 
 * @author Wojciech Galanciak, 2014
 * 
 */
public class ServersUI extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.zend.php.server.ui"; //$NON-NLS-1$

	public static final String ADD_ICON = "icons/obj16/add.gif"; //$NON-NLS-1$
	public static final String EDIT_ICON = "icons/obj16/edit.gif"; //$NON-NLS-1$
	public static final String REFRESH_ICON = "icons/obj16/refresh.gif"; //$NON-NLS-1$
	public static final String REMOVE_ICON = "icons/obj16/remove.gif"; //$NON-NLS-1$

	public static final String DEFAULT_SERVER_ICON = "icons/obj16/default_server.png"; //$NON-NLS-1$

	public static final String ZEND_SERVER_ICON = "icons/obj16/zend_server.png"; //$NON-NLS-1$
	public static final String ZEND_SERVER_WIZ = "icons/wizban/zend_server_wiz.png"; //$NON-NLS-1$

	public static final String APACHE_SERVER_ICON = "icons/obj16/apache.png"; //$NON-NLS-1$
	public static final String APACHE_SERVER_WIZ = "icons/wizban/local_apache_wiz.png"; //$NON-NLS-1$
	public static final String REFRESH_APACHE_ICON = "icons/obj16/refresh.gif"; //$NON-NLS-1$

	public static final String OPENSHIFT_ICON = "icons/obj16/openshift.png"; //$NON-NLS-1$
	public static final String OPENSHIFT_WIZ = "icons/wizban/openshift_wiz.png"; //$NON-NLS-1$

	public static final String PHPCLOUD_ICON = "icons/obj16/phpcloud.png"; //$NON-NLS-1$
	public static final String PHPCLOUD_WIZ = "icons/wizban/phpcloud_wiz.png"; //$NON-NLS-1$

	public static final String IMAGE_DATABASE = "icons/obj16/database.gif"; //$NON-NLS-1$
	public static final String IMAGE_DATABASE_CREATE = "icons/obj16/database_create.gif"; //$NON-NLS-1$
	public static final String IMAGE_DATABASE_ON = "icons/obj16/database_on.gif"; //$NON-NLS-1$
	public static final String IMAGE_DATABASE_OFF = "icons/obj16/database_off.gif"; //$NON-NLS-1$

	public static final String SET_DEFAULT_ICON = "icons/obj16/set_default.png"; //$NON-NLS-1$

	// The shared instance
	private static ServersUI plugin;

	/**
	 * The constructor
	 */
	public ServersUI() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext
	 * )
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext
	 * )
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static ServersUI getDefault() {
		return plugin;
	}

	public Image getImage(String path) {
		Image image = getImageRegistry().get(path);
		if (image == null) {
			getImageRegistry().put(path, getImageDescriptor(path));
			image = getImageRegistry().get(path);
		}

		return image;
	}

	/**
	 * Returns an image descriptor for the image file at the given plug-in
	 * relative path
	 * 
	 * @param path
	 *            the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}

	public static void logError(Throwable e) {
		getDefault().getLog().log(
				new Status(IStatus.ERROR, PLUGIN_ID, e.getMessage(), e));
	}

}
