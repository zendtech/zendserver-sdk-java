package org.zend.php.zendserver.deployment.core.sdk;

import org.eclipse.core.runtime.IProgressMonitor;
import org.zend.sdklib.event.IStatusChangeEvent;
import org.zend.sdklib.event.IStatusChangeListener;
import org.zend.sdklib.library.IStatus;

public class SdkStatusChangeListener implements IStatusChangeListener {

	private IProgressMonitor monitor;
	private String currentTask;

	public SdkStatusChangeListener(IProgressMonitor monitor) {
		this.monitor = monitor;
	}

	public void statusChanged(IStatusChangeEvent event) {
		IStatus status = event.getStatus();
		System.out.println(status.getTitle() + " " + status.getMessage() + " "
				+ status.getTotalWork());
		switch (status.getCode()) {
		case STARTING:
			currentTask = status.getTitle();
			monitor.setTaskName(currentTask);
			monitor.beginTask(status.getMessage(), status.getTotalWork());
			break;
		case STOPPING:
			currentTask = null;
			monitor.done();
			break;
		case PROCESSING:
			monitor.worked(status.getTotalWork());
			break;
		default:
			break;
		}
	}
}
