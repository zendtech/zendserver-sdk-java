package org.zend.php.zendserver.deployment.core;

import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.zend.php.zendserver.deployment.core.sdk.SdkManager;
import org.zend.sdkcli.internal.logger.CliLogger;
import org.zend.sdklib.logger.Log;

public class DeploymentCore implements BundleActivator {

	public static final String PLUGIN_ID = "org.zend.php.zendserver.deployment.core";

	private SdkManager sdkManager;

	private static BundleContext context;

	public static BundleContext getContext() {
		return context;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext
	 * )
	 */
	public void start(BundleContext bundleContext) throws Exception {
		DeploymentCore.context = bundleContext;

		sdkManager = new SdkManager();
		Log.getInstance().registerLogger(new CliLogger());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext bundleContext) throws Exception {
		DeploymentCore.context = null;
	}

	public static void log(Throwable e) {
		// TODO
	}

	public static IEclipsePreferences getPreferenceScope() {
		return DefaultScope.INSTANCE.getNode(DeploymentCore.PLUGIN_ID);
	}

}
