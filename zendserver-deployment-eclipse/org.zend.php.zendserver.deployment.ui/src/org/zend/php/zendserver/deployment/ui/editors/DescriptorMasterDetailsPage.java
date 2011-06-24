package org.zend.php.zendserver.deployment.ui.editors;

import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.zend.php.zendserver.deployment.ui.actions.DeployAppInCloudAction;
import org.zend.php.zendserver.deployment.ui.actions.ExportApplicationAction;
import org.zend.php.zendserver.deployment.ui.actions.RunApplicationAction;

public class DescriptorMasterDetailsPage extends DescriptorEditorPage  {

	private DescriptorMasterDetailsBlock block;
	
	public DescriptorMasterDetailsPage(DeploymentDescriptorEditor editor, MasterDetailsProvider provider, String id, String title) {
		super(editor, id, title);
		
		block = new DescriptorMasterDetailsBlock(editor, provider, title, provider.getDescription());
	}
	

	@Override
	protected void createFormContent(IManagedForm managedForm) {
		ScrolledForm form = managedForm.getForm();
		form.setText(getTitle());
		IToolBarManager mgr = form.getToolBarManager();
		mgr.add(new RunApplicationAction());
		mgr.add(new DeployAppInCloudAction());
		mgr.add(new ExportApplicationAction());
		mgr.update(true);
		
		block.createContent(managedForm);
	}
	
	public void refresh() {
		block.refresh();
	}
}
