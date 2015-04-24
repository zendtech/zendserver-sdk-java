package org.zend.sdkcli.internal.monitor;

import java.io.PrintWriter;

import org.zend.sdkcli.monitor.ProgressMonitor;
import org.zend.sdkcli.monitor.TextProgressMonitor;
import org.zend.webapi.core.progress.IStatus;
import org.zend.webapi.core.progress.IStatusChangeEvent;
import org.zend.webapi.core.progress.IStatusChangeListener;

public class StatusChangeListener implements IStatusChangeListener {

	private IStatus status;

	private ProgressMonitor monitor;

	public StatusChangeListener() {
		monitor = new TextProgressMonitor(new PrintWriter(System.out));
	}

	public void statusChanged(IStatusChangeEvent event) {
		status = event.getStatus();
		switch (status.getCode()) {
		case STARTING:
			if (status.getTotalWork() == -1) {
				monitor.beginTask(status.getMessage(), ProgressMonitor.UNKNOWN);
			} else {
				monitor.beginTask(status.getMessage(), status.getTotalWork());
			}
			break;
		case STOPPING:
			monitor.endTask();
			break;
		case PROCESSING:
			monitor.update(status.getTotalWork());
			break;
		case ERROR:
			monitor.endTask();
			break;
		default:
			break;
		}
	}

	public IStatus getStatus() {
		return status;
	}

}
