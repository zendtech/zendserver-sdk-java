package org.zend.php.zendserver.deployment.ui.editors;

import org.zend.php.zendserver.deployment.ui.Activator;
import org.zend.php.zendserver.deployment.ui.editors.propertiestext.PropertiesColorManager;
import org.zend.php.zendserver.deployment.ui.editors.propertiestext.PropertiesFileSourceViewerConfiguration;

public class PropertiesSourcePage extends SourcePage {

	public PropertiesSourcePage(String pageId, DeploymentDescriptorEditor editor) {
		super(pageId, editor);
		setDocumentProvider(editor.getDocumentProvider());
		PropertiesFileSourceViewerConfiguration a = new PropertiesFileSourceViewerConfiguration(
				new PropertiesColorManager(), Activator.getDefault().getPreferenceStore());
		setSourceViewerConfiguration(a);
	}

	@Override
	public void dispose() {
		super.dispose();
	}

}