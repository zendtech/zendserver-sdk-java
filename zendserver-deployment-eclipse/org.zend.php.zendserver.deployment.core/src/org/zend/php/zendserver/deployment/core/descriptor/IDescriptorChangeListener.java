package org.zend.php.zendserver.deployment.core.descriptor;


public interface IDescriptorChangeListener {
	
	public static final int SET = 1;
	public static final int ADD = 2;
	public static final int REMOVE = 4;

	void descriptorChanged(ChangeEvent event);
	
}
