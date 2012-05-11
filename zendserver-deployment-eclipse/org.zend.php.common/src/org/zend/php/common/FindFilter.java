package org.zend.php.common;

import org.eclipse.equinox.internal.p2.discovery.model.CatalogCategory;
import org.eclipse.equinox.internal.p2.discovery.model.CatalogItem;
import org.eclipse.equinox.internal.p2.ui.discovery.util.PatternFilter;
import org.eclipse.jface.viewers.Viewer;
import org.zend.php.common.ZendCatalogContentProvider.VirtualTreeCategory;

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
		} else if (element instanceof VirtualTreeCategory) {
			return ((VirtualTreeCategory) element).children.toArray();
		}
		return super.getChildren(element);
	}

	
	protected boolean isLeafMatch(Viewer filteredViewer, Object element) {
		if (element instanceof CatalogItem) {
			CatalogItem descriptor = (CatalogItem) element;
			return (filterMatches(descriptor.getName())
					|| filterMatches(descriptor.getDescription())
					|| filterMatches(descriptor.getProvider()) || filterMatches(descriptor
						.getLicense()));
		}
		return true;
	}
	
	@Override
	protected boolean isParentMatch(Viewer viewer, Object element) {
		return false;
	}
}
