package org.zend.php.zendserver.deployment.ui.editors.text;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.reconciler.DirtyRegion;
import org.eclipse.jface.text.reconciler.IReconcilingStrategy;

public class CompositeReconcilingStrategy implements IReconcilingStrategy {

	private IReconcilingStrategy[] strategies;

	public void setReconcilingStrategies(IReconcilingStrategy[] strategies) {
		this.strategies = strategies;
	}
	
	public void setDocument(IDocument document) {
		for (int i = 0; i < strategies.length; i++) {
			strategies[i].setDocument(document);
		}
	}

	public void reconcile(DirtyRegion dirtyRegion, IRegion subRegion) {
		for (int i = 0; i < strategies.length; i++) {
			strategies[i].reconcile(dirtyRegion, subRegion);
		}
	}

	public void reconcile(IRegion partition) {
		for (int i = 0; i < strategies.length; i++) {
			strategies[i].reconcile(partition);
		}
	}

}
