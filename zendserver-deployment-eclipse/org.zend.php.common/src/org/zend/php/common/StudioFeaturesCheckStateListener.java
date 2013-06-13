package org.zend.php.common;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eclipse.equinox.internal.p2.discovery.Catalog;
import org.eclipse.equinox.internal.p2.discovery.model.CatalogCategory;
import org.eclipse.equinox.internal.p2.discovery.model.CatalogItem;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.zend.php.common.ZendCatalogContentProvider.VirtualTreeCategory;

public class StudioFeaturesCheckStateListener implements ICheckStateListener {

	public static final String STUDIO_CORE_IU = "org.zend.pdt.discovery.studiocore";
	public static final String STUDIO_EXTRA = "org.zend.pdt.discovery.studioextra";
	private CheckboxTreeViewer viewer;
	private CatalogItem[] initiallyChecked;
	private List<String> studioSubIds;
	private List<String> selectedExtraFeatures = new ArrayList<String>();

	public StudioFeaturesCheckStateListener(CheckboxTreeViewer viewer) {
		this.viewer = viewer;
	}

	public void checkStateChanged(CheckStateChangedEvent event) {
		updateState(event);
		automaticallyCheckChildren(event);
		handleStudioCheckedState(event);
		updateParentState(event);
	}

	private void updateState(CheckStateChangedEvent event) {
		Object element = event.getElement();
		viewer.setGrayed(element, false);
	}

	private void handleStudioCheckedState(CheckStateChangedEvent event) {
		// if a feature is checked but it's not whole studiocore category, then
		// check studiocore too
		Object element = event.getElement();
		String id = getId(element);

		if (event.getChecked()) {
			if (isStudioFeature(element)) {
				Object studioElement = getTopLevelElement(STUDIO_CORE_IU);
				if (studioElement != null) {
					viewer.setSubtreeChecked(studioElement, true);
					Object[] studioChildren = getChildren(studioElement);
					studioSubIds = new ArrayList(studioChildren.length);
					for (Object o : studioChildren) {
						String childId = getId(o);
						studioSubIds.add(childId);
					}
				}

			}
		} else {
			if (studioSubIds != null) {
				if (studioSubIds.contains(id)) { // under studio sub-tree,
													// features cannot be
													// unchecked
					event.getCheckable().setChecked(event.getElement(), true);
				} else if (STUDIO_CORE_IU.equals(id)) { // studio core feature
														// can be unchecked if
														// no extra features is
														// checked
					if (viewer.getChecked(getTopLevelElement(STUDIO_EXTRA))) {
						viewer.setSubtreeChecked(event.getElement(), true);
					}
				}
			}
		}

	}

	void updateParentState(CheckStateChangedEvent event) {
		updateParentState(event.getElement(), event.getChecked());
	}

	void updateParentState(Object child, boolean baseChildState) {

		if (child == null)
			return;
		Object parent = null;
		if (child instanceof CatalogCategory) {
			CatalogCategory p = (CatalogCategory) child;
			if (isSubCategory(p.getId())) {
				parent = getTopLevelElement(STUDIO_EXTRA);
			}
		} else {
			parent = ((ZendCatalogContentProvider) viewer.getContentProvider())
					.getParent(child);
		}
		if (parent == null || parent instanceof Catalog)
			return;

		boolean allSameState = true;
		Object[] children = null;
		children = ((ZendCatalogContentProvider) viewer.getContentProvider())
				.getChildren(parent);

		for (int i = children.length - 1; i >= 0; i--) {
			if (viewer.getChecked(children[i]) != baseChildState
					|| viewer.getGrayed(children[i])) {
				allSameState = false;
				break;
			}
		}
		boolean isStudioCore = parent instanceof CatalogCategory && ((CatalogCategory)parent).getId().equals(STUDIO_CORE_IU);

		if (!allSameState && !isStudioCore) {
			viewer.setGrayChecked(parent, true);
		} else if (allSameState && baseChildState) {
			viewer.setGrayed(parent, false);
			viewer.setChecked(parent, true);
		} else if(!isStudioCore){
			viewer.setGrayed(parent, false);
			viewer.setChecked(parent, false);
		}

		updateParentState(parent, baseChildState);
	}

	private Object getTopLevelElement(String id) {
		Object input = viewer.getInput();
		Object[] topLevel = ((ZendCatalogContentProvider) viewer
				.getContentProvider()).getElements(input);
		for (Object tl : topLevel) {
			String tlId = getId(tl);
			if (tlId.equals(id)) {
				return tl;
			}
		}

		return null;
	}

	private Object[] getChildren(Object o) {
		return ((ZendCatalogContentProvider) viewer.getContentProvider())
				.getChildren(o);
	}

	private void automaticallyCheckChildren(CheckStateChangedEvent event) {
		Object checkedElement = event.getElement();
		viewer.setSubtreeChecked(checkedElement, event.getChecked());
	}

	private String getId(Object element) {
		if (element instanceof VirtualTreeCategory) {
			VirtualTreeCategory vcat = (VirtualTreeCategory) element;
			return vcat.parent.getId();

		} else if (element instanceof CatalogCategory) {
			CatalogCategory cat = (CatalogCategory) element;
			return cat.getId();

		} else if (element instanceof CatalogItem) {
			CatalogItem item = (CatalogItem) element;
			return item.getId();
		}

		return null;
	}

	private boolean isStudioFeature(Object element) {
		if (element instanceof VirtualTreeCategory) {
			VirtualTreeCategory vcat = (VirtualTreeCategory) element;
			return isStudio(vcat.parent.getId());

		} else if (element instanceof CatalogCategory) {
			CatalogCategory cat = (CatalogCategory) element;
			return isStudio(cat.getId());

		} else if (element instanceof CatalogItem) {
			CatalogItem item = (CatalogItem) element;
			return isStudio(item.getCategoryId());
		}

		return false;
	}

	private boolean isStudio(String id) {
		return (id.equals(STUDIO_EXTRA) || id.equals(STUDIO_CORE_IU) || isSubCategory(id));
	}
	
	public static boolean isSubCategory(String id) {
		return (id.contains("-subgroup"));
	}

	public void setInstalledFeatures(CatalogItem[] installedFeatures) {
		initiallyChecked = installedFeatures;
		if (initiallyChecked != null)
			for (CatalogItem item : initiallyChecked) {
				updateParentState(item, true);
			}
	}
}