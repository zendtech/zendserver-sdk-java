package org.zend.php.zendserver.deployment.ui.editors;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.dialogs.ListDialog;
import org.zend.php.zendserver.deployment.core.descriptor.IDeploymentDescriptor;
import org.zend.php.zendserver.deployment.core.descriptor.IParameter;
import org.zend.php.zendserver.deployment.core.descriptor.IVariable;
import org.zend.php.zendserver.deployment.core.internal.descriptor.Parameter;
import org.zend.php.zendserver.deployment.core.internal.descriptor.Variable;
import org.zend.php.zendserver.deployment.ui.Messages;

public class VarsAndParamsMasterDetailsProvider implements MasterDetailsProvider {

	private static final Object[] EMPTY = new Object[0];
	
	public String getDescription() {
		return Messages.VarsAndParamsMasterDetailsProvider_DefineVarsAndParams;
	}
	
	public Object[] doGetElements(Object input) {
		if (input instanceof IDeploymentDescriptor) {
			IDeploymentDescriptor descr = (IDeploymentDescriptor) input;
			Object[] vars = descr.getVariables().toArray();
			Object[] params = descr.getParameters().toArray();
			
			Object[] result = new Object[vars.length + params.length];
			System.arraycopy(vars,  0, result, 0, vars.length);
			System.arraycopy(params,  0, result, vars.length, params.length);
			
			return result;
		}
		
		if (input instanceof Object[]) {
			return (Object[]) input;
		}
		
		return EMPTY;
	}
	
	public Object addElment(IDeploymentDescriptor model, DescriptorMasterDetailsBlock block) {
		IVariable var = new Variable();
		var.setName(Messages.VariablesMasterDetailsProvider_DefaultVariableName);
		
		IParameter param = new Parameter();
		param.setId(Messages.ParametersMasterDetailsProvider_newParamName);
		param.setType(IParameter.STRING);
		
		Object[] input = new Object[] { var, param };
		ListDialog sd = new ListDialog(block.viewer.getControl().getShell());
		sd.setInput(input);
		sd.setContentProvider((IStructuredContentProvider) block.viewer.getContentProvider());
		sd.setLabelProvider(new DeploymentDescriptorLabelProvider());
		sd.setMessage(Messages.VarsAndParamsMasterDetailsProvider_SelectItemToAdd);
		sd.setTitle(Messages.VarsAndParamsMasterDetailsProvider_AddVariableOrParam);
			
		if (sd.open() == Window.CANCEL) {
			return null;
		}
			
		return sd.getResult()[0];
	}

	public Class getType() {
		return null;
	}

	public Object doGetParent(Object element) {
		return null;
	}
}
