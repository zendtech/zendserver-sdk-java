package org.zend.php.zendserver.deployment.core.sdk;

import java.io.IOException;
import java.util.Set;

import org.zend.php.zendserver.deployment.core.PreferenceManager;
import org.zend.sdklib.mapping.IMapping;
import org.zend.sdklib.mapping.PropertiesBasedMappingLoader;

public class EclipseMappingModelLoader extends PropertiesBasedMappingLoader {

	public Set<IMapping> getDefaultExclusion() throws IOException {
		return getMappings(getExclusionsPreference());
	}

	private static String[] getExclusionsPreference() {
		String pref = PreferenceManager.getInstance().getString(
				PreferenceManager.EXCLUDE);
		if (!"".equals(pref)) {
			return pref.split(SEPARATOR);
		}
		return new String[0];
	}

}
