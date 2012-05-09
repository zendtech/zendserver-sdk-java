package org.zend.php.common.welcome;

import java.util.Collection;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.zend.php.common.IProfileModificationListener;
import org.zend.php.common.RevertUtil;

public class PdtWelcomeModificationListener implements
		IProfileModificationListener {

	private static final String STUDIO_IU = "com.zend.php.ide";
	private static final String PDT_PRODUCT_ID = "org.zend.php.product";
	private IStatus status;
	
	public IStatus aboutToChange(final Collection<String> setToAdd,
			final Collection<String> setToRemove) {
		if (! isPDtProduct()) { // do nothing, if we're not in PDT product
			return Status.OK_STATUS;
		}
		
		RevertUtil ru = new RevertUtil();
		ru.setRevertTimestamp();
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
		if (! isPDtProduct()) {  // do nothing, if we're not in PDT product
			return;
		}
		
		if ((setToAdd != null) && (setToAdd.contains(STUDIO_IU)) && status.getSeverity() == IStatus.OK) {
			RevertUtil ru = new RevertUtil();
			ru.setRevertTimestamp();
			
			// enforce welcome page on upgraded product start
			WelcomePageFirstTimeStartup.disableFirstStartup(false);
			closeWelcomeEditor();
		}
	}

	private void closeWelcomeEditor() {
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		if (window == null) {
			return;
		}
		
		IWorkbenchPage page = window.getActivePage();
		if (page == null) {
			return;
		}
		
		IEditorReference[] editorRefs = page.getEditorReferences();
		for (IEditorReference editorRef : editorRefs) {
			if (WelcomePageEditor.EDITOR_ID.equals(editorRef.getId())) {
				IEditorPart editor = editorRef.getEditor(false);
				if (editor != null) {
					page.closeEditor(editor, false);
				}
			}
		}
	}

	private boolean isPDtProduct() {
		return PDT_PRODUCT_ID.equals(Platform.getProduct().getId());
	}

}
