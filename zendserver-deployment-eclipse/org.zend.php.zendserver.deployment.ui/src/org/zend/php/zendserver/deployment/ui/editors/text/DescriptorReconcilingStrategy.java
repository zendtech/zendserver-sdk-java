package org.zend.php.zendserver.deployment.ui.editors.text;

import java.io.ByteArrayInputStream;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.reconciler.DirtyRegion;
import org.eclipse.jface.text.reconciler.IReconcilingStrategy;
import org.zend.php.zendserver.deployment.core.descriptor.IDeploymentDescriptor;
import org.zend.php.zendserver.deployment.core.descriptor.IDeploymentDescriptorModifier;
import org.zend.php.zendserver.deployment.core.descriptor.IDescriptorContainer;
import org.zend.php.zendserver.deployment.core.internal.descriptor.DeploymentDescriptor;
import org.zend.php.zendserver.deployment.core.internal.descriptor.DeploymentDescriptorParser;
import org.zend.php.zendserver.deployment.ui.editors.DeploymentDescriptorEditor;


public class DescriptorReconcilingStrategy implements IReconcilingStrategy {

	private IDocument fDocument;
	private DeploymentDescriptorEditor fEditor;

	public DescriptorReconcilingStrategy(DeploymentDescriptorEditor editor) {
		this.fEditor = editor;
	}
	
	public void setDocument(IDocument document) {
		this.fDocument = document;
	}

	public void reconcile(DirtyRegion dirtyRegion, IRegion subRegion) {
		reconcile(subRegion);
	}

	public void reconcile(IRegion partition) {
		IDescriptorContainer container = fEditor.getDescriptorContainer();
		DeploymentDescriptor model = (DeploymentDescriptor) container.getDescriptorModel();
		DeploymentDescriptorParser parser = new DeploymentDescriptorParser(model);
		parser.setRecordChanges(true);
		
		ByteArrayInputStream charArray = new ByteArrayInputStream(fDocument.get().getBytes()); // TODO get document encoding
		parser.load(charArray);
		Object[] elements = parser.getChangedElements();
		if (elements != null) {
			for (int i = 0; i < elements.length; i++) {
			container.fireChange(elements[i]);			
			}
		}
	}

}
