package org.zend.php.zendserver.deployment.debug.core;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.zend.php.zendserver.deployment.debug.core.tunnel.ZendDevCloudTunnelManager;

public class Activator implements BundleActivator {

	public static final String PLUGIN_ID = "org.zend.php.zendserver.deployment.debug.core"; //$NON-NLS-1$

	private static BundleContext context;

	static BundleContext getContext() {
		return context;
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext bundleContext) throws Exception {
		Activator.context = bundleContext;
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext bundleContext) throws Exception {
		Activator.context = null;
		ZendDevCloudTunnelManager.getManager().disconnectAll();
	}

}
