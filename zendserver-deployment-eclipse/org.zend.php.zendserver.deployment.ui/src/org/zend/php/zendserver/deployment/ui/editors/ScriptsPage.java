package org.zend.php.zendserver.deployment.ui.editors;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.TableWrapLayout;
import org.zend.php.zendserver.deployment.ui.Messages;

public class ScriptsPage extends DescriptorEditorPage  {

	private ScriptsSection scripts;
	private DescriptorMasterDetailsBlock variablesBlock;
	
	public ScriptsPage(DeploymentDescriptorEditor editor, String id, String title) {
		super(editor, id, title);
		
		
		VarsAndParamsMasterDetailsProvider variablesProvider = new VarsAndParamsMasterDetailsProvider();
		variablesBlock = new DescriptorMasterDetailsBlock(editor, variablesProvider, Messages.ScriptsPage_VarsAndParams, variablesProvider.getDescription()) {
			@Override
			protected void createMasterPart(IManagedForm managedForm,
					Composite parent) {
				Composite cp = managedForm.getToolkit().createComposite(parent);
				TableWrapLayout tw = new TableWrapLayout();
				tw.numColumns = 1;
				cp.setLayout((tw));
				super.createMasterPart(managedForm, cp);
				scripts.createDeploymentScriptsSection(managedForm, cp);
			}
		};
		scripts = new ScriptsSection(editor);
	}
	

	@Override
	protected void createFormContent(IManagedForm managedForm) {
		super.createFormContent(managedForm);		

		variablesBlock.createContent(managedForm);
	}
	
	public void refresh() {
		variablesBlock.refresh();
		scripts.refresh();
	}
}