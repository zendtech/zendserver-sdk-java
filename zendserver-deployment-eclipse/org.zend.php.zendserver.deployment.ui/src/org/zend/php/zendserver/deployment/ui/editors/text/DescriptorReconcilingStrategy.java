package org.zend.php.zendserver.deployment.ui.editors.text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.wst.validation.internal.provisional.core.IReporter;
import org.eclipse.wst.validation.internal.provisional.core.IValidationContext;
import org.eclipse.wst.validation.internal.provisional.core.IValidator;
import org.zend.php.zendserver.deployment.core.descriptor.IDescriptorContainer;
import org.zend.php.zendserver.deployment.core.internal.validation.DescriptorSemanticValidator;
import org.zend.php.zendserver.deployment.core.internal.validation.DescriptorValidator;
import org.zend.php.zendserver.deployment.core.internal.validation.ValidationStatus;
import org.zend.php.zendserver.deployment.ui.editors.DeploymentDescriptorEditor;

public class DescriptorReconcilingStrategy implements IValidator {

	private DescriptorSemanticValidator validator = new DescriptorSemanticValidator();

	public DescriptorReconcilingStrategy() {
	}

	@Override
	public void cleanup(IReporter reporter) {
	}

	public void validate(IValidationContext helper, IReporter reporter) {
		List<IEditorReference> editors = findEditors(helper);
		for (IEditorReference ref : editors) {
			DeploymentDescriptorEditor editor = (DeploymentDescriptorEditor) ref
					.getEditor(true);
			IDescriptorContainer container = editor.getDescriptorContainer();
			container.load();

			validator.setFile(container.getFile());
			ValidationStatus[] statuses = validator.validate(
					container.getDescriptorModel(), editor.getDocument());
			DescriptorValidator.reportProblems(container.getFile(), statuses);
		}
	}

	private List<IEditorReference> findEditors(IValidationContext helper) {
		String[] path = helper.getURIs();
		if (path.length == 0)
			return null;

		IFile file = ResourcesPlugin.getWorkspace().getRoot()
				.getFile(new Path(path[0]));
		if (file == null)
			return null;

		return getEditors(new FileEditorInput(file));
	}

	private List<IEditorReference> getEditors(final IEditorInput editorInput) {
		final List<IEditorReference> editors = new ArrayList<IEditorReference>();

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
					editors.addAll(Arrays.asList(refs));
				}
			}
		});

		return editors;
	}

}
