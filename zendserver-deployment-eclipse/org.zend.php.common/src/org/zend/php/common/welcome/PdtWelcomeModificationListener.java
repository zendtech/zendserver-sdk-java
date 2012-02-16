package org.zend.php.common.welcome;

import java.util.Collection;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.zend.php.common.IProfileModificationListener;

public class PdtWelcomeModificationListener implements
		IProfileModificationListener {

	private static final String STUDIO_IU = "com.zend.php.ide";
	private IStatus status;
	
	public IStatus aboutToChange(final Collection<String> setToAdd,
			final Collection<String> setToRemove) {
		
		status = null;
		
		Display.getDefault().syncExec(new Runnable() {
			
			public void run() {
				Shell parent = Display.getDefault().getActiveShell();
				boolean ok = MessageDialog.openConfirm(parent, "About to Upgrade", "You are about to upgrade to Zend Studio 30 day free trial. Press OK to Continue, or Cancel.");
				if (ok) {
					if (! setToAdd.contains(STUDIO_IU)) {
						setToAdd.add(STUDIO_IU);
					}
					
					if (setToRemove.contains(STUDIO_IU)) {
						setToRemove.remove(STUDIO_IU);
					}
				} else {
					status = Status.CANCEL_STATUS;
				}
			}
		});
		
		return status;
	}

	public void profileChanged(Collection<String> setToAdd,
			Collection<String> setToRemove, IStatus status) {
		// empty
	}

}
