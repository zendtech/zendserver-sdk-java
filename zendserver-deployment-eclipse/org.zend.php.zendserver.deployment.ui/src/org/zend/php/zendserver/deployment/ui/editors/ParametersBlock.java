package org.zend.php.zendserver.deployment.ui.editors;

import org.eclipse.core.runtime.CoreException;
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
import org.eclipse.ui.forms.widgets.Section;
import org.zend.php.zendserver.deployment.core.descriptor.IDeploymentDescriptor;
import org.zend.php.zendserver.deployment.core.descriptor.IDescriptorChangeListener;
import org.zend.php.zendserver.deployment.core.descriptor.IParameter;
import org.zend.php.zendserver.deployment.core.internal.descriptor.Parameter;


public class ParametersBlock extends MasterDetailsBlock {
	
	private static class MasterContentProvider implements IStructuredContentProvider {

		public void dispose() {
			// empty
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			// empty
		}

		public Object[] getElements(Object input) {
			if (input instanceof IDeploymentDescriptor) {
				return ((IDeploymentDescriptor) input).getParameters().toArray();
			}
			
			return null;
		}
		
	}
	
	private DeploymentDescriptorEditor editor;
	private TableViewer viewer;

	public ParametersBlock(DeploymentDescriptorEditor editor) {
		this.editor = editor;
	}
	
	@Override
	protected void createMasterPart(final IManagedForm managedForm, Composite parent) {
		FormToolkit toolkit = managedForm.getToolkit();
		
		Section section = toolkit.createSection(parent, Section.DESCRIPTION|Section.TITLE_BAR);
		section.marginWidth = 5;
		section.marginHeight = 5;
		section.setText("Parameters");
		section.setDescription("Following information will be required in order\nto deploy application.");
		final SectionPart spart = new SectionPart(section);
		managedForm.addPart(spart);
		
		Composite client = toolkit.createComposite(section, SWT.NONE);
		section.setClient(client);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		layout.marginWidth = 2;
		layout.marginHeight = 2;
		client.setLayout(layout);
		
		Table table = toolkit.createTable(client, SWT.H_SCROLL|SWT.V_SCROLL);
		GridData gd = new GridData(GridData.FILL, GridData.FILL, true, true);
		gd.widthHint = 200;
		table.setLayoutData(gd);
		
		Composite buttons = toolkit.createComposite(client, SWT.NONE);
		layout = new GridLayout(1, false);
		buttons.setLayout(layout);
		gd = new GridData(SWT.BEGINNING, SWT.TOP, false, false);
		buttons.setLayoutData(gd);
		
		Button addButton = toolkit.createButton(buttons, "Add", SWT.NONE);
		gd = new GridData(SWT.FILL, SWT.TOP, true, false);
		addButton.setLayoutData(gd);
		addButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				addElment();
			}
		});
		Button removeButton = toolkit.createButton(buttons, "Remove", SWT.NONE);
		gd = new GridData(SWT.FILL, SWT.TOP, true, false);
		removeButton.setLayoutData(gd);
		removeButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				removeElement(viewer.getSelection());
			}
		});
		
		viewer = new TableViewer(table);
		viewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				managedForm.fireSelectionChanged(spart, event.getSelection());
				managedForm.getForm().reflow(true);
			}
		});
		viewer.setContentProvider(new MasterContentProvider());
		viewer.setLabelProvider(new DeploymentDescriptorLabelProvider());
		viewer.setInput(editor.getModel());
		editor.getDescriptorContainer().addChangeListener(new IDescriptorChangeListener() {
			
			public void descriptorChanged(Object target) {
				refreshViewer(target);
			}
		});
	}

	protected void refreshViewer(final Object target) {
		viewer.getControl().getDisplay().asyncExec(new Runnable() {

			public void run() {
				viewer.refresh(target);
			}
		});
	}

	protected void removeElement(ISelection selection) {
		IStructuredSelection sel = (IStructuredSelection) selection;
		Object elem = sel.getFirstElement();
		if (elem instanceof IParameter) {
			IParameter param = (IParameter) elem;
			editor.getModel().getParameters().remove(param);
		}
		viewer.refresh();
	}

	protected void addElment() {
		IDeploymentDescriptor model = editor.getModel();
		IParameter param = new Parameter();
		param.setId("parameter"+(model.getParameters().size() + 1));
		param.setType(IParameter.STRING);
		model.getParameters().add(param);
		viewer.refresh();
		viewer.setSelection(new StructuredSelection(param));
	}

	@Override
	protected void registerPages(DetailsPart detailsPart) {
		detailsPart.setPageProvider(new IDetailsPageProvider() {
			
			private ParameterDetailsPage page = new ParameterDetailsPage(editor);
			
			public Object getPageKey(Object object) {
				return "key";
			}
			
			public IDetailsPage getPage(Object key) {
				return page;
			}
		});
	}

	@Override
	protected void createToolBarActions(IManagedForm managedForm) {
		// TODO Auto-generated method stub

	}

	public void refresh() {
		viewer.refresh();
		detailsPart.refresh();
	}

}
