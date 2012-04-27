package org.zend.php.common;

import java.util.HashSet;
import java.util.Set;

public enum FeatureIdentifiers {

	CODEGALLERY(new String[] { "com.zend.php.codegallery.feature" }), //$NON-NLS-1$

	RSS(new String[] { "com.zend.php.phpdocumentor.feature" }), //$NON-NLS-1$

	PHPDOCUMENTOR(new String[] { "com.zend.php.phpdocumentor.feature" }), //$NON-NLS-1$

	JQUERY(new String[] { "com.zend.jsdt.support.jquery.feature" }), //$NON-NLS-1$

	PROTOTYPE(new String[] { "com.zend.jsdt.support.prototype.feature" }), //$NON-NLS-1$

	EXTJS(new String[] { "com.zend.jsdt.support.extjs.feature" }), //$NON-NLS-1$

	ATF(new String[] { "com.zend.jsdt.atf.feature" }), //$NON-NLS-1$

	CVS(new String[] { "org.eclipse.cvs" }), //$NON-NLS-1$

	SVN(
			new String[] {
					"org.eclipse.team.svn", "org.polarion.eclipse.team.svn.connector", "org.polarion.eclipse.team.svn.connector.svnkit16" }), //$NON-NLS-1$

	GIT(new String[] { "org.eclipse.egit", "org.eclipse.jgit" }), //$NON-NLS-1$

	WSDL(new String[] { "com.zend.wsdl.support.feature" }); //$NON-NLS-1$

	private static final String POSTFIX = ".feature.group"; //$NON-NLS-1$

	private Set<String> ids;

	private FeatureIdentifiers(String[] ids) {
		this.ids = new HashSet<String>();
		for (String str : ids) {
			str = str.concat(POSTFIX);
			this.ids.add(str);
		}
	}

	public Set<String> getName() {
		return ids;
	}

}
