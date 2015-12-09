package org.zend.php.zendserver.deployment.core.targets;

import java.io.IOException;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.equinox.security.storage.ISecurePreferences;
import org.eclipse.equinox.security.storage.SecurePreferencesFactory;
import org.eclipse.equinox.security.storage.StorageException;
import org.osgi.service.prefs.BackingStoreException;
import org.zend.php.zendserver.deployment.core.DeploymentCore;
import org.zend.sdklib.internal.target.ZendDevCloud;
import org.zend.sdklib.manager.TargetsManager;
import org.zend.sdklib.target.IZendTarget;

/**
 * Single service for managing targets within running VM. Please use this
 * service for unified user-experience.
 * 
 */
public class TargetsManagerService {

	private static final String PASSWORD_KEY = "password"; //$NON-NLS-1$

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

	public IZendTarget getContainerByName(String containerName) {
		Assert.isNotNull(containerName);

		IZendTarget[] targets = getTargetManager().getTargets();
		for (IZendTarget target : targets) {
			String container = target
					.getProperty(ZendDevCloud.TARGET_CONTAINER);
			if (containerName.equals(container)) {
				return target;
			}
		}

		return null;
	}

	public void storePhpcloudPassword(IZendTarget target, String password) {
		ISecurePreferences node = getNode(target);
		try {
			node.put(PASSWORD_KEY, password, true);
			node.flush();
		} catch (StorageException e) {
			DeploymentCore.log(e);
		} catch (IOException e) {
			DeploymentCore.log(e);
		}
	}

	public String getPhpcloudPassword(IZendTarget target) {
		ISecurePreferences node = getNode(target);
		try {
			return node.get(PASSWORD_KEY, null);
		} catch (StorageException e) {
			DeploymentCore.log(e);
		}
		return null;
	}
	
	public void storeContainerPassword(IZendTarget target, String password) {
		ZendDevCloud devCloud = new ZendDevCloud();
		devCloud.setContainerPassword(target, password);
	}

	public String getContainerPassword(IZendTarget target) {
		ZendDevCloud devCloud = new ZendDevCloud();
		return devCloud.getContainerPassword(target);
	}

	private ISecurePreferences getNode(IZendTarget target) {
		ISecurePreferences root = SecurePreferencesFactory.getDefault();
		ISecurePreferences node = root.node(DeploymentCore.PLUGIN_ID
				+ "/targets/" + target.getHost().getHost()); //$NON-NLS-1$
		return node;
	}

}
