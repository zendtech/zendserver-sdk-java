package org.zend.php.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.equinox.internal.p2.discovery.Catalog;
import org.eclipse.equinox.internal.p2.discovery.model.CatalogCategory;
import org.eclipse.equinox.internal.p2.discovery.model.CatalogItem;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Composite;

public class ZendCatalogContentProvider implements ITreeContentProvider {
	
	private Catalog catalog;

	private ZendCatalogViewer viewer;

	private Map<String, CatalogCategory[]> subcategories = new HashMap<String, CatalogCategory[]>();
	
	Composite container;

	public ZendCatalogContentProvider(Composite container,
			ZendCatalogViewer viewer) {
		this.viewer = viewer;
		this.container = container;
	}

	private boolean hasCategories;
	
	private boolean flattenTopLevelCategories;

	public boolean hasCategories() {
		return hasCategories;
	}

	public void setHasCategories(boolean hasCategories) {
		this.hasCategories = hasCategories;
	}
	
	public void setFlattenTopLevelCategories(boolean flatten) {
		this.flattenTopLevelCategories = flatten;
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
		if (parentElement instanceof VirtualTreeCategory) {
			VirtualTreeCategory element = ((VirtualTreeCategory) parentElement);
			Object[] regularChildren = getChildren(element.parent);
			Object[] subcats = element.children.toArray();
			
			if (regularChildren.length == 0 && subcats.length > 0) {
				return subcats;
			} else if (regularChildren.length > 0 && subcats.length == 0) {
				return regularChildren;
			} else if (regularChildren.length > 0 && subcats.length > 0) {
				Object[] total = new Object[regularChildren.length + subcats.length];
				System.arraycopy(regularChildren, 0, total, 0, regularChildren.length);
				System.arraycopy(subcats, 0, total, regularChildren.length, subcats.length);
				
				return total;
			}
			
		}
		return new Object[0];
	}

	public Object[] getElements(Object inputElement) {
		if (catalog != null) {
			List<Object> elements = new ArrayList<Object>();
			if (hasCategories()) {
				List categories = buildCategoriesTree(catalog.getCategories());
				
				if (! flattenTopLevelCategories) {
					elements.addAll(categories);
				} else {
					for (Object category : categories) {
						elements.addAll(Arrays.asList(getChildren(category)));
					}
				}
			} else {
				elements.addAll(catalog.getItems());
			}
			
			return elements.toArray(new Object[0]);
		}
		return new Object[0];
	}
	
	public static class VirtualTreeCategory {
		public CatalogCategory parent;
		public List<CatalogCategory> children = new ArrayList<CatalogCategory>();
		public VirtualTreeCategory(CatalogCategory parent) {
			this.parent = parent;
		}
		
		@Override
		public String toString() {
			return parent.toString();
		}

		@Override
		public int hashCode() {
			return parent.hashCode();
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			VirtualTreeCategory other = (VirtualTreeCategory) obj;
			if (parent == null) {
				if (other.parent != null)
					return false;
			} else if (!parent.equals(other.parent))
				return false;
			return true;
		}
	}

	private List<VirtualTreeCategory> buildCategoriesTree(List<CatalogCategory> categories) {
		List<VirtualTreeCategory> result = new ArrayList();
		
		List<CatalogCategory> addLater = new ArrayList<CatalogCategory>();
		
		for (CatalogCategory c : categories) {
			String id = c.getId(); // eg. "org.zend.pdt.discovery.extrafeatures-subgroup4-db"
			int parentIdEnd = id.indexOf("-subgroup");
			if (parentIdEnd != -1) {
				String parentId = id.substring(0, parentIdEnd);
				boolean added = false;
				for (VirtualTreeCategory parent : result) {
					if (parentId.equals(parent.parent.getId())) {
						parent.children.add(c);
						added = true;
						break;
					}
				}
				
				if (! added) {
					addLater.add(c);
				}
				
			} else {
				VirtualTreeCategory toplevel = new VirtualTreeCategory(c);
				result.add(toplevel);
				for (CatalogCategory toAdd : addLater) {
					if (toAdd.getId().equals(c.getId())) {
						toplevel.children.add(toAdd);
					}
				}
			}
		}
		
		return result;
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
		Object[] children = getChildren(element);
		return (children != null && children.length > 0);
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
