package org.zend.php.zendserver.deployment.ui.editors;

import org.zend.php.zendserver.deployment.core.descriptor.IDeploymentDescriptor;
import org.zend.php.zendserver.deployment.core.descriptor.IParameter;
import org.zend.php.zendserver.deployment.core.internal.descriptor.Parameter;
import org.zend.php.zendserver.deployment.ui.Messages;


public class ParametersMasterDetailsProvider implements MasterDetailsProvider {
	
	public String getDescription() {
		return Messages.ParametersMasterDetailsProvider_0;
	}
	
	public Object[] doGetElements(Object input) {
		if (input instanceof IDeploymentDescriptor) {
			return ((IDeploymentDescriptor) input).getParameters().toArray();
		}
		
		return null;
	}
	
	public Object addElment(IDeploymentDescriptor model, DescriptorMasterDetailsBlock block) {
		IParameter param = new Parameter();
		param.setId(Messages.ParametersMasterDetailsProvider_newParamName+(model.getParameters().size() + 1));
		param.setType(IParameter.STRING);
		return param;
	}

	public Class getType() {
		return IParameter.class;
	}
}
