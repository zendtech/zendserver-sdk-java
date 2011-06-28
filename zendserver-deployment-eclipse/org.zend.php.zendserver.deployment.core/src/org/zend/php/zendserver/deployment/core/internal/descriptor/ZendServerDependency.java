package org.zend.php.zendserver.deployment.core.internal.descriptor;

import java.util.List;

import org.zend.php.zendserver.deployment.core.descriptor.DeploymentDescriptorPackage;
import org.zend.php.zendserver.deployment.core.descriptor.IModelObject;
import org.zend.php.zendserver.deployment.core.descriptor.IZendServerDependency;


public class ZendServerDependency extends ModelContainer implements IZendServerDependency {

	private String fName;
	private String fEquals;
	private String fMin;
	private String fMax;
	private String fConflicts;

	public ZendServerDependency() {
		super(new Feature[] {
				DeploymentDescriptorPackage.DEPENDENCY_NAME,
				DeploymentDescriptorPackage.DEPENDENCY_EQUALS,
				DeploymentDescriptorPackage.DEPENDENCY_MIN,
				DeploymentDescriptorPackage.DEPENDENCY_MAX,
				DeploymentDescriptorPackage.DEPENDENCY_CONFLICTS
		}, 
				new Feature[] { DeploymentDescriptorPackage.DEPENDENCY_EXCLUDE});
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
	
	public void setName(String fName) {
		this.fName = fName;
		fireChange(DeploymentDescriptorPackage.DEPENDENCY_NAME, fName);
	}

	public void setEquals(String fEquals) {
		this.fEquals = fEquals;
		fireChange(DeploymentDescriptorPackage.DEPENDENCY_EQUALS, fEquals);
	}

	public void setMin(String fMin) {
		this.fMin = fMin;
		fireChange(DeploymentDescriptorPackage.DEPENDENCY_MIN, fMin);
	}

	public void setMax(String fMax) {
		this.fMax = fMax;
		fireChange(DeploymentDescriptorPackage.DEPENDENCY_MAX, fMax);
	}

	public void setConflicts(String fConflicts) {
		this.fConflicts = fConflicts;
		fireChange(DeploymentDescriptorPackage.DEPENDENCY_CONFLICTS, fConflicts);
	}
	
	public void copy(IModelObject obj) {
		IZendServerDependency src = (IZendServerDependency) obj; 
		setConflicts(src.getConflicts());
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
				throw new IllegalArgumentException("Unknown dependency property to set: "+key); 
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
			throw new IllegalArgumentException("Unknown dependency property to set: "+key); 
		}
		
	}
}
