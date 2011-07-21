package org.zend.php.zendserver.deployment.core.internal.validation;

import org.eclipse.core.resources.IMarker;


public class ValidationStatus {

	public static final int WARNING = 1;
	public static final int ERROR = 2;
	public static final int INFO = 4;
	
	private int featureId;
	private int severity;
	private String message;
	private int line;
	private int start;
	private int end;
	private IMarker marker;
	private int objectId;
	private int objectNo;

	public ValidationStatus(int line, int start, int end, int severity, String message) {
		this(-1, -1, -1, line, start, end, severity, message);
	}
	
	public ValidationStatus(int objId, int objNo, int featureId, int line, int start, int end, int severity, String message) {
		this.objectId = objId;
		this.objectNo = objNo;
		this.featureId = featureId;
		this.line = line;
		this.start = start;
		this.end = end;
		this.severity = severity;
		this.message = message;
	}
	
	public void setMarker(IMarker marker) {
		this.marker = marker;
	}
	
	public int getSeverity() {
		return severity;
	}
	
	public String getMessage() {
		return message;
	}

	public int getLine() {
		return line;
	}

	public int getStart() {
		return start;
	}

	public int getEnd() {
		return end;
	}

	public int getFeatureId() {
		return featureId;
	}

	public IMarker getMarker() {
		return marker;
	}
	
	@Override
	public String toString() {
		return "ValidationStatus [featureId=" + featureId + ", severity=" //$NON-NLS-1$ //$NON-NLS-2$
				+ severity + ", message=" + message + ", line=" + line //$NON-NLS-1$ //$NON-NLS-2$
				+ ", start=" + start + ", end=" + end + ", marker=" + marker //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				+ "]"; //$NON-NLS-1$
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + end;
		result = prime * result + line;
		result = prime * result + ((message == null) ? 0 : message.hashCode());
		result = prime * result + featureId;
		result = prime * result + severity;
		result = prime * result + start;
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
		ValidationStatus other = (ValidationStatus) obj;
		if (end != other.end)
			return false;
		if (line != other.line)
			return false;
		if (message == null) {
			if (other.message != null)
				return false;
		} else if (!message.equals(other.message))
			return false;
		if (featureId != other.featureId)
			return false;
		if (severity != other.severity)
			return false;
		if (start != other.start)
			return false;
		return true;
	}

	public int getObjectId() {
		return objectId;
	}
	
	public int getObjectNo() {
		return objectNo;
	}
	
}
