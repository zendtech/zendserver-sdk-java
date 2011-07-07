package org.zend.php.zendserver.deployment.ui.editors;

import org.eclipse.ui.forms.IManagedForm;
import org.zend.php.zendserver.deployment.ui.Messages;

public class ScriptsPage extends DescriptorEditorPage  {

	private ScriptsSection scripts;
	private DescriptorMasterDetailsBlock variablesBlock;
	//private DescriptorMasterDetailsBlock parametersBlock;
	
	public ScriptsPage(DeploymentDescriptorEditor editor, String id, String title) {
		super(editor, id, title);
		
		
		VarsAndParamsMasterDetailsProvider variablesProvider = new VarsAndParamsMasterDetailsProvider();
		variablesBlock = new DescriptorMasterDetailsBlock(editor, variablesProvider, Messages.DeploymentDescriptorEditor_Variables, variablesProvider.getDescription());
		scripts = new ScriptsSection(editor);
		
		//ParametersMasterDetailsProvider paramsProvider = new ParametersMasterDetailsProvider();
		//parametersBlock = new DescriptorMasterDetailsBlock(editor, paramsProvider, Messages.DeploymentDescriptorEditor_Parameters, paramsProvider.getDescription());
	}
	

	@Override
	protected void createFormContent(IManagedForm managedForm) {
		super.createFormContent(managedForm);		

		variablesBlock.createContent(managedForm);
		scripts.createDeploymentScriptsSection(managedForm);
		//parametersBlock.createContent(managedForm);
	}
	
	public void refresh() {
		variablesBlock.refresh();
		//parametersBlock.refresh();
		scripts.refresh();
	}
}