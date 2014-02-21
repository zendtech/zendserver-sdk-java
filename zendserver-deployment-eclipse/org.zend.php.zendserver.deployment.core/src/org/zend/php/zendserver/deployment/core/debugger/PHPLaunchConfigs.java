package org.zend.php.zendserver.deployment.core.debugger;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchManager;
import org.osgi.service.prefs.BackingStoreException;
import org.zend.php.zendserver.deployment.core.DeploymentCore;
import org.zend.sdklib.target.IZendTarget;

public class PHPLaunchConfigs {

	public static final String LAUNCH_CONFIG_TYPE = "org.eclipse.php.debug.core.launching.webPageLaunch"; //$NON-NLS-1$
	private static final String REMOTE_PROJECT = "com.zend.php.remoteproject.core"; //$NON-NLS-1$
	private static final String REMOTE_PROJECT_ENABLED = "isRemoteProjectEnabled"; //$NON-NLS-1$

	
	public ILaunchConfiguration[] getLaunches(IZendTarget target) {
		ILaunchManager mgr = DebugPlugin.getDefault().getLaunchManager();
		ILaunchConfigurationType type = mgr.getLaunchConfigurationType(LAUNCH_CONFIG_TYPE);
		
		String id = target.getId();
		
		ILaunchConfiguration[] launchConfigs;
		try {
			launchConfigs = mgr.getLaunchConfigurations(type);
		} catch (CoreException e) {
			DeploymentCore.log(e);
			return null;
		}
		
		List<ILaunchConfiguration> result = new ArrayList<ILaunchConfiguration>();
		for (ILaunchConfiguration config : launchConfigs) {
			try {
				String targetId = config.getAttribute(DeploymentAttributes.TARGET_ID.getName(), (String)null);
				if (id.equals(targetId)) {
					result.add(config);
				}
			} catch (CoreException e) {
				
			}
			
		}
		
		return result.toArray(new ILaunchConfiguration[result.size()]);
	}

	public static IStatus preLaunchConfigurationRemoval(ILaunchConfiguration config) {
		try {
			String projectName = config.getAttribute(DeploymentAttributes.PROJECT_NAME.getName(), (String) null);
			IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
			IProject project = root.getProject(projectName);
			if (project != null) {
				ProjectScope projectScope = new ProjectScope(project);
				IEclipsePreferences remoteNode = projectScope.getNode(REMOTE_PROJECT);
				if (remoteNode != null
						&& remoteNode.getBoolean(
								REMOTE_PROJECT_ENABLED, false)) {
					remoteNode.putBoolean(REMOTE_PROJECT_ENABLED, false);
					remoteNode.flush();
				}
			}
			return Status.OK_STATUS;
		} catch (CoreException e) {
			return new Status(IStatus.ERROR, DeploymentCore.PLUGIN_ID, e.getMessage(), e);
		} catch (BackingStoreException e) {
			return new Status(IStatus.ERROR, DeploymentCore.PLUGIN_ID, e.getMessage(), e);
		}
	}
}
