package org.zend.php.zendserver.monitor.internal;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.zend.php.zendserver.deployment.core.targets.TargetsManagerService;
import org.zend.php.zendserver.monitor.core.MonitorManager;
import org.zend.sdklib.target.IZendTarget;

public class TargetsMonitorPreferenceInitializer extends
		AbstractPreferenceInitializer {

	public TargetsMonitorPreferenceInitializer() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer#
	 * initializeDefaultPreferences()
	 */
	public void initializeDefaultPreferences() {
		IZendTarget[] targets = TargetsManagerService.INSTANCE
				.getTargetManager().getTargets();
		for (IZendTarget target : targets) {
			MonitorManager.setDefaultPreferences(target);
		}
	}

}
