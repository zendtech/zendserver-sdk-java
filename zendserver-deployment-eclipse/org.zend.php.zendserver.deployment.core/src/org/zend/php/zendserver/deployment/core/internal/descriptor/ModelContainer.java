package org.zend.php.zendserver.deployment.core.internal.descriptor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.zend.php.zendserver.deployment.core.descriptor.IDescriptorChangeListener;
import org.zend.php.zendserver.deployment.core.descriptor.IModelContainer;
import org.zend.php.zendserver.deployment.core.descriptor.IModelObject;

public abstract class ModelContainer extends ModelObject implements IModelContainer {
	
	private Map<Feature, List<Object>> mmap;
	
	public ModelContainer(Feature[] properties, Feature[] children) {
		super(properties);
		
		// make sure that listeners list exists, because ModelObject creates it lazily
		if (listeners == null) {
			listeners = new ArrayList<IDescriptorChangeListener>();
		}
		
		mmap = new LinkedHashMap<Feature, List<Object>>();
		for (Feature s : children) {
			mmap.put(s, new ObservableList<Object>(this, s, listeners));
		}
	}

	public void copy(IModelObject source) {
		ModelContainer src = (ModelContainer) source;
		
		for (Map.Entry<Feature, List<Object>> entry : src.mmap.entrySet()) {
			for (int i = 0; i < entry.getValue().size(); i++) {
				set(entry.getKey(), i, entry.getValue().get(i));
			}
		}
	}
	
	public List getList(Feature key) {
		List list = mmap.get(key);
		if (list == null) {
			throw new IllegalArgumentException("Unknown list property name "+key);
		}
		
		return list;
	}

	public Object add(Feature key, Object o) {
		if (getList(key).add(o)) {
			return o;
		}
		
		return null;
	}
	
	public void remove(Feature key, int index) {
		getList(key).remove(index);
	}
	
	public void set(Feature key, int index, Object value) {
		List list = getList(key);
		if (index < list.size()) {
			list.set(index, value);
		} else {
			list.add(value);
		}
	}

	public Feature[] getChildNames() {
		return mmap.keySet().toArray(new Feature[mmap.keySet().size()]);
	}

	public List<Object> getChildren(Feature key) {
		return Collections.unmodifiableList(mmap.get(key));
	}
}
