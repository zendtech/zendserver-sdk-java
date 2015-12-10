package org.zend.php.zendserver.deployment.core;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.php.internal.server.core.manager.IServersManagerListener;
import org.eclipse.php.internal.server.core.manager.ServersManager;
import org.osgi.framework.BundleContext;
import org.zend.php.zendserver.deployment.core.sdk.SdkManager;
import org.zend.php.zendserver.deployment.core.tunnel.SSHTunnelManager;
import org.zend.webapi.core.IWebApiLogger;
import org.zend.webapi.core.WebApiClient;

@SuppressWarnings("restriction")
public class DeploymentCore extends Plugin {

	public static final String PLUGIN_ID = "org.zend.php.zendserver.deployment.core"; //$NON-NLS-1$

	private static BundleContext context;

	private static DeploymentCore plugin;

	private SdkManager sdkManager;

	private IServersManagerListener serversListener;

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
		WebApiClient.setLogger(new IWebApiLogger() {

			@Override
			public void logWarning(String message) {
				getLog().log(
						new Status(IStatus.WARNING, "org.zend.webapi", message));//$NON-NLS-1$
			}

			@Override
			public void logInfo(String message) {
				getLog().log(
						new Status(IStatus.INFO, "org.zend.webapi", message));//$NON-NLS-1$
			}

			@Override
			public void logError(String message, Throwable e) {
				getLog().log(
						new Status(IStatus.ERROR, "org.zend.webapi", message, e));//$NON-NLS-1$
			}

			@Override
			public void logError(Throwable e) {
				getLog().log(new Status(IStatus.ERROR, "org.zend.webapi", e //$NON-NLS-1$
						.getMessage(), e));
			}

			@Override
			public void logError(String message) {
				getLog().log(
						new Status(IStatus.ERROR, "org.zend.webapi", message));//$NON-NLS-1$
			}
		});
		serversListener = new ServersManagerListener();
		ServersManager.addManagerListener(serversListener);
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
		ServersManager.removeManagerListener(serversListener);
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
		logError("ADP internal error", e); //$NON-NLS-1$
	}

	public static void logError(String message) {
		logError(message, null);
	}

	public static void logError(String message, Throwable e) {
		log(new Status(IStatus.ERROR, PLUGIN_ID, message, e));
	}

	public static IEclipsePreferences getPreferenceScope() {
		// not using DefaultScope.INSTANCE for backwards compatibility
		return (new DefaultScope()).getNode(DeploymentCore.PLUGIN_ID);
	}

	public SdkManager getSdk() {
		return sdkManager;
	}
}
