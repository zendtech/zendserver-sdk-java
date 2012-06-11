package org.zend.php.common.welcome;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.ProgressEvent;
import org.eclipse.swt.browser.ProgressListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class PdtStats {

	/**
	 * Here we make sure that Zend get's notified about upgrade, for statistics
	 */
	public static void visit(final String url) {
		Display.getDefault().syncExec(new Runnable() {
			
			public void run() {
				guiVisit(url);
			}
		});
	}

	protected static void guiVisit(String url) {
		try {
			Display d = Display.getDefault();
			final Shell shell = new Shell(d);
			shell.setLayout(new GridLayout());
			Browser b = null;
			try {
				b = new Browser(shell, SWT.NONE);
			} catch (Throwable ex) {
				shell.close();
				return;
			}
			b.addProgressListener(new ProgressListener() {
				
				public void completed(ProgressEvent event) {
					shell.close();
				}
				
				public void changed(ProgressEvent event) {
					// empty
				}
			});
			b.setUrl(url);
		} catch (Throwable ex) {
			// ignore any errors
		}
	}

}
