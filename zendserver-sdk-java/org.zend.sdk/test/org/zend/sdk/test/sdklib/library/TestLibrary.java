package org.zend.sdk.test.sdklib.library;

import static org.junit.Assert.assertSame;

import org.junit.Test;
import org.zend.sdklib.internal.library.AbstractChangeNotifier;
import org.zend.webapi.core.progress.BasicStatus;
import org.zend.webapi.core.progress.IStatus;
import org.zend.webapi.core.progress.IStatusChangeEvent;
import org.zend.webapi.core.progress.IStatusChangeListener;
import org.zend.webapi.core.progress.StatusCode;

public class TestLibrary {

	private class ExampleLibrary extends AbstractChangeNotifier {
		public boolean doSomething() {
			IStatus progressStatus = new BasicStatus(StatusCode.PROCESSING,
					"in progress", "process is in progress");
			statusChanged(progressStatus);
			IStatus endStatus = new BasicStatus(StatusCode.STOPPING,
					"finished", "process is finished");
			statusChanged(endStatus);
			return true;
		}
	}

	@Test
	public void testExampleLibrary() {
		ExampleLibrary lib = new ExampleLibrary();
		IStatusChangeListener listener = new IStatusChangeListener() {
			@Override
			public void statusChanged(IStatusChangeEvent event) {
				IStatus status = event.getStatus();
				if (status.getCode() == StatusCode.PROCESSING) {
					assertSame("in progress", status.getTitle());
					assertSame("process is in progress", status.getMessage());
				}
				if (status.getCode() == StatusCode.STOPPING) {
					assertSame("finished", status.getTitle());
					assertSame("process is finished", status.getMessage());
				}
			}
		};
		lib.addStatusChangeListener(listener);
		lib.doSomething();
		lib.removeStatusChangeListener(listener);
	}
}
