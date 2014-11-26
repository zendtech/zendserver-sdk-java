package org.zend.php.zendserver.deployment.ui.editors.text;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.wst.sse.ui.internal.reconcile.validator.ISourceValidator;
import org.eclipse.wst.validation.internal.core.ValidationException;
import org.eclipse.wst.validation.internal.provisional.core.IReporter;
import org.eclipse.wst.validation.internal.provisional.core.IValidationContext;
import org.eclipse.wst.validation.internal.provisional.core.IValidator;
import org.zend.php.zendserver.deployment.core.descriptor.IDescriptorContainer;
import org.zend.php.zendserver.deployment.core.internal.validation.DescriptorSemanticValidator;
import org.zend.php.zendserver.deployment.core.internal.validation.DescriptorValidator;
import org.zend.php.zendserver.deployment.core.internal.validation.ValidationStatus;
import org.zend.php.zendserver.deployment.ui.editors.DeploymentDescriptorEditor;

public class DescriptorReconcilingStrategy implements ISourceValidator, IValidator {
	
	private IDocument document;
	private DescriptorSemanticValidator validator = new DescriptorSemanticValidator();

	public DescriptorReconcilingStrategy() {
	}

	@Override
	public void cleanup(IReporter arg0) {
	}

	@Override
	public void validate(IValidationContext helper, IReporter reporter)
			throws ValidationException {
		validate(null, helper, reporter);
		
	}

	@Override
	public void connect(IDocument document) {
		this.document = document; 
	}

	@Override
	public void disconnect(IDocument document) {
		this.document = null;
	}

	@Override
	public void validate(IRegion dirtyRegion, IValidationContext helper,
			IReporter reporter) {
		DeploymentDescriptorEditor fEditor = findEditor(helper);
		if (fEditor == null)
			return;
		
		IDescriptorContainer container = fEditor.getDescriptorContainer();
		container.load();
		
		validator.setFile(container.getFile());
		ValidationStatus[] statuses = validator.validate(container.getDescriptorModel(), document);
		DescriptorValidator.reportProblems(container.getFile(), statuses);
	}

	private DeploymentDescriptorEditor findEditor(IValidationContext helper) {
		String[] path = helper.getURIs();
		if (path.length == 0) 
			return null;
		
		IFile file = getFile(path[0]);
		if (file == null)
			return null;
		
		IEditorPart editor = getEditor(new FileEditorInput(file));
		if (editor == null)
			return null;
		
		if (!(editor instanceof DeploymentDescriptorEditor))
			return null;
		
		return (DeploymentDescriptorEditor) editor;
	}

	private IFile getFile(String path) {
		try {
			return ResourcesPlugin.getWorkspace().getRoot()
					.getFile(new Path(path));
		} catch (Exception e) {
		}
		return null;
	}
	

	private IEditorPart getEditor(final IEditorInput editorInput) {
		final IEditorPart editor[] = new IEditorPart[1];
		Display.getDefault().syncExec(new Runnable() {
			// needs UI thread to retrieve active page
			public void run() {
				IWorkbenchPage activePage = PlatformUI.getWorkbench()
						.getActiveWorkbenchWindow().getActivePage();
				if (activePage != null) {
					IEditorReference[] refs = activePage.findEditors(
							editorInput, DeploymentDescriptorEditor.ID,
							IWorkbenchPage.MATCH_ID
									| IWorkbenchPage.MATCH_INPUT);
					if (refs.length > 0) {
						editor[0] = refs[0].getEditor(true);
					}
				}
			}
		});
		return editor[0];
	}

}
