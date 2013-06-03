package org.zend.php.zendserver.deployment.ui.contentassist;

import java.util.Arrays;

public class PHPLibrariesProvider implements IProposalProvider {

	private static String[] libraries;

	public void init() {
		Arrays.sort(libraries, String.CASE_INSENSITIVE_ORDER);
	}

	public String[] getNames() {
		return libraries;
	}
}
