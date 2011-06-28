package org.zend.php.zendserver.deployment.ui.editors.text;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.reconciler.DirtyRegion;
import org.eclipse.jface.text.reconciler.IReconcilingStrategy;
import org.zend.php.zendserver.deployment.core.descriptor.IDescriptorContainer;
import org.zend.php.zendserver.deployment.ui.editors.DeploymentDescriptorEditor;


public class DescriptorReconcilingStrategy implements IReconcilingStrategy {

	private DeploymentDescriptorEditor fEditor;

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
	}

}
