package org.zend.php.zendserver.deployment.ui.editors;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.layout.PixelConverter;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.DecoratingLabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.forms.DetailsPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.MasterDetailsBlock;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.zend.php.zendserver.deployment.core.descriptor.ChangeEvent;
import org.zend.php.zendserver.deployment.core.descriptor.DeploymentDescriptorFactory;
import org.zend.php.zendserver.deployment.core.descriptor.IDescriptorChangeListener;
import org.zend.php.zendserver.deployment.core.internal.descriptor.Feature;
import org.zend.php.zendserver.deployment.ui.Messages;
import org.zend.php.zendserver.deployment.ui.wizards.NewDependencyWizard;

public class DescriptorMasterDetailsBlock extends MasterDetailsBlock {

	private class MasterContentProvider implements ITreeContentProvider {

		public void dispose() {
			// empty
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			// empty
		}

		public Object[] getElements(Object input) {
			return provider.doGetElements(input);
		}

		public Object[] getChildren(Object parentElement) {
			return provider.doGetElements(parentElement);
		}

		public Object getParent(Object element) {
			return provider.doGetParent(element);
		}

		public boolean hasChildren(Object element) {
			Object[] obj = getChildren(element);
			return obj != null && obj.length > 0;
		}

	}

	protected DeploymentDescriptorEditor editor;
	protected TreeViewer viewer;
	protected String title;
	protected String description;
	protected MasterDetailsProvider provider;
	protected Button removeButton;

	public DescriptorMasterDetailsBlock(DeploymentDescriptorEditor editor,
			MasterDetailsProvider prov, String title, String description) {
		this.editor = editor;
		this.provider = prov;
		this.title = title;
		this.description = description;
	}

	@Override
	protected void createMasterPart(final IManagedForm managedForm,
			Composite parent) {
		FormToolkit toolkit = managedForm.getToolkit();
		
		final Section section = toolkit.createSection(parent, Section.DESCRIPTION
				| Section.TITLE_BAR | Section.TWISTIE | Section.EXPANDED);
		section.setText(title);
		section.setDescription(provider.getDescription());
		final SectionPart spart = new SectionPart(section);
		managedForm.addPart(spart);
		TableWrapData tdd = new TableWrapData(TableWrapData.FILL_GRAB,
				TableWrapData.FILL_GRAB);
		section.setLayoutData(tdd);

		Composite client = toolkit.createComposite(section, SWT.NONE);
		section.setClient(client);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		layout.marginWidth = 2;
		layout.marginHeight = 2;
		client.setLayout(layout);
		client.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		Tree tree = toolkit.createTree(client, SWT.H_SCROLL | SWT.V_SCROLL);
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd.widthHint = 200;
		gd.heightHint = 400;
		tree.setLayoutData(gd);

		Composite buttons = toolkit.createComposite(client, SWT.NONE);
		layout = new GridLayout(1, false);
		buttons.setLayout(layout);
		gd = new GridData(SWT.BEGINNING, SWT.TOP, false, false);
		buttons.setLayoutData(gd);

		addButtons(toolkit, buttons);

		viewer = new TreeViewer(tree);
		viewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				managedForm.fireSelectionChanged(spart, event.getSelection());
				managedForm.getForm().reflow(true);
				updateButtonsEnabledState();
			}
		});
		viewer.setContentProvider(new MasterContentProvider());
		DecoratingLabelProvider labelProvider = new DecoratingLabelProvider(
				new DeploymentDescriptorLabelProvider(),
				new DeploymentDescriptorLabelDecorator(editor));
		viewer.setLabelProvider(labelProvider);
		viewer.setInput(editor.getModel());
		editor.getModel().addListener(new IDescriptorChangeListener() {

			public void descriptorChanged(final ChangeEvent event) {
				Display.getDefault().asyncExec(new Runnable() {
					public void run() {
						if (!section.isDisposed()) {
							section.setDescription(provider.getDescription());
							refreshViewer(event.target);
						}
					}
				});
			}
		});
		
		toolkit.paintBordersFor(client);
	}

	protected void addButtons(FormToolkit toolkit, Composite buttons) {
		Button addButton = createButton(toolkit, buttons,
				Messages.DescriptorMasterDetailsBlock_Add);
		addButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				final Object result = provider.addElment(editor.getModel(),
						DescriptorMasterDetailsBlock.this);
				if (result == null) {
					return;
				}

				NewDependencyWizard wizard = new NewDependencyWizard(result);
				WizardDialog dialog = new WizardDialog(e.display
						.getActiveShell(), wizard);
				dialog.create();
				setDialogSize(dialog, 420, 300);
				dialog.open();
				if (dialog.getReturnCode() == Window.CANCEL) {
					return;
				}

				Feature feature = DeploymentDescriptorFactory
						.getFeature(result);
				editor.getModel().add(feature, result);
				Object[] expanded = viewer.getExpandedElements();
				viewer.refresh();
				viewer.setExpandedElements(expanded);
				viewer.setSelection(new StructuredSelection(result));

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

	protected Button createButton(FormToolkit toolkit, Composite buttons,
			String message) {
		Button button = toolkit.createButton(buttons, message, SWT.NONE);
		GridData gd = new GridData(
				SWT.FILL | GridData.VERTICAL_ALIGN_BEGINNING, SWT.TOP, true,
				false);
		button.setLayoutData(gd);

		// Set the default button size
		button.setFont(JFaceResources.getDialogFont());
		PixelConverter converter = new PixelConverter(button);
		int widthHint = converter
				.convertHorizontalDLUsToPixels(IDialogConstants.BUTTON_WIDTH);
		gd.widthHint = Math.max(widthHint,
				button.computeSize(SWT.DEFAULT, SWT.DEFAULT, true).x);
		return button;
	}

	private void updateButtonsEnabledState() {
		boolean isEmpty = viewer.getSelection().isEmpty();
		removeButton.setEnabled(!isEmpty);
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
		if (elem == null) {
			return;
		}

		Feature feature = DeploymentDescriptorFactory.getFeature(elem);
		editor.getModel().getChildren(feature).remove(elem);
		viewer.refresh();
	}

	@Override
	protected void registerPages(DetailsPart detailsPart) {
		detailsPart.setPageProvider(new DetailsPageProvider(editor, provider
				.getType()));
	}

	@Override
	protected void createToolBarActions(IManagedForm managedForm) {
		// TODO Auto-generated method stub

	}

	public void refresh() {
		viewer.refresh();
		detailsPart.refresh();
		updateButtonsEnabledState();
		showMarkers();
	}

	public static void setDialogSize(Dialog dialog, int width, int height) {
		Point computedSize = dialog.getShell().computeSize(SWT.DEFAULT,
				SWT.DEFAULT);
		width = Math.max(computedSize.x, width);
		height = Math.max(computedSize.y, height);
		dialog.getShell().setSize(width, height);
	}

	public void showMarkers() {
		viewer.refresh();
		DescriptorDetailsPage page = ((DescriptorDetailsPage)detailsPart.getCurrentPage());
		if (page != null) {
			page.showMarkers();
		}
	}
	
	@Override
	protected void applyLayout(Composite parent) {
		parent.setLayout(FormLayoutFactory.createFormGridLayout(true, 1));
	}
}
