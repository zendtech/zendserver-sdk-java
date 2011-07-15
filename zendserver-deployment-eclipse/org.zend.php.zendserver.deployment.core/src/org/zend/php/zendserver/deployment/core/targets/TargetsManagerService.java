package org.zend.php.zendserver.deployment.core.targets;

import org.zend.sdklib.manager.TargetsManager;

/**
 * Single service for managing targets within running VM.
 * Please use this service for unified user-experience.
 *
 */
public class TargetsManagerService {

	private TargetsManager tm;
	
	public static final TargetsManagerService INSTANCE = new TargetsManagerService();
	
	private TargetsManagerService() {
		tm = new TargetsManager();
	}
	
	/**
	 * Singleton instance
	 */
	public TargetsManager getTargetManager() {
		return tm;
	}
}
