package org.zend.php.common;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.widgets.Display;

public class DetectNoResponse {

	// timeout (msec) minimal time of no response to detect
	protected long timeout = 2000;
	protected long displayThreadResponse;
	private boolean isRunning;

	public void start() {
		isRunning = true;
		Thread t = new Thread(new Runnable() {
			public void run() {
				displayThreadResponse = System.currentTimeMillis();
				while ((!Display.getDefault().isDisposed()) && isRunning) {
					pingDisplayThread();
					long now = System.currentTimeMillis();

					try {
						Thread.sleep(timeout);
					} catch (InterruptedException e) {
						// ignore
					}

					long responseTime = Math.abs(now - displayThreadResponse);
					if (responseTime > timeout) {
						String message = "No response for "+responseTime+"msec\n";
						message += captureThreadDump();
						Activator.log(new Status(IStatus.ERROR, Activator.PLUGIN_ID, message));
					}
				}
			}
		});
		t.start();
	}

	public static String captureThreadDump() {
		// from http://henryranch.net/software/capturing-a-thread-dump-in-java/
		Map allThreads = Thread.getAllStackTraces();
		Iterator iterator = allThreads.keySet().iterator();
		StringBuffer stringBuffer = new StringBuffer();
		while (iterator.hasNext()) {
			Thread key = (Thread) iterator.next();
			StackTraceElement[] trace = (StackTraceElement[]) allThreads
					.get(key);
			stringBuffer.append(key + "\r\n");
			for (int i = 0; i < trace.length; i++) {
				stringBuffer.append(" " + trace[i] + "\r\n");
			}
			stringBuffer.append("");
		}
		return stringBuffer.toString();
	}

	protected void pingDisplayThread() {
		Display.getDefault().asyncExec(new Runnable() {

			public void run() {
				displayThreadResponse = System.currentTimeMillis();
			}
		});
	}

	public void stop() {
		isRunning = false;
	}

}