package org.zend.php.zendserver.deployment.core;

import org.eclipse.core.runtime.preferences.ConfigurationScope;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;

/**
 * Provides set of methods to operate on preferences
 */
public class PreferenceManager {

	public static String EXCLUDE = "exclude"; //$NON-NLS-1$
	private static String DEFAULT_STRING = ""; //$NON-NLS-1$

	private IEclipsePreferences defaultScope;
	private IEclipsePreferences configurationScope;

	private static PreferenceManager manager;

	private PreferenceManager(String id) {
		this.defaultScope = (new DefaultScope()).getNode(id);
		this.configurationScope = (new ConfigurationScope()).getNode(id);
	}

	public static PreferenceManager getInstance() {
		if (manager == null) {
			manager = new PreferenceManager(DeploymentCore.PLUGIN_ID);
		}
		return manager;
	}

	public String getString(String key) {
		return configurationScope.get(key,
				defaultScope.get(key, DEFAULT_STRING));
	}

	public void putString(String key, String value) {
		configurationScope.put(key, value);
	}

}
