package org.zend.php.common.welcome;

import java.io.IOException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.ProgressEvent;
import org.eclipse.swt.browser.ProgressListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.osgi.framework.Version;
import org.zend.php.common.Activator;

public class PdtStats {

	/**
	 * Here we make sure that Zend get's notified about upgrade, for statistics
	 */
	public static void visit(final String url) {
		HttpClient client = new HttpClient();
		
		Version prodVersion = Platform.getProduct().getDefiningBundle().getVersion();
		int majorVer = prodVersion.getMajor();
		int minorVer = prodVersion.getMinor();
		int microVer = prodVersion.getMicro();
		client.getParams().setParameter(HttpMethodParams.USER_AGENT, "ZendStudio/" + majorVer + "." + minorVer+"." + microVer);
		
		GetMethod request = new GetMethod(url);
		try {
			int response = client.executeMethod(request);
			System.out.println(response);
		} catch (HttpException e) {
			Activator.log(e);
		} catch (IOException e) {
			Activator.log(e);
		}

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
