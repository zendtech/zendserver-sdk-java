package org.zend.php.zendserver.deployment.ui.editors.propertiestext;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.TextPresentation;
import org.eclipse.jface.text.presentation.PresentationReconciler;

/**
 * Presentation reconciler, adding functionality for operation without a viewer.
 */
public class PropertiesPresentationReconciler extends PresentationReconciler {

	/** Last used document */
	private IDocument fLastDocument;

	/**
	 * Constructs a "repair description" for the given damage and returns this
	 * description as a control presentation.
	 * <p>
	 * NOTE: Should not be used if this reconciler is installed on a viewer.
	 * </p>
	 * 
	 * @param damage
	 *            the damage to be repaired
	 * @param document
	 *            the document whose presentation must be repaired
	 * @return the presentation repair description as control presentation
	 */
	public TextPresentation createRepairDescription(IRegion damage, IDocument document) {
		if (document != fLastDocument) {
			setDocumentToDamagers(document);
			setDocumentToRepairers(document);
			fLastDocument = document;
		}
		return createPresentation(damage, document);
	}
}
