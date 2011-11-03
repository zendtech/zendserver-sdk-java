package org.zend.webapi.internal.core.connection.request;

import java.io.ByteArrayInputStream;

import org.zend.webapi.core.progress.BasicStatus;
import org.zend.webapi.core.progress.IChangeNotifier;
import org.zend.webapi.core.progress.IStatus;
import org.zend.webapi.core.progress.StatusCode;

public class WebApiInputStream extends ByteArrayInputStream {

	private IChangeNotifier notifier;

	public WebApiInputStream(byte[] bytes) {
		this(bytes, null);
	}

	public WebApiInputStream(byte[] bytes, IChangeNotifier notifier) {
		super(bytes);
		this.notifier = notifier;
	}

	@Override
	public synchronized int read(byte[] b, int off, int len) {
		statusChanged(new BasicStatus(StatusCode.PROCESSING,
				"Package Transfering",
				"Transfering a package to the target...", b.length));
		return super.read(b, off, len);
	}

	private void statusChanged(IStatus status) {
		if (notifier != null) {
			notifier.statusChanged(status);
		}
	}

}
