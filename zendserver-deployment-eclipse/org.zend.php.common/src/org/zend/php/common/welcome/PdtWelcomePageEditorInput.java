package org.zend.php.common.welcome;

import java.net.URL;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.util.StatusHandler;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class PdtWelcomePageEditorInput extends WelcomePageEditorInput {

	public PdtWelcomePageEditorInput(URL fileURL, int persistent, String string) {
		super(fileURL, persistent, string);
	}
	
	@Override
	public StatusHandler getStatusHandler() {
		return new StatusHandler() {

			@Override
			public void show(IStatus status, String title) {
				Shell parent = Display.getDefault().getActiveShell();
				MessageDialog.openError(parent, "Upgrade to Zend Studio failed", "PDT version that you're using cannot be upgraded to Zend Studio due to internal version conflicts. Please make sure you're using the latest available PDT version.");
			}
			
		};
	}
	
	public String getDiscoveryDirFileName() {
		return "/pdt_directory.xml";
	}

}
