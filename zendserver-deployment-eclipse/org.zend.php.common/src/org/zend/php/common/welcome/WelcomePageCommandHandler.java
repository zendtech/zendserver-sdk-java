package org.zend.php.common.welcome;

import java.net.URL;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.browser.IWorkbenchBrowserSupport;
import org.zend.php.common.Activator;


public class WelcomePageCommandHandler extends AbstractHandler {

	public WelcomePageCommandHandler() {

	}

	public Object execute(ExecutionEvent event) throws ExecutionException {
		new Job("Show Welcome") {

			public IStatus run(IProgressMonitor arg0) {
				Display.getDefault().syncExec(new Runnable() {

					public void run() {
						IWorkbenchWindow window = PlatformUI.getWorkbench()
								.getActiveWorkbenchWindow();
						IWorkbenchPage page = window.getActivePage();

						if (page == null) {
							return;
						}
						try {
							URL url = FileLocator
									.find(Activator.getDefault()
											.getBundle(),
											new Path(
													"/resources/welcome/index.html"),
											null);

							final IEditorInput editorInput = new PdtWelcomePageEditorInput(
									FileLocator.toFileURL(url),
									IWorkbenchBrowserSupport.PERSISTENT,
									"welcomeBrowser");
							page.openEditor(editorInput,
									WelcomePageEditor.EDITOR_ID, true,
									IWorkbenchPage.MATCH_ID);

						} catch (Exception e) {
							Activator.log(e);
						}
					}
				});
				return Status.OK_STATUS;
			}
		}.schedule();

		return null;
	}
}
