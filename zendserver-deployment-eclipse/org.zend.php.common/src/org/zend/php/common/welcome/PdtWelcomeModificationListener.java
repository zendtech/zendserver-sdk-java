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
import org.zend.php.common.core.utils.PDTProductUtils;

public class PdtWelcomeModificationListener implements
		IProfileModificationListener {

	private static final String STUDIO_IU = "com.zend.php.ide.feature.group";
	private IStatus status;

	public IStatus aboutToChange(final Collection<String> setToAdd,
			final Collection<String> setToRemove) {

		if (!PDTProductUtils.isPDtProduct()) { // do nothing, if we're not in PDT product
			return Status.OK_STATUS;
		}

		status = Status.OK_STATUS;
		if (setToAdd.contains(STUDIO_IU)) {
			Display.getDefault().syncExec(new Runnable() {
				public void run() {
					Shell parent = Display.getDefault().getActiveShell();
					boolean ok = MessageDialog
							.openConfirm(
									parent,
									"About to Upgrade",
									"You are about to upgrade to Zend Studio 30 day free trial. Press OK to Continue, or Cancel.");
					if (!ok) {
						status = Status.CANCEL_STATUS;
					}
				}

			});
		}

		return status;
	}

	public void profileChanged(Collection<String> setToAdd,
			Collection<String> setToRemove, IStatus status) {
		if (!PDTProductUtils.isPDtProduct()) { // do nothing, if we're not in PDT product
			return;
		}

		if ((setToAdd != null) && (setToAdd.contains(STUDIO_IU))
				&& status.getSeverity() == IStatus.OK) {
			RevertUtil ru = new RevertUtil();
			ru.setRevertTimestamp();

			// enforce welcome page on upgraded product start
			WelcomePageFirstTimeStartup.disableFirstStartup(false);
			closeWelcomeEditor();

			PdtStats.visit("http://updates.zend.com/studio/pdt/?upgrade");
		}
	}

	public static void closeWelcomeEditor() {
		Display.getDefault().asyncExec(new Runnable() {

			public void run() {
				IWorkbenchWindow window = PlatformUI.getWorkbench()
						.getActiveWorkbenchWindow();
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
		});
	}

}
