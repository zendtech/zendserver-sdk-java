package org.zend.php.zendserver.deployment.core.internal.descriptor;

public class Feature {

	public static final int NONE = 0;
	
	public static final int SET_EMPTY_TO_NULL = 1;
	
	public String xpath;
	
	public String attrName;
	
	public Class type;

	public int id;
	
	public int flags;
	
	public Feature(String nodePath, String attrName, Class type, int id) {
		this(nodePath, attrName, type, id, NONE);
	}
	
	public Feature(String nodePath, String attrName, Class type, int id, int flags) {
		this.xpath = nodePath;
		this.attrName = attrName;
		this.type = type;
		this.id = id;
		this.flags = flags;
	}

	@Override
	public String toString() {
		return "Feature [xpath=" + xpath + ", attrName=" + attrName //$NON-NLS-1$ //$NON-NLS-2$
				+ ", type=" + type + "]"; //$NON-NLS-1$ //$NON-NLS-2$
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((attrName == null) ? 0 : attrName.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		result = prime * result + ((xpath == null) ? 0 : xpath.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Feature other = (Feature) obj;
		if (attrName == null) {
			if (other.attrName != null)
				return false;
		} else if (!attrName.equals(other.attrName))
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		if (xpath == null) {
			if (other.xpath != null)
				return false;
		} else if (!xpath.equals(other.xpath))
			return false;
		return true;
	}
	
	
}
