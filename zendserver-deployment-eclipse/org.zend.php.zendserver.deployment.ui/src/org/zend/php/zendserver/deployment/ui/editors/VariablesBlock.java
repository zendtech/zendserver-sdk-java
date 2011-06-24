package org.zend.php.zendserver.deployment.ui.editors;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.forms.DetailsPart;
import org.eclipse.ui.forms.IDetailsPage;
import org.eclipse.ui.forms.IDetailsPageProvider;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.MasterDetailsBlock;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.zend.php.zendserver.deployment.core.descriptor.IDeploymentDescriptor;
import org.zend.php.zendserver.deployment.core.descriptor.IDescriptorChangeListener;
import org.zend.php.zendserver.deployment.core.descriptor.IVariable;
import org.zend.php.zendserver.deployment.core.internal.descriptor.Variable;

public class VariablesBlock extends DescriptorMasterDetailsBlock {

	public VariablesBlock(DeploymentDescriptorEditor editor) {
		super(editor, "Variables", "Variables to pass to application deployment scripts.");
	}
	
	protected Object[] doGetElements(Object input) {
		if (input instanceof IDeploymentDescriptor) {
			return ((IDeploymentDescriptor) input).getVariables().toArray();
		}
		
		return null;
	}
	
	protected void addElment() {
		IDeploymentDescriptor descr = editor.getModel();
		int variablesSize = descr.getVariables().size() + 1;

		IVariable param = new Variable();
		param.setName("variable" + variablesSize);
		editor.getModel().getVariables().add(param);

		viewer.refresh();
		viewer.setSelection(new StructuredSelection(param));
	}
}
