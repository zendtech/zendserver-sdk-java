package org.zend.php.zendserver.deployment.ui.wizards;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.zend.php.zendserver.deployment.ui.Activator;

/**
 * @deprecated This is a copy-paste from the org.zend.php.framework plugin.
 *             Remove this code when we resolve the dependency issue.
 * @author Kaloyan Raev
 */
public class ZFUtils {

	public static final String ZF_LIBRARY_PATH = "library"; //$NON-NLS-1$
	public static final String ZF_PACKAGE = "zendframework/zendframework"; //$NON-NLS-1$

	public static boolean isZF2Project(IProject project) {
		if (project == null) {
			return false;
		}

		IPath frameworkPath = project.getLocation().append("vendor") //$NON-NLS-1$
				.append(ZF_PACKAGE).append(ZF_LIBRARY_PATH);

		URL url;
		try {
			url = frameworkPath.toFile().toURI().toURL();
		} catch (MalformedURLException e) {
			return false;
		}
		String version = getFrameworkVersion(url);
		return version != null;
	}

	/**
	 * Reads version number of Zend Framework. Based on given frameworkPath url,
	 * locates Zend/Version.php and reads that file looking for regular
	 * expression matching VERSION = 'NUMBER'. Returns the NUMBER.
	 * 
	 * @param frameworkPath
	 *            Base path of ZendFramework library.
	 * @return Zend Framework version.
	 */
	public static String getFrameworkVersion(URL frameworkPath) {
		URL url;
		try {
			url = new URL(frameworkPath, "Zend/Version/Version.php"); //$NON-NLS-1$
		} catch (MalformedURLException e) {
			Activator.log(e);
			return null;
		}
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(url.openStream()));
		} catch (FileNotFoundException fnfe) {
			return null;
		} catch (IOException e) {
			Activator.log(e);
			return null;
		}
		String line;

		String regex = ".*[ \t]+VERSION[ \t]*=[ \t]*['\"]([^'\"]+)['\"].*"; //$NON-NLS-1$
		Pattern a = Pattern.compile(regex);
		try {
			while ((line = br.readLine()) != null) {
				Matcher matcher = a.matcher(line);
				if (matcher.matches()) {
					return matcher.group(1);
				}
			}
		} catch (IOException e) {
			Activator.log(e);
			return null;
		} finally {
			try {
				br.close();
			} catch (IOException e) {
				// ignoreinitializeZendFramework
			}
		}

		return null;
	}

}
