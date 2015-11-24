package org.zend.php.zendserver.deployment.core.sdk;

import org.eclipse.core.runtime.IProgressMonitor;
import org.zend.webapi.core.progress.IStatus;
import org.zend.webapi.core.progress.IStatusChangeEvent;
import org.zend.webapi.core.progress.IStatusChangeListener;

public class StatusChangeListener2 implements IStatusChangeListener {

	private IProgressMonitor monitor;
	private IStatus status;

	public StatusChangeListener2(IProgressMonitor monitor) {
		this.monitor = monitor;
	}

	@Override
	public void statusChanged(IStatusChangeEvent event) {
		status = event.getStatus();
		
		switch (status.getCode()) {
		case STARTING:
			monitor.subTask(status.getMessage());
			break;
		case STOPPING:
			break;
		case PROCESSING:
			monitor.worked(status.getTotalWork());
			break;
		case ERROR:
			break;
		default:
			break;
		}
	}

	public IStatus getStatus() {
		return status;
	}
}
