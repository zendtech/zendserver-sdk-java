package org.zend.php.zendserver.deployment.core.internal.descriptor;

import java.text.MessageFormat;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.zend.php.zendserver.deployment.core.Messages;
import org.zend.php.zendserver.deployment.core.descriptor.ChangeEvent;
import org.zend.php.zendserver.deployment.core.descriptor.IDescriptorChangeListener;
import org.zend.php.zendserver.deployment.core.descriptor.IModelContainer;
import org.zend.php.zendserver.deployment.core.descriptor.IModelObject;

public abstract class ModelContainer extends ModelObject implements IModelContainer {
	
	private Map<Feature, List<Object>> mmap;
	
	public ModelContainer(Feature[] properties, Feature[] children) {
		super(properties);
		
		addListener(new IDescriptorChangeListener() {
			
			public void descriptorChanged(ChangeEvent event) {
				if (event.target != ModelContainer.this) {
					return;
				}
				
				if (event.feature.type != IModelObject.class) {
					return;
				}
				
				if (event.newValue != null) {
					((IModelObject)event.newValue).setParent(ModelContainer.this);
				} 
				
				if (event.oldValue != null) {
					((IModelObject)event.oldValue).setParent(null);
				}
				
			}
		});
		
		mmap = new LinkedHashMap<Feature, List<Object>>();
		for (Feature s : children) {
			mmap.put(s, new ObservableList<Object>(this, s));
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
			throw new IllegalArgumentException(MessageFormat.format(
					Messages.ModelContainer_UnknownList, key));
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
		return mmap.get(key);
	}
}
