package org.zend.php.common;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.equinox.internal.p2.discovery.Catalog;
import org.eclipse.equinox.internal.p2.discovery.model.CatalogCategory;
import org.eclipse.equinox.internal.p2.discovery.model.CatalogItem;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Composite;

public class ZendCatalogContentProvider implements ITreeContentProvider {

	private Catalog catalog;

	private ZendCatalogViewer viewer;

	Composite container;

	public ZendCatalogContentProvider(Composite container,
			ZendCatalogViewer viewer) {
		this.viewer = viewer;
		this.container = container;
	}

	private boolean hasCategories;

	public boolean hasCategories() {
		return hasCategories;
	}

	public void setHasCategories(boolean hasCategories) {
		this.hasCategories = hasCategories;
	}

	public void dispose() {
		catalog = null;
	}

	public Catalog getCatalog() {
		return catalog;
	}

	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof CatalogCategory) {
			return ((CatalogCategory) parentElement).getItems().toArray();
		}
		return new Object[0];
	}

	public Object[] getElements(Object inputElement) {
		if (catalog != null) {
			List<Object> elements = new ArrayList<Object>();
			if (hasCategories()) {
				elements.addAll(catalog.getCategories());	
			} else {
				elements.addAll(catalog.getItems());
			}
			
			return elements.toArray(new Object[0]);
		}
		return new Object[0];
	}

	public Object getParent(Object element) {
		if (element instanceof CatalogCategory) {
			return catalog;
		}
		if (element instanceof CatalogItem) {
			return ((CatalogItem) element).getCategory();
		}
		return null;
	}

	public boolean hasChildren(Object element) {
		if (element instanceof CatalogCategory) {
			return ((CatalogCategory) element).getItems().size() > 0;
		}
		return false;
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		this.catalog = (Catalog) newInput;
	}

	public CatalogItem[] getInstalledElements() {
		Catalog catalog = getCatalog();
		if (getCatalog() != null) {
			List<Object> installedElements = new ArrayList<Object>();
			for (CatalogItem item : catalog.getItems()) {
				if (item.isInstalled()) {
					installedElements.add(item);
				}
			}
			return installedElements.toArray(new CatalogItem[0]);
		}
		return new CatalogItem[0];
	}
}
