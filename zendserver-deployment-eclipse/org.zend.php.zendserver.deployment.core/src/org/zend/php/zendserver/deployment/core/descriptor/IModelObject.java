package org.zend.php.zendserver.deployment.core.descriptor;

import org.zend.php.zendserver.deployment.core.internal.descriptor.Feature;

/**
 * Basic model element.
 * 
 */
public interface IModelObject {
	
	/**
	 * Get notified on this object changes
	 * 
	 * @param listener to be notified about changes
	 */
	void addListener(IDescriptorChangeListener listener);
	
	/**
	 * Remove listener
	 * 
	 * @param listener to remove
	 */
	void removeListener(IDescriptorChangeListener listener);

	/**
	 * Sets model property to new value.
	 * 
	 * @param key property name
	 * @param value new value
	 */
	void set(Feature key, String value);

	/**
	 * Sets model property to new boolean value.
	 * 
	 * @param key property name
	 * @param value new value
	 */
	void set(Feature key, boolean value);

	/**
	 * Retrieves model's property value.
	 * 
	 * @param key property name
	 * @return value, or null if value is not set
	 */
	String get(Feature key);
	
	/**
	 * Retrieves model's boolean property value.
	 *  
	 * @param key property name
	 * @return value, or false if value is not set
	 */
	boolean getBoolean(Feature key);
	
	Feature[] getPropertyNames();
	
	public void setParent(IModelContainer parent);

	int getOffset(Feature f);

	void setOffset(Feature f, int intValue);
	
	/**
	 * @return <code>true</code> if children nodes should be added before any
	 *         property, otherwise returns <code>false</code>
	 */
	boolean isChildrenFirst();
}
