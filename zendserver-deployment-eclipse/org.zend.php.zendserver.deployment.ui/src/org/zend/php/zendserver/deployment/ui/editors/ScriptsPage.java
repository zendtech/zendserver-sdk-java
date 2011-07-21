package org.zend.php.zendserver.deployment.ui.editors;

import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.TableWrapLayout;
import org.zend.php.zendserver.deployment.core.descriptor.DeploymentDescriptorFactory;
import org.zend.php.zendserver.deployment.core.descriptor.IModelObject;
import org.zend.php.zendserver.deployment.core.descriptor.IParameter;
import org.zend.php.zendserver.deployment.core.descriptor.IVariable;
import org.zend.php.zendserver.deployment.core.internal.descriptor.Feature;
import org.zend.php.zendserver.deployment.core.internal.descriptor.Parameter;
import org.zend.php.zendserver.deployment.core.internal.descriptor.Variable;
import org.zend.php.zendserver.deployment.ui.Messages;

public class ScriptsPage extends DescriptorEditorPage {

	private ScriptsSection scripts;
	private DescriptorMasterDetailsBlock variablesBlock;

	public ScriptsPage(DeploymentDescriptorEditor editor, String id,
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

		variablesBlock.createContent(managedForm);
		
		showMarkers();
	}

	public void refresh() {
		variablesBlock.refresh();
		scripts.refresh();
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
			scripts.createDeploymentScriptsSection(managedForm, cp);
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

		
		}
	}

}