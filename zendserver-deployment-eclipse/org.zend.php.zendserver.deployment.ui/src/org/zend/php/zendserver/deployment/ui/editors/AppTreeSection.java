package org.zend.php.zendserver.deployment.ui.editors;

import java.util.Set;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.zend.php.zendserver.deployment.core.descriptor.IDescriptorContainer;
import org.zend.sdklib.mapping.IMapping;

public class AppTreeSection extends PropertiesTreeSection {

	private static final String APPDIR = "appdir";

	public AppTreeSection(Composite parent, FormToolkit toolkit,
			IDescriptorContainer model) {
		super(parent, toolkit, model);
		setText("Appdir mapping");
		setDescription("Information about appdir folder content in deployment package");
	}

	protected void initializeCheckState() {
		uncheckAll();
		Set<IMapping> includes = mappingModel.getInclusion(APPDIR);
		Set<IMapping> exculdes = mappingModel.getExclusion(APPDIR);

		super.initializeCheckState(includes, exculdes);
	}

}
