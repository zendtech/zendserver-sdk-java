package org.zend.php.zendserver.deployment.ui.editors;

import java.util.List;

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

	private static final Object[] EMPTY = new Object[0];
	
	public String getDescription() {
		return Messages.VarsAndParamsMasterDetailsProvider_DefineVarsAndParams;
	}
	
	private Node[] rootNodes;
	
	public Object[] doGetElements(Object input) {
		if (input instanceof IDeploymentDescriptor) {
			IDeploymentDescriptor descr = (IDeploymentDescriptor) input;
			List<IVariable> vars = descr.getVariables();
			List<IParameter> params = descr.getParameters();
			if (vars.size() > 0 && params.size() > 0) {
				if (rootNodes == null) {
					rootNodes = new Node[] {
							new Node(Messages.VarsAndParamsMasterDetailsProvider_1, DeploymentDescriptorPackage.VARIABLES, (IModelContainer)input),
							new Node(Messages.VarsAndParamsMasterDetailsProvider_2, DeploymentDescriptorPackage.PARAMETERS, (IModelContainer)input)
					};
				}
				return rootNodes;
			} else if (vars.size() > 0) {
				return vars.toArray();
			} else if (params.size() > 0) {
				return params.toArray();
			}
		}
		
		if (input instanceof Node) {
			Node node = (Node) input;
			return node.container.getChildren(node.feature).toArray();
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
		if (rootNodes != null) {
			if (element instanceof IVariable) {
				return rootNodes[0];
			} else if (element instanceof IParameter) {
				return rootNodes[1];
			}
		}
		return null;
	}
}
