package org.zend.php.zendserver.deployment.ui.editors;

import org.zend.php.zendserver.deployment.core.descriptor.IDeploymentDescriptor;
import org.zend.php.zendserver.deployment.core.descriptor.IVariable;
import org.zend.php.zendserver.deployment.core.internal.descriptor.Variable;

public class VariablesMasterDetailsProvider implements MasterDetailsProvider {

	public String getDescription() {
		return "Variables to pass to application deployment scripts.";
	}
	
	public Object[] doGetElements(Object input) {
		if (input instanceof IDeploymentDescriptor) {
			return ((IDeploymentDescriptor) input).getVariables().toArray();
		}
		
		return null;
	}
	
	public Object addElment(IDeploymentDescriptor model, DescriptorMasterDetailsBlock block) {
		int variablesSize = model.getVariables().size() + 1;

		IVariable param = new Variable();
		param.setName("variable" + variablesSize);
		
		return param;
	}
}
