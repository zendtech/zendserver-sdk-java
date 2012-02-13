package org.zend.php.common;

import org.eclipse.equinox.internal.p2.discovery.model.CatalogCategory;
import org.eclipse.equinox.internal.p2.discovery.model.CatalogItem;
import org.eclipse.equinox.internal.p2.ui.discovery.util.PatternFilter;
import org.eclipse.jface.viewers.Viewer;

public class FindFilter extends PatternFilter {

	public FindFilter() {
		// constructor
	}

	private boolean filterMatches(String text) {
		return text != null && wordMatches(text);
	}

	
	protected Object[] getChildren(Object element) {
		if (element instanceof CatalogCategory) {
			return ((CatalogCategory) element).getItems().toArray();
		}
		return super.getChildren(element);
	}

	
	protected boolean isLeafMatch(Viewer filteredViewer, Object element) {
		if (element instanceof CatalogItem) {
			CatalogItem descriptor = (CatalogItem) element;
			if (!(filterMatches(descriptor.getName())
					|| filterMatches(descriptor.getDescription())
					|| filterMatches(descriptor.getProvider()) || filterMatches(descriptor
						.getLicense()))) {
				return false;
			}
			return true;
		}
		return false;
	}
}
