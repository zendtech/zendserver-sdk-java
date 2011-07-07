package org.zend.php.zendserver.deployment.ui.editors;

import org.zend.php.zendserver.deployment.core.descriptor.IDeploymentDescriptor;
import org.zend.php.zendserver.deployment.core.descriptor.IVariable;
import org.zend.php.zendserver.deployment.core.internal.descriptor.Variable;
import org.zend.php.zendserver.deployment.ui.Messages;

public class VariablesMasterDetailsProvider implements MasterDetailsProvider {

	public String getDescription() {
		return Messages.VariablesMasterDetailsProvider_VariablesDescr;
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
		param.setName(Messages.VariablesMasterDetailsProvider_DefaultVariableName + variablesSize);
		
		return param;
	}

	public Class getType() {
		return IVariable.class;
	}

	public Object doGetParent(Object element) {
		// TODO Auto-generated method stub
		return null;
	}
}
