package org.zend.php.zendserver.deployment.core.internal.descriptor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.zend.php.zendserver.deployment.core.descriptor.IDescriptorChangeListener;
import org.zend.php.zendserver.deployment.core.descriptor.IModelObject;

public abstract class ModelObject implements IModelObject {

	protected List<IDescriptorChangeListener> listeners;
	
	protected List<Feature> properties;
	
	public ModelObject(Feature[] properties) {
		this.properties = new ArrayList<Feature>(properties.length);
		this.properties.addAll(Arrays.asList(properties));
	}
	
	public void copy(IModelObject source) {
		throw new UnsupportedOperationException("Can't copy "+this);
	}

	public void set(Feature key, boolean value) {
		throw new IllegalArgumentException("Can't set feature "+key+" to value "+value+" in "+this);
	}

	public boolean getBoolean(Feature key) {
		throw new IllegalArgumentException("Can't get feature "+key+" from "+this);
	}
	
	public Feature[] getPropertyNames() {
		return properties.toArray(new Feature[properties.size()]);
	}
	
	public void addListener(IDescriptorChangeListener listener) {
		if (listeners == null) {
			listeners = new ArrayList<IDescriptorChangeListener>();
		}
		listeners.add(listener);
	}
	
	public void removeListener(IDescriptorChangeListener listener) {
		listeners.remove(listener);
	}
	
	protected void fireChange(Feature key) {
		if (listeners == null) {
			return;
		}
		
		for (IDescriptorChangeListener l : listeners) {
			l.descriptorChanged(this, key, IDescriptorChangeListener.SET);
		}
	}
}
