package org.zend.php.zendserver.deployment.core;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.osgi.framework.BundleContext;
import org.zend.php.zendserver.deployment.core.sdk.SdkManager;
import org.zend.php.zendserver.deployment.core.targets.PhpcloudContainerListener;
import org.zend.php.zendserver.deployment.core.tunnel.SSHTunnelManager;
import org.zend.webapi.core.WebApiClient;
import org.zend.webapi.core.service.IRequestListener;

public class DeploymentCore extends Plugin {

	public static final String PLUGIN_ID = "org.zend.php.zendserver.deployment.core"; //$NON-NLS-1$

	private static final int INTERNAL_ERROR = 0;

	private static BundleContext context;

	private static DeploymentCore plugin;

	private SdkManager sdkManager;
	
	private IRequestListener containerListener;

	public DeploymentCore() {
		super();
		plugin = this;
	}

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
	public void start(BundleContext context) throws Exception {
		super.start(context);
		DeploymentCore.context = context;
		sdkManager = new SdkManager();
		containerListener = new PhpcloudContainerListener();
		WebApiClient.registerPreRequestListener(containerListener);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext bundleContext) throws Exception {
		super.stop(bundleContext);
		DeploymentCore.context = null;
		SSHTunnelManager.getManager().disconnectAll();
		WebApiClient.unregisterPreRequestListener(containerListener);
	}

	/**
	 * Returns the shared instance.
	 */
	public static DeploymentCore getDefault() {
		return plugin;
	}

	public static void log(IStatus status) {
		getDefault().getLog().log(status);
	}

	public static void log(Throwable e) {
		log(new Status(IStatus.ERROR, PLUGIN_ID, INTERNAL_ERROR,
				"ADP internal error", e)); //$NON-NLS-1$
	}

	public static void logErrorMessage(String message) {
		log(new Status(IStatus.ERROR, PLUGIN_ID, INTERNAL_ERROR, message, null));
	}

	public static IEclipsePreferences getPreferenceScope() {
		// not using DefaultScope.INSTANCE for backwards compatibility
		return (new DefaultScope()).getNode(DeploymentCore.PLUGIN_ID);
	}

	public void debug(Object message) {
		logErrorMessage(message.toString());
	}

	public void info(Object message) {
		logErrorMessage(message.toString());
	}

	public void warning(Object message) {
		logErrorMessage(message.toString());
	}

	public void error(Object message) {
		logErrorMessage(message.toString());
	}

	public SdkManager getSdk() {
		return sdkManager;
	}
}
