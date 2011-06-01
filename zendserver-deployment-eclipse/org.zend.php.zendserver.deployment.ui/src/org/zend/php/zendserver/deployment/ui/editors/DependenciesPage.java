package org.zend.php.zendserver.deployment.ui.editors;

import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.menus.CommandContributionItem;
import org.eclipse.ui.menus.CommandContributionItemParameter;
import org.zend.php.zendserver.deployment.ui.Activator;
import org.zend.php.zendserver.deployment.ui.actions.ExportApplicationAction;
import org.zend.php.zendserver.deployment.ui.actions.RunApplicationAction;


public class DependenciesPage extends DescriptorEditorPage {

	private DependenciesBlock block;

	public DependenciesPage(DeploymentDescriptorEditor editor) {
		super(editor, "dependencies", "Dependencies");
		
		block = new DependenciesBlock(editor);
	}
	
	@Override
	protected void createFormContent(IManagedForm managedForm) {
		ScrolledForm form = managedForm.getForm();
		form.setText(getTitle());
		IToolBarManager mgr = form.getToolBarManager();
		mgr.add(new RunApplicationAction());
		
		CommandContributionItemParameter params = new CommandContributionItemParameter(getSite(), "id", "org.zend.php.zendserver.deployment.ui.deploy", null, Activator.getImageDescriptor(Activator.IMAGE_RUN_APPLICATION), null, null, "Run", null, "Deploy application to Zend Server", 0, "", true);
		
		mgr.add(new CommandContributionItem(params));
		mgr.add(new ExportApplicationAction());
		mgr.update(true);
		
		block.createContent(managedForm);
	}

	public void refresh() {
		block.refresh();
	}
}