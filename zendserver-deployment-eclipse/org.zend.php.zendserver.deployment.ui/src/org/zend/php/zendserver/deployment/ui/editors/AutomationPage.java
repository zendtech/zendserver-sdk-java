package org.zend.php.zendserver.deployment.ui.editors;

import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;
import org.zend.php.zendserver.deployment.core.descriptor.DeploymentDescriptorFactory;
import org.zend.php.zendserver.deployment.core.descriptor.IModelObject;
import org.zend.php.zendserver.deployment.core.descriptor.IParameter;
import org.zend.php.zendserver.deployment.core.descriptor.IVariable;
import org.zend.php.zendserver.deployment.core.internal.descriptor.Feature;
import org.zend.php.zendserver.deployment.core.internal.descriptor.Parameter;
import org.zend.php.zendserver.deployment.core.internal.descriptor.Variable;
import org.zend.php.zendserver.deployment.ui.HelpContextIds;
import org.zend.php.zendserver.deployment.ui.Messages;

public class AutomationPage extends DescriptorEditorPage {

	private ScriptsSection scripts;
	private DescriptorMasterDetailsBlock variablesBlock;

	public AutomationPage(DeploymentDescriptorEditor editor, String id,
			String title) {
		super(editor, id, title);

		VarsAndParamsMasterDetailsProvider variablesProvider = new VarsAndParamsMasterDetailsProvider();
		variablesBlock = new ParametersVariablesBlock(editor,
				variablesProvider, Messages.ScriptsPage_VarsAndParams,
				variablesProvider.getDescription());
		scripts = new ScriptsSection(editor);
	}

	@Override
	protected void createFormContent(IManagedForm managedForm) {
		super.createFormContent(managedForm);

		ScrolledForm form = managedForm.getForm();
		FormToolkit toolkit = managedForm.getToolkit();
		form.getBody().setLayout(
				FormLayoutFactory.createFormTableWrapLayout(true, 1));
		
		final Composite body = managedForm.getForm().getBody();
		
		Composite up = toolkit.createComposite(body);
		TableWrapLayout layout = FormLayoutFactory.createFormTableWrapLayout(true, 2);
		up.setBackground(body.getBackground());
		layout.bottomMargin = layout.horizontalSpacing = layout.verticalSpacing = layout.leftMargin = layout.rightMargin = layout.topMargin = 0;
		up.setLayout(layout);
		up.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		scripts.createDeploymentScriptsSection(managedForm, up);
		
		createInfoSection(toolkit, up);
		
		variablesBlock.createContent(managedForm);
		
		showMarkers();
	}

	private void createInfoSection(FormToolkit toolkit, Composite parent) {
		Section section = toolkit.createSection(parent,
				Section.DESCRIPTION | Section.TITLE_BAR | Section.TWISTIE | Section.EXPANDED);
		section.marginWidth = 5;
		section.setText(Messages.AutomationPage_WhatsAutomation);
		section.setDescription(Messages.AutomationPage_Description);
		Composite sectionClient = toolkit.createComposite(section);
		section.setClient(sectionClient);
		section.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB, TableWrapData.TOP));
		sectionClient.setLayout(new GridLayout(3, false));	
	}

	public void refresh() {
		variablesBlock.refresh();
		scripts.refresh();
	}
	
	@Override
	public void showMarkers() {
		super.showMarkers();
		variablesBlock.showMarkers();
	}

	private final class ParametersVariablesBlock extends
			DescriptorMasterDetailsBlock {
		private ParametersVariablesBlock(DeploymentDescriptorEditor editor,
				MasterDetailsProvider prov, String title, String description) {
			super(editor, prov, title, description);
		}

		@Override
		protected void createMasterPart(IManagedForm managedForm,
				Composite parent) {
			Composite cp = managedForm.getToolkit().createComposite(parent);
			TableWrapLayout tw = new TableWrapLayout();
			tw.numColumns = 1;
			cp.setLayout((tw));
			super.createMasterPart(managedForm, cp);
		}

		protected void createModelElement(IModelObject element) {
			Feature feature = DeploymentDescriptorFactory
					.getFeature(element);
			editor.getModel().add(feature, element);
			Object[] expanded = viewer.getExpandedElements();
			viewer.refresh();
			viewer.setExpandedElements(expanded);
			viewer.setSelection(new StructuredSelection(element));
		}
		
		
		@Override
		protected void addButtons(FormToolkit toolkit, Composite buttons) {
			Button addButton1 = createButton(toolkit, buttons, Messages.ScriptsPage_AddVariable);
			addButton1.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					// TODO: replace with a wizard
					IVariable var = new Variable();
					var.setName(Messages.VariablesMasterDetailsProvider_DefaultVariableName);
					createModelElement(var);
				}
			});

			Button addButton2 = createButton(toolkit, buttons, Messages.ScriptsPage_AddParameter);
			addButton2.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					// TODO: replace with a wizard
					IParameter param = new Parameter();
					param.setId(Messages.ParametersMasterDetailsProvider_newParamName);
					param.setType(IParameter.STRING);
					createModelElement(param);
				}

			});

			removeButton = createButton(toolkit, buttons,
					Messages.DescriptorMasterDetailsBlock_Remove);
			removeButton.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					removeElement(viewer.getSelection());
					final TreeItem[] expandedElements = viewer.getTree().getItems();
					if (expandedElements != null && expandedElements.length > 0) {
						viewer.getTree().setSelection(expandedElements[0]);
					}

				}
			});
		
			GridData gd1 = (GridData) addButton1.getLayoutData();
			GridData gd2 = (GridData) addButton2.getLayoutData();
			GridData gd3 = (GridData) removeButton.getLayoutData();
			int maxsize = Math.max(Math.max(gd1.widthHint, gd2.widthHint), gd3.widthHint);
			gd1.widthHint = maxsize;
			gd2.widthHint = maxsize;
			gd3.widthHint = maxsize;
		}
	}
	
	@Override
	protected String getHelpResource() {
		return HelpContextIds.TRIGGERS_TAB;
	}

}