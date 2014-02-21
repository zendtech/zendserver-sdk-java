package org.zend.php.zendserver.deployment.core.sdk;

import org.eclipse.core.runtime.IStatus;
import org.zend.php.zendserver.deployment.core.DeploymentCore;

public class SdkStatus implements IStatus {

	private org.zend.webapi.core.progress.IStatus status;
	private int severity;

	public SdkStatus(org.zend.webapi.core.progress.IStatus status) {
		this.status = status;
		switch (status.getCode()) {
		case ERROR:
			this.severity = IStatus.ERROR;
			break;
		case WARNING:
			this.severity = IStatus.WARNING;
			break;
		default:
			this.severity = IStatus.OK;
			break;
		}
	}

	public IStatus[] getChildren() {
		return new IStatus[0];
	}

	public int getCode() {
		return severity;
	}

	public Throwable getException() {
		return status.getThrowable();
	}

	public String getMessage() {
		return status.getMessage();
	}

	public String getPlugin() {
		return DeploymentCore.PLUGIN_ID;
	}

	public int getSeverity() {
		return severity;
	}

	public boolean isMultiStatus() {
		return false;
	}

	public boolean isOK() {
		return severity == IStatus.OK ? true : false;
	}

	public boolean matches(int severityMask) {
		return (severity & severityMask) != 0;
	}

}
