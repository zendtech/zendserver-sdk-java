package org.zend.php.zendserver.deployment.core.targets;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.osgi.service.prefs.BackingStoreException;
import org.zend.php.zendserver.deployment.core.DeploymentCore;
import org.zend.sdklib.manager.TargetsManager;
import org.zend.sdklib.target.IZendTarget;

/**
 * Single service for managing targets within running VM. Please use this
 * service for unified user-experience.
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
		IEclipsePreferences pref = new ProjectScope(project)
				.getNode(DeploymentCore.PLUGIN_ID);
		pref.put("targetId", target.getId()); //$NON-NLS-1$
		pref.put("targetHost", target.getHost().toString()); //$NON-NLS-1$
		try {
			pref.flush();
		} catch (BackingStoreException e) {
			DeploymentCore.log(e);
		}
	}

}
