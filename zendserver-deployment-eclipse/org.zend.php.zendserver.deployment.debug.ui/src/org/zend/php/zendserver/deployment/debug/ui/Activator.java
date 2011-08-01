package org.zend.php.zendserver.deployment.debug.ui;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchListener;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.zend.php.zendserver.deployment.debug.ui.listeners.DeploymentLaunchListener;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.zend.php.zendserver.deployment.debug.ui"; //$NON-NLS-1$

	public static final String IMAGE_RUN_APPLICATION = "icons/obj16/run_exc.gif"; //$NON-NLS-1$
	public static final String IMAGE_DEBUG_APPLICATION = "icons/obj16/debug_exc.gif"; //$NON-NLS-1$
	public static final String IMAGE_WIZBAN_DEP = "icons/wizban/newdep_wiz.png"; //$NON-NLS-1$

	// The shared instance
	private static Activator plugin;
	
	private ILaunchListener listener;

	/**
	 * The constructor
	 */
	public Activator() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		listener = new DeploymentLaunchListener();
		DebugPlugin.getDefault().getLaunchManager().addLaunchListener(listener);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
		DebugPlugin.getDefault().getLaunchManager().removeLaunchListener(listener);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
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

	public static void log(Throwable e) {
		getDefault().getLog().log(new Status(IStatus.ERROR, PLUGIN_ID, e.getMessage(), e));
	}

}
