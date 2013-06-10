package org.zend.php.zendserver.deployment.ui.contentassist;

import java.util.Arrays;

import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.php.internal.core.PHPLanguageToolkit;

public class PHPLibrariesProvider implements IProposalProvider {

	private static String[] libraries;

	public void init() {
		libraries = DLTKCore.getUserLibraryNames(PHPLanguageToolkit
				.getDefault());
		Arrays.sort(libraries, String.CASE_INSENSITIVE_ORDER);
		for (int i = 0; i < libraries.length; i++) {
			boolean builtIn = DLTKCore.isBuiltInUserLibrary(libraries[i],
					PHPLanguageToolkit.getDefault());
			if (builtIn) {
				libraries[i] += " [built-in]"; //$NON-NLS-1$
			}
		}
	}

	public String[] getNames() {
		return libraries;
	}
}
