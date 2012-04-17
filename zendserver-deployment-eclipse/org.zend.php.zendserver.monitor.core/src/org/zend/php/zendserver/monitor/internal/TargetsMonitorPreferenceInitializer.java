package org.zend.php.zendserver.monitor.internal;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;
import org.zend.php.zendserver.monitor.core.Activator;
import org.zend.php.zendserver.monitor.core.MonitorManager;
import org.zend.webapi.core.connection.data.values.IssueSeverity;

public class TargetsMonitorPreferenceInitializer extends
		AbstractPreferenceInitializer {

	public TargetsMonitorPreferenceInitializer() {
	}

	@Override
	public void initializeDefaultPreferences() {
		IPreferenceStore preferences = Activator.getDefault()
				.getPreferenceStore();
		preferences.setDefault(MonitorManager.HIDE_KEY, false);
		preferences.setDefault(MonitorManager.HIDE_TIME_KEY, 10);
		IssueSeverity[] severityValues = IssueSeverity.values();
		for (int i = 0; i < severityValues.length; i++) {
			String name = severityValues[i].getName();
			if (IssueSeverity.CRITICAL == severityValues[i]
					|| IssueSeverity.WARNING == severityValues[i]) {
				preferences.setDefault(name.toLowerCase(), true);
			}
		}
	}

}
