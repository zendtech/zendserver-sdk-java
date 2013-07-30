package org.zend.webapi.internal.core.connection.request;

import java.io.ByteArrayInputStream;

import org.zend.webapi.core.progress.BasicStatus;
import org.zend.webapi.core.progress.IChangeNotifier;
import org.zend.webapi.core.progress.IStatus;
import org.zend.webapi.core.progress.StatusCode;

public class WebApiInputStream extends ByteArrayInputStream {

	public static final int STEPS = 50;
	
	private IChangeNotifier notifier;
	private int totalWork;

	private int resolution;
	private int progress;


	public WebApiInputStream(byte[] bytes) {
		this(bytes, null);
	}

	public WebApiInputStream(byte[] bytes, IChangeNotifier notifier) {
		super(bytes);
		this.notifier = notifier;
		this.totalWork = bytes.length;
		this.resolution = (int) totalWork / STEPS;
	}

	@Override
	public synchronized int read(byte[] b, int off, int len) {
		if (totalWork >= 0) {
			progress += b.length;
			if (progress >= resolution) {
				statusChanged(new BasicStatus(StatusCode.PROCESSING,
						"Package Sending", "Sending package...", 1));
				progress = 0;
			}
			totalWork -= b.length;
		}
		return super.read(b, off, len);
	}

	private void statusChanged(IStatus status) {
		if (notifier != null) {
			notifier.statusChanged(status);
		}
	}

}
