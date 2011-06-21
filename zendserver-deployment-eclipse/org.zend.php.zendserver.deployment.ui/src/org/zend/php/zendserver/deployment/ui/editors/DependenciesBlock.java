package org.zend.php.zendserver.deployment.ui.editors;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.dialogs.ListDialog;
import org.eclipse.ui.forms.DetailsPart;
import org.eclipse.ui.forms.IDetailsPage;
import org.eclipse.ui.forms.IDetailsPageProvider;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.MasterDetailsBlock;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.zend.php.zendserver.deployment.core.descriptor.DeploymentDescriptorFactory;
import org.zend.php.zendserver.deployment.core.descriptor.IDeploymentDescriptor;
import org.zend.php.zendserver.deployment.core.descriptor.IDescriptorChangeListener;
import org.zend.php.zendserver.deployment.core.descriptor.IDirectiveDependency;
import org.zend.php.zendserver.deployment.core.descriptor.IExtensionDependency;
import org.zend.php.zendserver.deployment.core.descriptor.IPHPDependency;
import org.zend.php.zendserver.deployment.core.descriptor.IZendComponentDependency;
import org.zend.php.zendserver.deployment.core.descriptor.IZendFrameworkDependency;
import org.zend.php.zendserver.deployment.core.internal.descriptor.Feature;
import org.zend.php.zendserver.deployment.core.internal.descriptor.ZendServerDependency;


public class DependenciesBlock extends MasterDetailsBlock {

	private static class MasterContentProvider implements IStructuredContentProvider {

		public void dispose() {
			// empty
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			// empty
		}

		public Object[] getElements(Object input) {
			IDeploymentDescriptor descr = (IDeploymentDescriptor) input;
			List all = new ArrayList();
			all.addAll(descr.getPHPDependencies());
			all.addAll(descr.getDirectiveDependencies());
			all.addAll(descr.getExtensionDependencies());
			all.addAll(descr.getZendFrameworkDependencies());
			all.addAll(descr.getZendServerDependencies());
			all.addAll(descr.getZendComponentDependencies());
			
			if (input instanceof IDeploymentDescriptor) {
				return all.toArray();
			}
			
			if (input instanceof Object[]) {
				return (Object[])input;
			}
			
			return null;
		}
		
	}
	
	private DeploymentDescriptorEditor editor;
	private TableViewer viewer;

	public DependenciesBlock(DeploymentDescriptorEditor editor) {
		this.editor = editor;
	}
	
	@Override
	protected void createMasterPart(final IManagedForm managedForm, Composite parent) {
		FormToolkit toolkit = managedForm.getToolkit();
		
		Section section = toolkit.createSection(parent, Section.DESCRIPTION|Section.TITLE_BAR);
		section.marginWidth = 5;
		section.marginHeight = 5;
		section.setText("Dependencies");
		section.setDescription("Following will be required in order to install application.");
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
		
		Feature feature = DeploymentDescriptorFactory.getFeature(elem);
		editor.getModel().getChildren(feature).remove(elem);
		viewer.refresh();
	}

	protected void addElment() {
		Object[] input = new Object[] {
			DeploymentDescriptorFactory.createModelElement(IDeploymentDescriptor.DEPENDENCIES_PHP),
			DeploymentDescriptorFactory.createModelElement(IDeploymentDescriptor.DEPENDENCIES_DIRECTIVE),
			DeploymentDescriptorFactory.createModelElement(IDeploymentDescriptor.DEPENDENCIES_EXTENSION),
			DeploymentDescriptorFactory.createModelElement(IDeploymentDescriptor.DEPENDENCIES_ZENDFRAMEWORK),
			DeploymentDescriptorFactory.createModelElement(IDeploymentDescriptor.DEPENDENCIES_ZENDSERVER),
			DeploymentDescriptorFactory.createModelElement(IDeploymentDescriptor.DEPENDENCIES_ZSCOMPONENT),
		};
		
		ListDialog sd = new ListDialog(sashForm.getShell());
		sd.setInput(input);
		sd.setContentProvider(new MasterContentProvider());
		sd.setLabelProvider(new DeploymentDescriptorLabelProvider());
		sd.setMessage("Dependency Type:");
		sd.setTitle("Add Dependency");
		
		if (sd.open() == Window.CANCEL) {
			return;
		}
		
		Object result = sd.getResult()[0];
		Feature feature = DeploymentDescriptorFactory.getFeature(result);
		
		editor.getModel().add(feature, result);
		viewer.refresh();
		viewer.setSelection(new StructuredSelection(result));
	}

	@Override
	protected void registerPages(DetailsPart detailsPart) {
		detailsPart.setPageProvider(new IDetailsPageProvider() {
			
			private IDetailsPage phpPage = new PHPDependencyDetailsPage(editor);
			private IDetailsPage dirPage = new DirectiveDependencyDetailsPage(editor);
			private IDetailsPage extensionPage = new ExtensionDependencyDetailsPage(editor);
			private IDetailsPage zsPage = new ZendServerDependencyDetailsPage(editor);
			private IDetailsPage zfPage = new ZendFrameworkDependencyDetailsPage(editor);
			private IDetailsPage zscompPage = new ZendComponentDependencyDetailsPage(editor);
			
			public Object getPageKey(Object object) {
				return object.getClass();
			}
			
			public IDetailsPage getPage(Object key) {
				Class clazz = (Class) key;
				if (IPHPDependency.class.isAssignableFrom(clazz)) {
					return phpPage;
				}
				if (IDirectiveDependency.class.isAssignableFrom(clazz)) {
					return extensionPage;
				}
				if (IExtensionDependency.class.isAssignableFrom(clazz)) {
					return dirPage;
				}
				if (IZendFrameworkDependency.class.isAssignableFrom(clazz)) {
					return zsPage;
				}
				if (ZendServerDependency.class.isAssignableFrom(clazz)) {
					return zfPage;
				}
				if (IZendComponentDependency.class.isAssignableFrom(clazz)) {
					return zscompPage;
				}
				return null;
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
