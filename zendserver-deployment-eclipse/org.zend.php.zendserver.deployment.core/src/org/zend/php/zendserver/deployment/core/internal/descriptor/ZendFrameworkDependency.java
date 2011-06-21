package org.zend.php.zendserver.deployment.core.internal.descriptor;

import java.util.List;

import org.zend.php.zendserver.deployment.core.descriptor.IModelObject;
import org.zend.php.zendserver.deployment.core.descriptor.IZendFrameworkDependency;


public class ZendFrameworkDependency extends ModelContainer implements IZendFrameworkDependency {

	private String fName;
	private String fEquals;
	private String fMin;
	private String fMax;
	private String fConflicts;

	public ZendFrameworkDependency() {
		super(new Feature[] {
				DEPENDENCY_NAME,
				DEPENDENCY_EQUALS,
				DEPENDENCY_MIN,
				DEPENDENCY_MAX,
				DEPENDENCY_CONFLICTS
		}, 
				new Feature[] { DEPENDENCY_EXCLUDE});
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
		return super.getList(DEPENDENCY_EXCLUDE);
	}

	public String getConflicts() {
		return fConflicts;
	}
	
	public void setName(String fName) {
		this.fName = fName;
	}

	public void setEquals(String fEquals) {
		this.fEquals = fEquals;
	}

	public void setMin(String fMin) {
		this.fMin = fMin;
	}

	public void setMax(String fMax) {
		this.fMax = fMax;
	}

	public void setConflicts(String fConflicts) {
		this.fConflicts = fConflicts;
	}
	
	public void copy(IModelObject obj) {
		IZendFrameworkDependency src = (IZendFrameworkDependency) obj; 
		setConflicts(src.getConflicts());
		setEquals(src.getEquals());
		setMax(src.getMax());
		setMin(src.getMin());
		setName(src.getName());
		super.copy(src);
	}

	public void set(Feature key, String value) {
		if (DEPENDENCY_NAME.equals(key)) {
			this.fName = value;
		} else if (DEPENDENCY_EQUALS.equals(key)) {
			this.fEquals = value;
		} else if (DEPENDENCY_MIN.equals(key)) {
			this.fMin = value;
		} else if (DEPENDENCY_MAX.equals(key)) {
			this.fMax = value;
		} else if (DEPENDENCY_CONFLICTS.equals(key)) {
			this.fConflicts = value;
		} else throw new IllegalArgumentException("Unknown dependency property to set: "+key); 
		
	}

	public String get(Feature key) {
		if (DEPENDENCY_NAME.equals(key)) {
			return fName;
		} else if (DEPENDENCY_EQUALS.equals(key)) {
			return fEquals;
		} else if (DEPENDENCY_MIN.equals(key)) {
			return fMin;
		} else if (DEPENDENCY_MAX.equals(key)) {
			return fMax;
		} else if (DEPENDENCY_CONFLICTS.equals(key)) {
			return fConflicts;
		} else throw new IllegalArgumentException("Unknown dependency property to set: "+key); 
		
	}
}
