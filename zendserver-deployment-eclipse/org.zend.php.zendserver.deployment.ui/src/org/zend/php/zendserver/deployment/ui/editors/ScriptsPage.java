package org.zend.php.zendserver.deployment.ui.editors;

import org.eclipse.ui.forms.IManagedForm;
import org.zend.php.zendserver.deployment.ui.Messages;

public class ScriptsPage extends DescriptorEditorPage  {

	private DescriptorMasterDetailsBlock variablesBlock;
	private DescriptorMasterDetailsBlock parametersBlock;
	
	public ScriptsPage(DeploymentDescriptorEditor editor, String id, String title) {
		super(editor, id, title);
		
		VariablesMasterDetailsProvider variablesProvider = new VariablesMasterDetailsProvider();
		variablesBlock = new DescriptorMasterDetailsBlock(editor, variablesProvider, Messages.DeploymentDescriptorEditor_Variables, variablesProvider.getDescription());
		
		ParametersMasterDetailsProvider paramsProvider = new ParametersMasterDetailsProvider();
		parametersBlock = new DescriptorMasterDetailsBlock(editor, paramsProvider, Messages.DeploymentDescriptorEditor_Parameters, paramsProvider.getDescription());
	}
	

	@Override
	protected void createFormContent(IManagedForm managedForm) {
		super.createFormContent(managedForm);		

		variablesBlock.createContent(managedForm);
		parametersBlock.createContent(managedForm);
	}
	
	public void refresh() {
		variablesBlock.refresh();
		parametersBlock.refresh();
	}
}