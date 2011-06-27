package org.zend.php.zendserver.deployment.ui.editors;

import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.text.IDocument;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.editor.IFormPage;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.zend.php.zendserver.deployment.core.descriptor.DescriptorContainerManager;
import org.zend.php.zendserver.deployment.core.descriptor.IDeploymentDescriptor;
import org.zend.php.zendserver.deployment.core.descriptor.IDescriptorChangeListener;
import org.zend.php.zendserver.deployment.core.descriptor.IDescriptorContainer;
import org.zend.php.zendserver.deployment.core.descriptor.IModelObject;
import org.zend.php.zendserver.deployment.core.descriptor.ResourceMapper;
import org.zend.php.zendserver.deployment.core.internal.descriptor.Feature;
import org.zend.php.zendserver.deployment.core.internal.validation.ValidationStatus;
import org.zend.php.zendserver.deployment.core.internal.validation.Validator;
import org.zend.php.zendserver.deployment.ui.Activator;


/**
 * An example showing how to create a multi-page editor. This example has 3
 * pages:
 * <ul>
 * <li>page 0 contains a nested text editor.
 * <li>page 1 allows you to change the font used in page 2
 * <li>page 2 shows the words in page 0 in sorted order
 * </ul>
 */
public class DeploymentDescriptorEditor extends FormEditor implements
		IResourceChangeListener {

	public static final String ID = "org.zend.php.zendserver.deployment.ui.editors.DeploymentDescriptorEditor";

	private SourcePage sourcePage;
	
	private Validator validator;

	protected FormToolkit createToolkit(Display display) {
		// Create a toolkit that shares colors between editors.
		return new FormToolkit(Activator.getDefault().getFormColors(display));
	}

	protected void addPages() {
		try {
			addPage(new OverviewPage(this));
			addPage(new DescriptorMasterDetailsPage(this, new VariablesMasterDetailsProvider(), "variables", "Variables"));
			addPage(new DescriptorMasterDetailsPage(this, new ParametersMasterDetailsProvider(), "parameters", "Parameters"));
			addPage(new DescriptorMasterDetailsPage(this, new DependenciesMasterDetailsProvider(), "dependencies", "Dependencies"));
			addPage(new PersistentResourcesPage(this));
			sourcePage = new SourcePage(this);
			addPage(sourcePage, getEditorInput());
		} catch (PartInitException e) {
			//
		}
	}

	private IDescriptorContainer fModel;
	private IDocumentProvider fDocumentProvider;
	private String iconLocation;

	private ResourceMapper fResourceMapper;

	/**
	 * Creates a multi-page editor example.
	 */
	public DeploymentDescriptorEditor() {
		super();
		ResourcesPlugin.getWorkspace().addResourceChangeListener(this);
		fDocumentProvider = new DescriptorDocumentProvider();
		validator = new Validator();
	}

	/**
	 * The <code>MultiPageEditorPart</code> implementation of this
	 * <code>IWorkbenchPart</code> method disposes all nested editors.
	 * Subclasses may extend.
	 */
	public void dispose() {
		ResourcesPlugin.getWorkspace().removeResourceChangeListener(this);
		super.dispose();
	}

	/**
	 * Saves the multi-page editor's document.
	 */
	public void doSave(IProgressMonitor monitor) {
		getEditor(5).doSave(monitor);
	}

	/**
	 * Saves the multi-page editor's document as another file. Also updates the
	 * text for page 0's tab, and updates this multi-page editor's input to
	 * correspond to the nested editor's.
	 */
	public void doSaveAs() {
		IEditorPart editor = getEditor(0);
		editor.doSaveAs();
		setPageText(0, editor.getTitle());
		setInput(editor.getEditorInput());
	}

	/*
	 * (non-Javadoc) Method declared on IEditorPart
	 */
	public void gotoMarker(IMarker marker) {
		setActivePage(0);
		IDE.gotoMarker(getEditor(0), marker);
	}

	/**
	 * The <code>MultiPageEditorExample</code> implementation of this method
	 * checks that the input is an instance of <code>IFileEditorInput</code>.
	 */
	public void init(IEditorSite site, IEditorInput editorInput)
			throws PartInitException {
		if (!(editorInput instanceof IFileEditorInput))
			throw new PartInitException(
					"Invalid Input: Must be IFileEditorInput");

		IFileEditorInput fileInput = (IFileEditorInput) editorInput;
		fModel = DescriptorContainerManager.getService()
				.openDescriptorContainer(fileInput.getFile());
		super.init(site, editorInput);

		try {
			fDocumentProvider.connect(editorInput);
		} catch (CoreException e) {
			throw new PartInitException(new Status(IStatus.ERROR,
					Activator.PLUGIN_ID, e.getMessage(), e));
		}
		
		fModel.connect(getDocument());
		changeIcon(fModel.getDescriptorModel().getIconLocation());
		fModel.addChangeListener(new IDescriptorChangeListener() {

			public void descriptorChanged(IModelObject target, Feature feature) {
				if (target instanceof IDeploymentDescriptor) {
					handleModelUpdate((IDeploymentDescriptor)target);
				}
			}
		});
	}

	protected void handleModelUpdate(final IDeploymentDescriptor descr) {
		getEditorSite().getShell().getDisplay().asyncExec(new Runnable() {
			
			public void run() {
				String newIconLocation = descr.getIconLocation();
				changeIcon(newIconLocation);
				
				validate(descr);
			}
		});
	}
	
	private void validate(IDeploymentDescriptor descr) {
		List<ValidationStatus> statuses = validator.validate(descr);
		
		IFormPage page = getActivePageInstance();
		
		if (page instanceof DescriptorEditorPage) {
			DescriptorEditorPage descrPage = (DescriptorEditorPage) page;
			descrPage.showStatuses(statuses);
		}
	}

	private void changeIcon(String newIconLocation) {
		if ((newIconLocation == iconLocation)
				|| (newIconLocation != null && (newIconLocation
						.equals(iconLocation)))) {
			return;
		}

		if (newIconLocation == null) {
			newIconLocation = Activator.IMAGE_DESCRIPTOR_OVERVIEW;
		}

		ImageRegistry reg = Activator.getDefault().getImageRegistry();
		Image img = reg.get(newIconLocation);
		if (img != null) {
			iconLocation = newIconLocation;
		} else {
			IFile file = null;
			try {
				file = fModel.getProject().getFile(newIconLocation);
			} catch (RuntimeException e) {
				// ignore
			}

			if (file != null && file.exists()) {
				String filePath = file.getLocation().toFile().getAbsolutePath();
				img = Activator.getDefault().createWorkspaceImage(filePath, 16);

				if (img != null) {
					iconLocation = newIconLocation;
					// reg.remove(iconLocation); // remove previous image
					reg.put(iconLocation, img);
				} else {
					iconLocation = Activator.IMAGE_DESCRIPTOR_OVERVIEW;
				}
			} else {
				iconLocation = Activator.IMAGE_DESCRIPTOR_OVERVIEW;
			}
		}

		updateImage();
	}

	private void updateImage() {
		Image img = Activator.getDefault().getImage(iconLocation);
		IFormPage page = getActivePageInstance();
		if ((page != null) && (page instanceof FormPage)) {
			FormPage fpage = (FormPage) page;
			fpage.getManagedForm().getForm().setImage(img);
		}
	}

	/*
	 * (non-Javadoc) Method declared on IEditorPart.
	 */
	public boolean isSaveAsAllowed() {
		return true;
	}

	/**
	 * Calculates the contents of page 2 when the it is activated.
	 */
	protected void pageChange(int newPageIndex) {
		super.pageChange(newPageIndex);
		updateImage();
		if (newPageIndex == 2) {
			//
		}
	}

	public void resourceChanged(IResourceChangeEvent event) {
		// TODO Auto-generated method stub

	}

	public IDeploymentDescriptor getModel() {
		return fModel.getDescriptorModel();
	}

	public IDescriptorContainer getDescriptorContainer() {
		return fModel;
	}

	public IProject getProject() {
		return fModel.getProject();
	}

	public IDocument getDocument() {
		return fDocumentProvider.getDocument(getEditorInput());
	}

	public IDocumentProvider getDocumentProvider() {
		return fDocumentProvider;
	}

	public ResourceMapper getResourceMapper() {
		if (fResourceMapper == null) {
			fResourceMapper = new ResourceMapper(fModel);
		}
		return fResourceMapper;
	}

}
