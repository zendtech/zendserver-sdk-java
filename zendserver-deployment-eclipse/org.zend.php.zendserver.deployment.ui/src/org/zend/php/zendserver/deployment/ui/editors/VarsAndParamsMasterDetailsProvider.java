package org.zend.php.zendserver.deployment.ui.editors;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.dialogs.ListDialog;
import org.zend.php.zendserver.deployment.core.descriptor.DeploymentDescriptorPackage;
import org.zend.php.zendserver.deployment.core.descriptor.IDeploymentDescriptor;
import org.zend.php.zendserver.deployment.core.descriptor.IModelContainer;
import org.zend.php.zendserver.deployment.core.descriptor.IParameter;
import org.zend.php.zendserver.deployment.core.descriptor.IVariable;
import org.zend.php.zendserver.deployment.core.internal.descriptor.Feature;
import org.zend.php.zendserver.deployment.core.internal.descriptor.Parameter;
import org.zend.php.zendserver.deployment.core.internal.descriptor.Variable;
import org.zend.php.zendserver.deployment.ui.Messages;

public class VarsAndParamsMasterDetailsProvider implements MasterDetailsProvider {

	private class Node {
		public String label;
		private Feature feature;
		private IModelContainer container;
		
		public Node(String label, Feature feature, IModelContainer container) {
			this.label = label;
			this.feature = feature;
			this.container = container;
		}
		
		@Override
		public String toString() {
			return label;
		}
	}
	
	public String getDescription() {
		return Messages.VariablesMasterDetailsProvider_VariablesDescr;
	}
	
	public Object[] doGetElements(Object input) {
		if (input instanceof IDeploymentDescriptor) {
			return new Node[] {
					new Node("Variables", DeploymentDescriptorPackage.VARIABLES, (IModelContainer)input),
					new Node("Parameters", DeploymentDescriptorPackage.PARAMETERS, (IModelContainer)input)
			};
		}
		
		if (input instanceof Node) {
			Node node = (Node) input;
			return node.container.getChildren(node.feature).toArray();
		}
		
		if (input instanceof Object[]) {
			return (Object[]) input;
		}
		
		return null;
	}
	
	public Object addElment(IDeploymentDescriptor model, DescriptorMasterDetailsBlock block) {
		int variablesSize = model.getVariables().size() + 1;

		IVariable var = new Variable();
		var.setName(Messages.VariablesMasterDetailsProvider_DefaultVariableName + variablesSize);
		
		IParameter param = new Parameter();
		param.setId(Messages.ParametersMasterDetailsProvider_newParamName+(model.getParameters().size() + 1));
		param.setType(IParameter.STRING);
		
		Object[] input = new Object[] { var, param };
		ListDialog sd = new ListDialog(block.viewer.getControl().getShell());
		sd.setInput(input);
		sd.setContentProvider((IStructuredContentProvider) block.viewer.getContentProvider());
		sd.setLabelProvider(new DeploymentDescriptorLabelProvider());
		sd.setMessage(Messages.DependenciesMasterDetailsProvider_DependencyType);
		sd.setTitle(Messages.DependenciesMasterDetailsProvider_Add);
			
		if (sd.open() == Window.CANCEL) {
			return null;
		}
			
		return sd.getResult()[0];
	}

	public Class getType() {
		return null;
	}
}
