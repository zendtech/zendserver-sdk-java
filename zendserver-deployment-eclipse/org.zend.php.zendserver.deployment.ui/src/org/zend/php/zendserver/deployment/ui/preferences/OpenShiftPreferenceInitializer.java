package org.zend.php.zendserver.deployment.ui.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.zend.php.zendserver.deployment.core.DeploymentCore;
import org.zend.sdklib.internal.target.OpenShiftTarget;

public class OpenShiftPreferenceInitializer extends
		AbstractPreferenceInitializer {

	public void initializeDefaultPreferences() {
		IEclipsePreferences prefs = DefaultScope.INSTANCE
				.getNode(DeploymentCore.PLUGIN_ID);
		prefs.put(OpenShiftTarget.LIBRA_SERVER_PROP,
				OpenShiftTarget.getLibraServer());
	}

}
