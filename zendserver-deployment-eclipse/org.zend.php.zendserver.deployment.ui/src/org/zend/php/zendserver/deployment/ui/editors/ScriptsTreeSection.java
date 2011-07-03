package org.zend.php.zendserver.deployment.ui.editors;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.zend.php.zendserver.deployment.core.descriptor.IDescriptorContainer;
import org.zend.sdklib.mapping.IMappingModel;

public class ScriptsTreeSection extends PropertiesTreeSection {

	public ScriptsTreeSection(FormEditor editor, Composite parent,
			FormToolkit toolkit, IDescriptorContainer model) {
		super(editor, parent, toolkit, model);
		setText("Scripts mapping");
		setDescription("Information about scriptsdir folder content in deployment package");
	}

	@Override
	protected String getFolder() {
		return IMappingModel.SCRIPTSDIR;
	}

}
