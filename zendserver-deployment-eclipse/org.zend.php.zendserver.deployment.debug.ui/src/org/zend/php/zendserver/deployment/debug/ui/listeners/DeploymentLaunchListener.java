package org.zend.php.zendserver.deployment.debug.ui.listeners;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.php.debug.core.debugger.launching.ILaunchDelegateListener;
import org.eclipse.php.internal.debug.core.debugger.AbstractDebuggerConfiguration;
import org.eclipse.php.internal.debug.core.preferences.PHPDebuggersRegistry;
import org.eclipse.php.internal.debug.core.zend.communication.DebuggerCommunicationDaemon;
import org.zend.php.zendserver.deployment.core.DeploymentNature;
import org.zend.php.zendserver.deployment.core.debugger.DeploymentAttributes;
import org.zend.php.zendserver.deployment.core.targets.TargetsManagerService;
import org.zend.php.zendserver.deployment.debug.core.config.LaunchUtils;
import org.zend.php.zendserver.deployment.debug.ui.Activator;
import org.zend.php.zendserver.deployment.debug.ui.config.DeploymentHandler;
import org.zend.sdklib.internal.target.OpenShiftTarget;
import org.zend.sdklib.manager.TargetsManager;
import org.zend.sdklib.target.IZendTarget;

public class DeploymentLaunchListener implements ILaunchDelegateListener {

	private static final String CLIENT_HOST_KEY = "org.eclipse.php.debug.coreclient_ip"; //$NON-NLS-1$
	private static final String DEBUG_PLUGIN_ID = "org.eclipse.php.debug.core"; //$NON-NLS-1$

	public int preLaunch(ILaunchConfiguration configuration, String mode,
			ILaunch launch, IProgressMonitor monitor) {
		try {
			final IProject project = LaunchUtils
					.getProjectFromFilename(configuration);
			if (project == null || !project.hasNature(DeploymentNature.ID)) {
				return IStatus.OK;
			}

			DeploymentHandler handler = new DeploymentHandler(configuration);
			int result = handler.executeDeployment(mode);
			if (result == IStatus.CANCEL) {
				configuration.delete();
			}
			
			if (mode.equals(ILaunchManager.DEBUG_MODE)) {
				AbstractDebuggerConfiguration debuggerConfiguration = PHPDebuggersRegistry
						.getDebuggerConfiguration(DebuggerCommunicationDaemon.ZEND_DEBUGGER_ID);
				String targetHost = configuration.getAttribute(
						DeploymentAttributes.TARGET_HOST.getName(),
						(String) null);
				if (debuggerConfiguration != null && targetHost != null) {
					if (TargetsManager.isOpenShift(targetHost)) {
						String targetId = configuration.getAttribute(
								DeploymentAttributes.TARGET_ID.getName(),
								(String) null);
						addInternalHost(TargetsManagerService.INSTANCE
								.getTargetManager().getTargetById(targetId));
						debuggerConfiguration.setPort(17000);
						debuggerConfiguration.save();
					}
					if (TargetsManager.isPhpcloud(targetHost)) {
						debuggerConfiguration.setPort(10137);
						debuggerConfiguration.save();
					}
				}
			}
			return result;
		} catch (CoreException e) {
			Activator.log(e);
		}
		return 0;
	}

	private void addInternalHost(IZendTarget target) {
		IEclipsePreferences prefs = InstanceScope.INSTANCE
				.getNode(DEBUG_PLUGIN_ID);
		String interalHost = target
				.getProperty(OpenShiftTarget.TARGET_INTERNAL_HOST);
		String clientHosts = prefs.get(CLIENT_HOST_KEY, (String) null);
		if (clientHosts == null) {
			IEclipsePreferences defaultPrefs = DefaultScope.INSTANCE
					.getNode(DEBUG_PLUGIN_ID);
			clientHosts = defaultPrefs.get(CLIENT_HOST_KEY, (String) null);
		}
		if (clientHosts != null) {
			String[] hosts = clientHosts.split(","); //$NON-NLS-1$
			for (String host : hosts) {
				if (host.trim().equals(interalHost)) {
					return;
				}
			}
			clientHosts += ',' + interalHost;
		}
		prefs.put(CLIENT_HOST_KEY, clientHosts);
	}

}
