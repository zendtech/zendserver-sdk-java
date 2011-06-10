package org.zend.php.zendserver.deployment.ui.editors;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
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
import org.zend.php.zendserver.deployment.core.descriptor.IDeploymentDescriptorModifier;
import org.zend.php.zendserver.deployment.core.descriptor.IDescriptorChangeListener;
import org.zend.php.zendserver.deployment.core.descriptor.IParameter;
import org.zend.php.zendserver.deployment.core.internal.descriptor.Parameter;
import org.zend.php.zendserver.deployment.ui.Activator;


public class ParametersBlock extends MasterDetailsBlock {
	
	private static class MasterContentProvider implements IStructuredContentProvider {

		public void dispose() {
			// empty
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			// empty
		}

		public Object[] getElements(Object input) {
			if (input instanceof IDeploymentDescriptorModifier) {
				input = ((IDeploymentDescriptorModifier) input).getDescriptor();
			}
			
			if (input instanceof IDeploymentDescriptor) {
				return ((IDeploymentDescriptor) input).getParameters().toArray();
			}
			
			return null;
		}
		
	}
	
	private static class MasterLabelProvider extends LabelProvider {
		
		@Override
		public Image getImage(Object element) {
			IParameter param = (IParameter) element;
			
			String type = param.getType();
			
			if (IParameter.PASSWORD.equals(type)) {
				return Activator.getDefault().getImage(Activator.IMAGE_PARAMTYPE_PASSWORD);
			} else if (IParameter.STRING.equals(type)) {
				return Activator.getDefault().getImage(Activator.IMAGE_PARAMTYPE_STRING);
			} else if (IParameter.NUMBER.equals(type)) {
				return Activator.getDefault().getImage(Activator.IMAGE_PARAMTYPE_NUMBER);
			} else if (IParameter.CHOICE.equals(type)) {
				return Activator.getDefault().getImage(Activator.IMAGE_PARAMTYPE_CHOICE);
			} else if (IParameter.CHECKBOX.equals(type)) {
				return Activator.getDefault().getImage(Activator.IMAGE_PARAMTYPE_CHECKBOX);
			} else if (IParameter.HOSTNAME.equals(type)) {
				return Activator.getDefault().getImage(Activator.IMAGE_PARAMTYPE_HOSTNAME);
			} else if (IParameter.EMAIL.equals(type)) {
				return Activator.getDefault().getImage(Activator.IMAGE_PARAMTYPE_EMAIL);
			}
			
			return Activator.getDefault().getImage(Activator.IMAGE_PARAMTYPE_UNKNOWN);
		}
		
		@Override
		public String getText(Object element) {
			IParameter param = (IParameter) element;
			
			StringBuilder sb = new StringBuilder();
			
			String label = param.getDisplay();
			if (label == null || label.trim().equals("")) {
				sb.append(param.getId());
			} else {
				sb.append(label);
			}
			
			if (param.isRequired()) {
				sb.append("*");
			}
			
			String defaultVal = param.getDefaultValue();
			if (defaultVal != null && !defaultVal.trim().equals("")) {
				sb.append(" = "+defaultVal);
			}
			
			String type = param.getType();
			if (type != null) {
				sb.append(" (").append(type).append(")");
			}
			
			return sb.toString();
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
		viewer.setLabelProvider(new MasterLabelProvider());
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
			try {
				editor.getModel().removeParameter(param);
			} catch (CoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		viewer.refresh();
	}

	protected void addElment() {
		IDeploymentDescriptorModifier model = editor.getModel();
		IParameter param = new Parameter("parameter"+(model.getDescriptor().getParameters().size() + 1), IParameter.STRING);
		try {
			model.addParameter(param);
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
