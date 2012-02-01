package org.zend.php.zendserver.deployment.core.targets;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.php.internal.server.core.Server;
import org.eclipse.php.internal.server.core.manager.ServersManager;
import org.eclipse.rse.core.RSECorePlugin;
import org.eclipse.rse.core.model.IHost;
import org.eclipse.rse.core.model.ISystemRegistry;
import org.osgi.service.prefs.BackingStoreException;
import org.zend.php.zendserver.deployment.core.DeploymentCore;
import org.zend.php.zendserver.deployment.core.debugger.PHPLaunchConfigs;
import org.zend.sdklib.internal.target.ZendDevCloud;
import org.zend.sdklib.manager.TargetsManager;
import org.zend.sdklib.target.IZendTarget;

/**
 * Single service for managing targets within running VM.
 * Please use this service for unified user-experience.
 *
 */
public class TargetsManagerService {

	private TargetsManager tm;
	
	public static final TargetsManagerService INSTANCE = new TargetsManagerService();
	
	private TargetsManagerService() {
		tm = new EclipseTargetsManager();
	}
	
	/**
	 * Singleton instance
	 */
	public TargetsManager getTargetManager() {
		return tm;
	}
	
	/**
	 * Sets default target for project
	 * 
	 * @param target
	 * @param project
	 */
	public void storeTarget(IZendTarget target, IProject project) {
		IEclipsePreferences pref = new ProjectScope(project).getNode(DeploymentCore.PLUGIN_ID);
		pref.put("targetId", target.getId());
		pref.put("targetHost", target.getHost().toString());
		try {
			pref.flush();
		} catch (BackingStoreException e) {
			DeploymentCore.log(e);
		}
	}

	public IZendTarget getContainerByName(String containerName) {
		Assert.isNotNull(containerName);
		
		IZendTarget[] targets = getTargetManager().getTargets();
		for (IZendTarget target : targets) {
			String container = target.getProperty(ZendDevCloud.TARGET_CONTAINER);
			if (containerName.equals(container)) {
				return target;
			}
		}
		
		return null;
	}
	
	public void removeTarget(final IZendTarget target) {
		Job removeJob = new Job("Remove target") { //$NON-NLS-1$
			
			@Override
			protected IStatus run(IProgressMonitor monitor) {
				// remove target from targets storage
				try {
					TargetsManagerService.INSTANCE.getTargetManager().remove(target);
				} catch (RuntimeException e) {
					return new Status(IStatus.ERROR, DeploymentCore.PLUGIN_ID, e.getMessage(), e);
				}
				// remove all launch configurations and RSE support
				PHPLaunchConfigs launchConfigs = new PHPLaunchConfigs();
				ILaunchConfiguration[] configs = launchConfigs.getLaunches(target);
				for (ILaunchConfiguration config : configs) {
					try {
						PHPLaunchConfigs.preLaunchConfigurationRemoval(config);
						config.delete();
					} catch (CoreException e) {
						return new Status(IStatus.ERROR, DeploymentCore.PLUGIN_ID, e.getMessage(), e);
					}
				}
				
				// remove corresponding server definition
				Server[] servers = ServersManager.getServers();
				for (Server server : servers) {
					try {
						URL serverBaseURL = new URL(server.getBaseURL());
						URL targetHost = target.getHost();
						if (serverBaseURL.getHost().equals(targetHost.getHost())) {
							if ((serverBaseURL.getPort() == targetHost.getPort())
									|| (isDefaultPort(serverBaseURL) && isDefaultPort(targetHost))) {
								ServersManager.removeServer(server.getName());
							}
						}
					} catch (MalformedURLException e) {
						// ignore and continue searching
					}
				}
				
				IHost[] existingHosts = RSECorePlugin.getTheSystemRegistry().getHosts();
				if (existingHosts != null && existingHosts.length > 0) {
					for (IHost existingHost : existingHosts) {
						if (existingHost.getHostName().equalsIgnoreCase(target.getHost().getHost())) {
							ISystemRegistry sr = RSECorePlugin.getTheSystemRegistry();
							sr.deleteHost(existingHost);
						}
					}
				}
				
				return Status.OK_STATUS;
			}
			
			private boolean isDefaultPort(URL url) {
				int port = url.getPort();
				if (port == -1 || port == 80) {
					return true;
				}
				return false;
			}
		};
		removeJob.setSystem(true);
		removeJob.schedule();
	}
	
}
