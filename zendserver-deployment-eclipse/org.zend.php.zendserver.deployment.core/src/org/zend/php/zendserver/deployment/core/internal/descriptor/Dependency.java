package org.zend.php.zendserver.deployment.core.internal.descriptor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.zend.php.zendserver.deployment.core.descriptor.IDependency;


public class Dependency implements IDependency {

	private String fType;
	private String fName;
	private String fEquals;
	private String fMin;
	private String fMax;
	private List<String> fExcludes;
	private String fConflicts;

	public Dependency(String type) {
		this(type, null);
	}
	
	public Dependency(String type, String name) {
		this(type, name, null, null, null, null, null);
	}
	
	public Dependency(String type, String name, String equals, String min, String max, String[] excludes, String conflicts) {
		fType = type;
		fName = name;
		fEquals = equals;
		fMin = min;
		fMax = max;
		fExcludes = excludes == null ? new ArrayList<String>() : new ArrayList<String>(Arrays.asList(excludes));
		fConflicts = conflicts;
	}
	
	public String getType() {
		return fType;
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
		return Collections.unmodifiableList(fExcludes);
	}

	public String getConflicts() {
		return fConflicts;
	}
	
	public void setType(String type) {
		this.fType = type;
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

	public List<String> setExcludes() {
		return fExcludes;
	}

	public void setConflicts(String fConflicts) {
		this.fConflicts = fConflicts;
	}
}
