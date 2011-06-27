package org.zend.php.zendserver.deployment.ui.editors;

import java.util.Set;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.zend.php.zendserver.deployment.core.descriptor.IDescriptorContainer;
import org.zend.sdklib.mapping.IMapping;

public class ScriptsTreeSection extends PropertiesTreeSection {

	private static final String SCRIPTSDIR = "scriptsdir";

	public ScriptsTreeSection(Composite parent, FormToolkit toolkit,
			IDescriptorContainer model) {
		super(parent, toolkit, model);
		setText("Scripts mapping");
		setDescription("Information about scriptsdir folder content in deployment package");
	}

	protected void initializeCheckState() {
		uncheckAll();
		Set<IMapping> includes = mappingModel.getInclusion(SCRIPTSDIR);
		Set<IMapping> exculdes = mappingModel.getExclusion(SCRIPTSDIR);

		super.initializeCheckState(includes, exculdes);
	}

}
