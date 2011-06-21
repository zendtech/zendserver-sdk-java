package org.zend.php.zendserver.deployment.core.internal.descriptor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.w3c.dom.Node;
import org.zend.php.zendserver.deployment.core.descriptor.IModelObject;

public abstract class ModelObject implements IModelObject {

	private Node node;
	protected List<Feature> properties;
	
	public ModelObject(Feature[] properties) {
		this.properties = new ArrayList<Feature>(properties.length);
		this.properties.addAll(Arrays.asList(properties));
	}
	
	public void copy(IModelObject source) {
		// empty
	}

	public void set(Feature key, boolean value) {
		
	}

	public boolean getBoolean(Feature key) {
		// TODO Auto-generated method stub
		return false;
	}
	
	public Feature[] getPropertyNames() {
		return properties.toArray(new Feature[properties.size()]);
	}
	
}
