package org.zend.php.zendserver.deployment.ui.editors;

import org.eclipse.jface.viewers.StructuredSelection;
import org.zend.php.zendserver.deployment.core.descriptor.IDeploymentDescriptor;
import org.zend.php.zendserver.deployment.core.descriptor.IParameter;
import org.zend.php.zendserver.deployment.core.internal.descriptor.Parameter;


public class ParametersBlock extends DescriptorMasterDetailsBlock {
	
	public ParametersBlock(DeploymentDescriptorEditor editor) {
		super(editor, "Parameters", "Following information will be required in order\nto deploy application.");
		this.editor = editor;
	}
	
	protected Object[] doGetElements(Object input) {
		if (input instanceof IDeploymentDescriptor) {
			return ((IDeploymentDescriptor) input).getParameters().toArray();
		}
		
		return null;
	}
	
	protected void addElment() {
		IDeploymentDescriptor model = editor.getModel();
		IParameter param = new Parameter();
		param.setId("parameter"+(model.getParameters().size() + 1));
		param.setType(IParameter.STRING);
		model.getParameters().add(param);
		viewer.refresh();
		viewer.setSelection(new StructuredSelection(param));
	}
}
