package org.zend.php.zendserver.deployment.core.internal.descriptor;

import java.text.MessageFormat;
import java.util.List;

import org.zend.php.zendserver.deployment.core.Messages;
import org.zend.php.zendserver.deployment.core.descriptor.DeploymentDescriptorPackage;
import org.zend.php.zendserver.deployment.core.descriptor.IDirectiveDependency;
import org.zend.php.zendserver.deployment.core.descriptor.IModelObject;


public class DirectiveDependency extends ModelContainer implements IDirectiveDependency {

	private String fName;
	private String fEquals;
	private String fMin;
	private String fMax;
	private String fConflicts;

	public DirectiveDependency() {
		super(new Feature[] {
				DeploymentDescriptorPackage.DEPENDENCY_NAME,
				DeploymentDescriptorPackage.DEPENDENCY_EQUALS,
				DeploymentDescriptorPackage.DEPENDENCY_MIN,
				DeploymentDescriptorPackage.DEPENDENCY_MAX,
				DeploymentDescriptorPackage.DEPENDENCY_CONFLICTS
		}, 
				new Feature[] {});
	}
	
	public String getName() {
		return fName;
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

	public String getConflicts() {
		return fConflicts;
	}
	
	public void setName(String name) {
		String oldValue = this.fName;
		this.fName = name;
		fireChange(DeploymentDescriptorPackage.DEPENDENCY_NAME, fName, oldValue);
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

	public void setConflicts(String conflicts) {
		String oldValue = this.fConflicts;
		this.fConflicts = conflicts;
		fireChange(DeploymentDescriptorPackage.DEPENDENCY_CONFLICTS, fName, oldValue);
	}
	
	public void copy(IModelObject obj) {
		IDirectiveDependency src = (IDirectiveDependency) obj; 
		setEquals(src.getEquals());
		setMax(src.getMax());
		setMin(src.getMin());
		setName(src.getName());
		super.copy(src);
	}

	public void set(Feature key, String value) {
		switch (key.id) {
		case DeploymentDescriptorPackage.DEPENDENCY_NAME_ID:
			setName(value);
			break;
		case DeploymentDescriptorPackage.DEPENDENCY_EQUALS_ID:
			setEquals(value);
			break;
		case DeploymentDescriptorPackage.DEPENDENCY_MIN_ID:
			setMin(value);
			break;
		case DeploymentDescriptorPackage.DEPENDENCY_MAX_ID:
			setMax(value);
			break;
		case DeploymentDescriptorPackage.DEPENDENCY_CONFLICTS_ID:
			setConflicts(value);
			break;
		default:
			throw new IllegalArgumentException(MessageFormat.format(
					Messages.ZendServerDependency_UnknownSetDependency, key));
		}
	}

	public String get(Feature key) {
		switch (key.id) {
		case DeploymentDescriptorPackage.DEPENDENCY_NAME_ID:
			return fName;
		case DeploymentDescriptorPackage.DEPENDENCY_EQUALS_ID:
			return fEquals;
		case DeploymentDescriptorPackage.DEPENDENCY_MIN_ID:
			return fMin;
		case DeploymentDescriptorPackage.DEPENDENCY_MAX_ID:
			return fMax;
		case DeploymentDescriptorPackage.DEPENDENCY_CONFLICTS_ID:
			return fConflicts;
		default:
			throw new IllegalArgumentException(MessageFormat.format(
					Messages.ZendServerDependency_UnknownGetDependency, key));
		}
	}
	
}
