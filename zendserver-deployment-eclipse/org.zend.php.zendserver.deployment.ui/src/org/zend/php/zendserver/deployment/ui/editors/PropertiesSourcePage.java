package org.zend.php.zendserver.deployment.ui.editors;

import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.zend.php.zendserver.deployment.ui.editors.propertiestext.PropertiesColorManager;
import org.zend.php.zendserver.deployment.ui.editors.propertiestext.PropertiesFileSourceViewerConfiguration;

public class PropertiesSourcePage extends SourcePage {

	public PropertiesSourcePage(DeploymentDescriptorEditor editor) {
		super(editor);
		setDocumentProvider(editor.getDocumentProvider());
		PropertiesFileSourceViewerConfiguration a = new PropertiesFileSourceViewerConfiguration(
				new PropertiesColorManager(), JavaPlugin.getDefault().getPreferenceStore());
		setSourceViewerConfiguration(a);
	}

	@Override
	public void dispose() {
		super.dispose();
	}

}