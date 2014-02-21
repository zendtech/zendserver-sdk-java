package org.zend.php.zendserver.deployment.core.internal.descriptor;

import java.text.MessageFormat;
import java.util.List;

import org.zend.php.zendserver.deployment.core.Messages;
import org.zend.php.zendserver.deployment.core.descriptor.DeploymentDescriptorPackage;
import org.zend.php.zendserver.deployment.core.descriptor.IModelObject;
import org.zend.php.zendserver.deployment.core.descriptor.IZendFrameworkDependency;


public class ZendFrameworkDependency extends ModelContainer implements IZendFrameworkDependency {

	private String fEquals;
	private String fMin;
	private String fMax;

	public ZendFrameworkDependency() {
		super(new Feature[] {
				DeploymentDescriptorPackage.DEPENDENCY_EQUALS,
				DeploymentDescriptorPackage.DEPENDENCY_MIN,
				DeploymentDescriptorPackage.DEPENDENCY_MAX,
		}, 
				new Feature[] { DeploymentDescriptorPackage.DEPENDENCY_EXCLUDE});
	}
	
	public String getEquals() {
		return fEquals;
	}

	public String getMin() {
		return fMin;
	}

	public String getMax() {
		return fMax;
	}

	public List<String> getExclude() {
		return super.getList(DeploymentDescriptorPackage.DEPENDENCY_EXCLUDE);
	}

	public void setEquals(String equals) {
		String oldValue = this.fEquals;
		this.fEquals = equals;
		fireChange(DeploymentDescriptorPackage.DEPENDENCY_EQUALS, fEquals, oldValue);
	}

	public void setMin(String min) {
		String oldValue = this.fMin;
		this.fMin = min;
		fireChange(DeploymentDescriptorPackage.DEPENDENCY_MIN, fMin, oldValue);
	}

	public void setMax(String max) {
		String oldValue = this.fMax;
		this.fMax = max;
		fireChange(DeploymentDescriptorPackage.DEPENDENCY_MAX, fMax, oldValue);
	}
	
	public void copy(IModelObject obj) {
		IZendFrameworkDependency src = (IZendFrameworkDependency) obj; 
		setEquals(src.getEquals());
		setMax(src.getMax());
		setMin(src.getMin());
		super.copy(src);
	}

	public void set(Feature key, String value) {
		switch (key.id) {
		case DeploymentDescriptorPackage.DEPENDENCY_EQUALS_ID:
			setEquals(value);
			break;
		case DeploymentDescriptorPackage.DEPENDENCY_MIN_ID:
			setMin(value);
			break;
		case DeploymentDescriptorPackage.DEPENDENCY_MAX_ID:
			setMax(value);
			break;
		default:
			throw new IllegalArgumentException(MessageFormat.format(
					Messages.ZendServerDependency_UnknownSetDependency, key));
		}
	}

	public String get(Feature key) {
		switch (key.id) {
		case DeploymentDescriptorPackage.DEPENDENCY_EQUALS_ID:
			return fEquals;
		case DeploymentDescriptorPackage.DEPENDENCY_MIN_ID:
			return fMin;
		case DeploymentDescriptorPackage.DEPENDENCY_MAX_ID:
			return fMax;
		default:
			throw new IllegalArgumentException(MessageFormat.format(
					Messages.ZendServerDependency_UnknownGetDependency, key));
		}
	}
	
}
