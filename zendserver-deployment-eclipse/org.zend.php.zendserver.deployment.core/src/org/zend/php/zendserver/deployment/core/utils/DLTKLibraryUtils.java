package org.zend.php.zendserver.deployment.core.utils;

import java.util.Map;

import org.eclipse.dltk.core.IDLTKLanguageToolkit;
import org.eclipse.dltk.internal.core.ModelManager;
import org.eclipse.dltk.internal.core.UserLibrary;

public class DLTKLibraryUtils {

	public static final String TAG_BUILTIN = "builtin";
	public static final String TAG_LIBRARYVERSION = "libraryVersion";

	public static boolean isUserLibraryBuiltIn(String name,
			IDLTKLanguageToolkit toolkit) {
		UserLibrary lib = ModelManager.getUserLibraryManager().getUserLibrary(
				name, toolkit);
		if (lib != null) {
			return Boolean.parseBoolean(lib.getAttribute(TAG_BUILTIN));
		}
		return false;
	}

	public static String getUserLibraryVersion(String name,
			IDLTKLanguageToolkit toolkit) {
		UserLibrary lib = ModelManager.getUserLibraryManager().getUserLibrary(
				name, toolkit);
		if (lib != null) {
			return lib.getAttribute(TAG_LIBRARYVERSION);
		}
		return null;
	}

	public static Map<String, String> getUserLibraryAttributes(String name,
			IDLTKLanguageToolkit toolkit) {
		UserLibrary lib = ModelManager.getUserLibraryManager().getUserLibrary(
				name, toolkit);
		if (lib != null) {
			return lib.getAttributes();
		}
		return null;
	}

}