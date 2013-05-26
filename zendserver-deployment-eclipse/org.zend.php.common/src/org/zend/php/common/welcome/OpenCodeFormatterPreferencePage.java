package org.zend.php.common.welcome;

import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.dialogs.PreferencesUtil;

public class OpenCodeFormatterPreferencePage extends
		AbstractWelcomePageListener {

	@Override
	public void launchWizard(IWorkbench workbench) {
		PreferenceDialog dialog = PreferencesUtil.createPreferenceDialogOn(
				workbench.getActiveWorkbenchWindow().getShell(),
				"org.eclipse.php.ui.preferences.PHPFormatterPreferencePage", //$NON-NLS-1$
				null, null);
		dialog.open();

	}

}
