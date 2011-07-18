package org.zend.php.zendserver.deployment.ui;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.swt.graphics.RGB;
import org.zend.php.zendserver.deployment.ui.editors.propertiestext.PropertiesFileSourceViewerConfiguration;

public class DeploymentUIPreferenceInitializer extends AbstractPreferenceInitializer {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer#
	 * initializeDefaultPreferences()
	 */
	@Override
	public void initializeDefaultPreferences() {
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		PreferenceConverter.setDefault(store,
				PropertiesFileSourceViewerConfiguration.PROPERTIES_FILE_COLORING_KEY, new RGB(0, 0,
						0));

		PreferenceConverter.setDefault(store,
				PropertiesFileSourceViewerConfiguration.PROPERTIES_FILE_COLORING_VALUE, new RGB(42,
						0, 255));

		PreferenceConverter.setDefault(store,
				PropertiesFileSourceViewerConfiguration.PROPERTIES_FILE_COLORING_ASSIGNMENT,
				new RGB(0, 0, 0));

		PreferenceConverter.setDefault(store,
				PropertiesFileSourceViewerConfiguration.PROPERTIES_FILE_COLORING_ARGUMENT, new RGB(
						127, 0, 85));

		PreferenceConverter.setDefault(store,
				PropertiesFileSourceViewerConfiguration.PROPERTIES_FILE_COLORING_COMMENT, new RGB(
						63, 127, 95));
	}

}