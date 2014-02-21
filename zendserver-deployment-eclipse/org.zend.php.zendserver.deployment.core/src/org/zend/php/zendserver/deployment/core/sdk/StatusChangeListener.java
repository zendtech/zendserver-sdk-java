package org.zend.php.zendserver.deployment.core.sdk;

import org.eclipse.core.runtime.IProgressMonitor;
import org.zend.webapi.core.progress.IStatus;
import org.zend.webapi.core.progress.IStatusChangeEvent;
import org.zend.webapi.core.progress.IStatusChangeListener;

public class StatusChangeListener implements IStatusChangeListener {

	private IProgressMonitor monitor;
	private String currentTask;
	private IStatus status;

	public StatusChangeListener(IProgressMonitor monitor) {
		this.monitor = monitor;
	}

	public void statusChanged(IStatusChangeEvent event) {
		status = event.getStatus();
		switch (status.getCode()) {
		case STARTING:
			currentTask = status.getTitle();
			monitor.beginTask(status.getMessage(), status.getTotalWork());
			break;
		case STOPPING:
			currentTask = null;
			monitor.done();
			break;
		case PROCESSING:
			monitor.worked(status.getTotalWork());
			break;
		case ERROR:
			monitor.done();
			break;
		default:
			break;
		}
	}

	public IStatus getStatus() {
		return status;
	}

}
