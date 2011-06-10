package org.zend.php.zendserver.deployment.ui.editors;

import org.eclipse.core.resources.IContainer;
import org.zend.php.zendserver.deployment.core.descriptor.IDeploymentDescriptor;
import org.zend.php.zendserver.deployment.core.descriptor.IDeploymentDescriptorModifier;

public class FolderField extends FileField {

	public FolderField(IDeploymentDescriptor target,
			IDeploymentDescriptorModifier modifier, String key, String label,
			IContainer root) {
		super(target, modifier, key, label, root);
	}

	@Override
	protected String openDialog(OpenFileDialog dialog) {
		return dialog.openFolder();
	}
}
