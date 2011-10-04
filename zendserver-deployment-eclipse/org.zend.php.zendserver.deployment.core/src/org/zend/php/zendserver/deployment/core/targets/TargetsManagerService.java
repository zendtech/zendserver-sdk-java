package org.zend.php.zendserver.deployment.core.targets;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.osgi.service.prefs.BackingStoreException;
import org.zend.php.zendserver.deployment.core.DeploymentCore;
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
	
}
