package org.zend.php.zendserver.deployment.core;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.osgi.service.prefs.BackingStoreException;
import org.zend.php.zendserver.deployment.core.sdk.SdkManager;


public class PreferenceInitializer extends AbstractPreferenceInitializer {

	public PreferenceInitializer() {
		super();
	}

	public void initializeDefaultPreferences() {
		IEclipsePreferences node = DeploymentCore.getPreferenceScope();
		node.put(PreferenceManager.EXCLUDE, "**/.cvs,**/.svn,**/.git"); //$NON-NLS-1$
		String sdkPath = SdkManager.getDefaultSdkPath();
		if (sdkPath != null) {
			node.put(SdkManager.SDK_PATH, sdkPath);
		}
		try {
			node.flush();
		} catch (BackingStoreException e) {
			DeploymentCore.log(e);
		}
	}
}