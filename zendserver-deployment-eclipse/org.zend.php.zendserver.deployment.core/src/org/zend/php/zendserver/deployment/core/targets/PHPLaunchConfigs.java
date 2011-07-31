package org.zend.php.zendserver.deployment.core.targets;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchManager;
import org.zend.php.zendserver.deployment.core.DeploymentCore;
import org.zend.sdklib.target.IZendTarget;

public class PHPLaunchConfigs {

	private static final String LAUNCH_CONFIG_TYPE = "org.eclipse.php.debug.core.launching.webPageLaunch"; //$NON-NLS-1$

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
				String targetId = config.getAttribute("org.zend.php.zendserver.deployment.targetId", (String)null); //$NON-NLS-1$
				if (id.equals(targetId)) {
					result.add(config);
				}
			} catch (CoreException e) {
				
			}
			
		}
		
		return result.toArray(new ILaunchConfiguration[result.size()]);
	}
}
