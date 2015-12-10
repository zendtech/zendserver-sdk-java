package org.zend.php.zendserver.deployment.ui.contentassist;

import java.util.Arrays;

import org.eclipse.dltk.internal.core.ModelManager;
import org.eclipse.php.internal.core.PHPLanguageToolkit;

public class PHPLibrariesProvider implements IProposalProvider {

	private static String[] libraries;

	public void init() {
		libraries = ModelManager.getUserLibraryManager()
				.getUserLibraryNames(PHPLanguageToolkit
				.getDefault());
		Arrays.sort(libraries, String.CASE_INSENSITIVE_ORDER);
	}

	public String[] getNames() {
		return libraries;
	}
}
