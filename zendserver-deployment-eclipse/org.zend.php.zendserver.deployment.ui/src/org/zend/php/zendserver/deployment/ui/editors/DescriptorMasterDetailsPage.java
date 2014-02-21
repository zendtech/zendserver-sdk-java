package org.zend.php.zendserver.deployment.ui.editors;

import org.eclipse.ui.forms.IManagedForm;
import org.zend.php.zendserver.deployment.ui.HelpContextIds;

public class DescriptorMasterDetailsPage extends DescriptorEditorPage  {

	private DescriptorMasterDetailsBlock block;
	
	public DescriptorMasterDetailsPage(DeploymentDescriptorEditor editor, MasterDetailsProvider provider, String id, String title) {
		super(editor, id, title);
		
		block = new DescriptorMasterDetailsBlock(editor, provider, title, provider.getDescription());
	}
	

	@Override
	protected void createFormContent(IManagedForm managedForm) {
		super.createFormContent(managedForm);		

		block.createContent(managedForm);
	}
	
	public void refresh() {
		block.refresh();
	}
	
	@Override
	public void showMarkers() {
		super.showMarkers();
		block.showMarkers();
	}
	
	@Override
	protected String getHelpResource() {
		return HelpContextIds.DEPENDENCIES_TAB;
	}
}
