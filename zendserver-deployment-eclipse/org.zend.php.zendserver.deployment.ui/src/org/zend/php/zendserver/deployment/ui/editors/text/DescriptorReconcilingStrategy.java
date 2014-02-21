package org.zend.php.zendserver.deployment.ui.editors.text;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.reconciler.DirtyRegion;
import org.eclipse.jface.text.reconciler.IReconcilingStrategy;
import org.zend.php.zendserver.deployment.core.descriptor.IDescriptorContainer;
import org.zend.php.zendserver.deployment.core.internal.validation.DescriptorSemanticValidator;
import org.zend.php.zendserver.deployment.core.internal.validation.DescriptorValidator;
import org.zend.php.zendserver.deployment.core.internal.validation.ValidationStatus;
import org.zend.php.zendserver.deployment.ui.editors.DeploymentDescriptorEditor;


public class DescriptorReconcilingStrategy implements IReconcilingStrategy {

	private DeploymentDescriptorEditor fEditor;
	private DescriptorSemanticValidator validator = new DescriptorSemanticValidator();

	public DescriptorReconcilingStrategy(DeploymentDescriptorEditor editor) {
		this.fEditor = editor;
	}
	
	public void setDocument(IDocument document) {
	}

	public void reconcile(DirtyRegion dirtyRegion, IRegion subRegion) {
		reconcile(subRegion);
	}

	public void reconcile(IRegion partition) {
		IDescriptorContainer container = fEditor.getDescriptorContainer();
		container.load();
		
		validator.setFile(container.getFile());
		ValidationStatus[] statuses = validator.validate(container.getDescriptorModel(), fEditor.getDocument());
		DescriptorValidator.reportProblems(container.getFile(), statuses);
	}

}
