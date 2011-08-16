package org.zend.php.zendserver.deployment.ui.editors;

import org.eclipse.core.resources.IContainer;
import org.zend.php.zendserver.deployment.core.descriptor.IDeploymentDescriptor;
import org.zend.php.zendserver.deployment.core.descriptor.IDescriptorContainer;
import org.zend.php.zendserver.deployment.core.internal.descriptor.Feature;

public class FolderField extends FileField {

	public FolderField(IDescriptorContainer model, Feature key, String label,
			IContainer root) {
		super(model, key, label, root);
	}
	
	public FolderField(IDeploymentDescriptor descriptor, Feature key, String label, IContainer root) {
		super(descriptor, key, label, root);
	}

	@Override
	protected String openDialog(OpenFileDialog dialog) {
		return dialog.openFolder();
	}
}
