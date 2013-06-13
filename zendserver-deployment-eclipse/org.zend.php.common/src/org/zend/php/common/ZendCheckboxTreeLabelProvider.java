package org.zend.php.common;

import org.eclipse.equinox.internal.p2.discovery.model.CatalogCategory;
import org.eclipse.equinox.internal.p2.discovery.model.CatalogItem;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;
import org.zend.php.common.ZendCatalogContentProvider.VirtualTreeCategory;

@SuppressWarnings("restriction")
public class ZendCheckboxTreeLabelProvider implements ILabelProvider {

	public void addListener(ILabelProviderListener listener) {
		// TODO Auto-generated method stub

	}

	public void dispose() {
		// TODO Auto-generated method stub

	}

	public boolean isLabelProperty(Object element, String property) {
		// TODO Auto-generated method stub
		return false;
	}

	public void removeListener(ILabelProviderListener listener) {
		// TODO Auto-generated method stub

	}

	public Image getImage(Object element) {
		if (element instanceof CatalogItem) {
			CatalogItem ci = (CatalogItem) element;
			if (ci.getIcon() != null) {
				return ToolTipHandler.computeImage(ci.getSource(), ci.getIcon().getImage16());
			}
		}
		if (element instanceof VirtualTreeCategory) {
			VirtualTreeCategory ca = (VirtualTreeCategory) element;
			CatalogCategory ci = (CatalogCategory) ca.parent;
			if (ci.getIcon() != null) {
				return ToolTipHandler.computeImage(ci.getSource(), ci.getIcon().getImage16());
			}
		}
		if (element instanceof CatalogCategory) {
			CatalogCategory ci = (CatalogCategory) element;
			if (ci.getIcon() != null) {
				return ToolTipHandler.computeImage(ci.getSource(), ci.getIcon().getImage16());
			}
		}
		return null;
	}

	public String getText(Object element) {
		if (element instanceof CatalogCategory)
			return ((CatalogCategory) element).getName();
		else if (element instanceof CatalogItem) {
			return ((CatalogItem) element).getName();
		}
		else if (element instanceof VirtualTreeCategory) {
			return ((VirtualTreeCategory) element).parent.getName();
		}
		return null;
	}


//	public Image getToolTipImage(Object object) {
//		if (object instanceof CatalogItem) {
//			CatalogItem ci = (CatalogItem) object;
//			if (ci.getIcon() != null) {
//				return computeImage(ci.getSource(), ci.getIcon().getImage16());
//			}
//		}
//		return null;
//	}
//
//	public String getToolTipText(Object element) {
//		if (element instanceof CatalogItem) {
//			CatalogItem ci = (CatalogItem) element;
//			if (ci.getOverview() != null
//					&& ci.getOverview().getSummary() != null) {
//				return ci.getOverview().getSummary();
//			}
//		}
//		return null;
//	}

}
