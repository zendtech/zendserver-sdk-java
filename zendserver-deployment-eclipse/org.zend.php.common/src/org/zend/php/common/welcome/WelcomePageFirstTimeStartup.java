package org.zend.php.common.welcome;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.NotEnabledException;
import org.eclipse.core.commands.NotHandledException;
import org.eclipse.core.commands.common.NotDefinedException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.ConfigurationScope;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.IHandlerService;
import org.osgi.service.prefs.Preferences;
import org.zend.php.common.Activator;


public class WelcomePageFirstTimeStartup {

	private static final String PHP_PRODUCT_ID = "org.zend.php.product";
	private static final String IS_FIRST_STARTUP = "isFirstStartup";
	private static final String SHOW_WELCOME = "showWelcome";

	public static void run() {
		if (! PHP_PRODUCT_ID.equals(Platform.getProduct().getId())) {
			return;
		}
		
		// Workspace specific preferences
		IPreferenceStore pref = Activator.getDefault()
				.getPreferenceStore();
		// Configuration specific preferences
		Preferences preferences = ConfigurationScope.INSTANCE
				.getNode(Activator.PLUGIN_ID);

		if (preferences.getBoolean(IS_FIRST_STARTUP, true)
				|| pref.getBoolean(SHOW_WELCOME)) {
			Display.getDefault().syncExec(new Runnable() {
				public void run() {
					IWorkbenchPage page = PlatformUI.getWorkbench()
							.getActiveWorkbenchWindow().getActivePage();
					if (page != null) {
						IViewReference outlineView = page
								.findViewReference("org.eclipse.ui.views.ContentOutline");
						if (outlineView != null) {
							page.hideView(outlineView);
						}
					}
				}
			});
			IHandlerService handlerService = (IHandlerService) PlatformUI
					.getWorkbench().getService(IHandlerService.class);
			try {
				handlerService.executeCommand(
						"org.zend.php.common.welcome.openEditorCommand", null);
				pref.setValue(SHOW_WELCOME, false);
			} catch (ExecutionException e) {
				Activator.log(e);
			} catch (NotDefinedException e) {
				Activator.log(e);
			} catch (NotEnabledException e) {
				Activator.log(e);
			} catch (NotHandledException e) {
				Activator.log(e);
			}
		}
	}
}