/*******************************************************************************
 * Copyright (c) 2013 Zend Technologies.
 * All rights reserved. This program and the accompanying materials
 * are the copyright of Zend Technologies and is protected under
 * copyright laws of the United States.
 * You must not copy, adapt or redistribute this document for 
 * any use.
 *******************************************************************************/
package org.zend.php.library.core.composer;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.zend.php.library.core.LibraryUtils;
import org.zend.php.library.core.LibraryVersion;
import org.zend.php.library.core.LibraryVersionRange;
import org.zend.php.library.internal.core.CommandExecutor;
import org.zend.php.library.internal.core.HttpHelper;
import org.zend.php.library.internal.core.ILogDevice;
import org.zend.php.library.internal.core.RepositoryPackage;
import org.zend.php.library.internal.json.JSONArray;
import org.zend.php.library.internal.json.JSONException;
import org.zend.php.library.internal.json.JSONObject;

/**
 * @author Wojciech Galanciak, 2013
 * 
 */
public class ComposerService {

	private static final String VENDOR_FOLDER = "vendor";

	private static final String COMPOSER_JSON = "composer.json";

	private static final File SHARE_FOLDER;

	static {
		final String property = System.getProperty("user.home");
		final File user = new File(property);
		SHARE_FOLDER = new File(user.getAbsolutePath() + File.separator
				+ ".zend" + File.separator + "libraries");
		if (!SHARE_FOLDER.exists()) {
			SHARE_FOLDER.mkdir();
		}
	}

	public static Map<String, LibraryVersionRange> getDependencies(String path) {
		File composerJson = getComposerFile(path);
		Map<String, LibraryVersionRange> result = new HashMap<String, LibraryVersionRange>();
		if (composerJson.exists()) {
			try {
				String content = readFile(composerJson);
				JSONObject json = new JSONObject(content);
				Object object = json.get("require");
				JSONObject require = (JSONObject) object;
				Iterator<?> keys = require.keys();
				while (keys.hasNext()) {
					String key = (String) keys.next();
					String range = require.getString(key);
					result.put(key, LibraryVersionRange.getRange(range));
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return result;
	}

	public static Map<IPath, List<String>> downloadPackages(
			RepositoryPackage pkg, List<String> requires, String version,
			ILogDevice log) {
		String composerPharPath = getComposerPhar();
		if (composerPharPath == null) {
			// TODO handle case when composer is not available
		}
		File temp = null;
		Map<IPath, List<String>> result = new HashMap<IPath, List<String>>();
		try {
			temp = getTemp();
			File phpExecFile = new File("/usr/bin/php");
			File jsonFile = getComposerFile(temp.getAbsolutePath());
			jsonFile.createNewFile();

			String content = "{\"require\": {\"" + pkg.getName() + "\": \""
					+ version + "\"}}";
			BufferedOutputStream out = new BufferedOutputStream(
					new FileOutputStream(jsonFile));
			out.write(content.getBytes());
			out.close();

			CommandExecutor cmd = new CommandExecutor();
			cmd.setOutputLogDevice(log);
			cmd.setErrorLogDevice(log);
			cmd.runCommand(phpExecFile.getAbsolutePath(), composerPharPath,
					"install", "-d", temp.getAbsolutePath());
			System.out.println(cmd.getCommandOutput());
			System.out.println(cmd.getCommandError());
			File vendor = new File(temp, VENDOR_FOLDER);
			File[] libs = vendor.listFiles();
			for (File lib : libs) {
				String packageName = getPackageName(lib, vendor);
				if (packageName != null && packageName.startsWith("/")) {
					packageName = packageName.substring(1);
				}
				if (pkg.getName().equals(packageName)
						|| requires.contains(packageName)) {
					File libRoot = new File(vendor.getAbsolutePath(),
							packageName);
					File composerJson = getComposerFile(libRoot
							.getAbsolutePath());
					if (composerJson.exists()) {
						String[] libVersions = getLibraryVersions(packageName,
								vendor);
						copy(libRoot, libRoot, packageName, libVersions[0]);
						String[] srcPaths = getSourcePath(composerJson);
						List<String> paths = new ArrayList<String>();
						File root = new File(SHARE_FOLDER, packageName
								+ File.separator + libVersions[0]);
						for (String path : srcPaths) {
							paths.add(new File(root, path).getAbsolutePath());
						}
						IPath key = new Path(
								LibraryUtils.createLibraryName(packageName)
										+ '/' + libVersions[1]);
						result.put(key, paths);
					}
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			delete(temp);
		}
		return result;
	}

	public static Map<String, String> search(String name, String version) {
		String composerPharPath = getComposerPhar();
		if (composerPharPath == null) {
			// TODO handle case when composer is not available
		}
		File temp = null;
		Map<String, String> result = new HashMap<String, String>();
		try {
			temp = getTemp();
			File jsonFile = getComposerFile(temp.getAbsolutePath());
			jsonFile.createNewFile();

			String content = "{\"repositories\": [{\"type\": \"composer\",\"url\": \"https://packagist.org/\"}]}";
			BufferedOutputStream out = new BufferedOutputStream(
					new FileOutputStream(jsonFile));
			out.write(content.getBytes());
			out.close();

			File phpExecFile = new File("/usr/bin/php");
			CommandExecutor cmd = new CommandExecutor();
			cmd.runCommand(phpExecFile.getAbsolutePath(), composerPharPath,
					"search", name);
			System.out.println(cmd.getCommandOutput());
			System.out.println(cmd.getCommandError());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			delete(temp);
		}
		return result;
	}

	private static File getComposerFile(String libPath) {
		return new File(libPath, COMPOSER_JSON);
	}

	public static String parseName(String name) {
		int index = name.indexOf("(");
		if (index != -1) {
			return name.substring(0, index).trim();
		}
		return name.trim();
	}

	public static LibraryVersion parseVersion(String name) {
		int index = name.indexOf("(");
		if (index != -1) {
			String version = name.substring(index + 1).trim();
			index = version.indexOf(")");
			return LibraryVersion.byName(version.substring(0, index));
		}
		return LibraryVersion.UNKNOWN;
	}

	private static String[] getLibraryVersions(String packageName, File vendor)
			throws IOException {
		File installedJson = new File(new File(vendor, "composer"),
				"installed.json");
		String content = readFile(installedJson);
		try {
			JSONArray json = new JSONArray(content);
			for (int i = 0; i < json.length(); i++) {
				JSONObject obj = (JSONObject) json.get(i);
				String name = obj.getString("name");
				if (packageName.equals(name)) {
					return new String[] { obj.getString("version"),
							obj.getString("version_normalized") };
				}
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	private static String[] getSourcePath(File composerJson)
			throws FileNotFoundException, IOException {
		String content = readFile(composerJson);
		try {
			JSONObject json = new JSONObject(content);
			Object object = json.get("autoload");
			JSONObject autoload = (JSONObject) object;

			try {
				Object psrObject = autoload.get("psr-0");
				JSONObject psr = (JSONObject) psrObject;
				Iterator<?> keys = psr.keys();
				while (keys.hasNext()) {
					return new String[] { psr.getString((String) keys.next()) };
				}
			} catch (JSONException e) {
				// try to find classmap
				Object classmapObject = autoload.get("classmap");
				JSONArray array = (JSONArray) classmapObject;
				String[] values = new String[array.length()];
				for (int i = 0; i < array.length(); i++) {
					values[i] = (String) array.get(i);
				}
				return values;
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new String[] { "/" };
	}

	private static String getPackageName(File lib, File vendor) {
		File composerFile = getComposerFile(lib.getAbsolutePath());
		if (composerFile.exists()) {
			return lib.getAbsolutePath().substring(
					vendor.getAbsolutePath().length());
		}
		if (lib.isDirectory()) {
			File[] children = lib.listFiles();
			for (File file : children) {
				String name = getPackageName(file, vendor);
				if (name != null) {
					return name;
				}
			}
		}
		return null;
	}

	private static String getComposerPhar() {
		try {
			String script = HttpHelper.executeGetRequest(
					"https://getcomposer.org/installer", null, null, 200);

			File scriptFile = new File(SHARE_FOLDER, "script"
					+ new Random().nextInt() + ".php");
			if (!scriptFile.exists()) {
				scriptFile.createNewFile();
			}
			FileOutputStream outStream = new FileOutputStream(scriptFile);
			outStream.write(script.getBytes());
			outStream.close();
			CommandExecutor cmd = new CommandExecutor();
			cmd.runCommand("php", scriptFile.getCanonicalPath(), "--",
					"--install-dir=" + SHARE_FOLDER.getCanonicalPath());
			scriptFile.delete();
			File composerPhar = new File(SHARE_FOLDER, "composer.phar");
			if (composerPhar.exists()) {
				return composerPhar.getCanonicalPath();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	private static String readFile(File composerJson)
			throws FileNotFoundException, IOException {
		BufferedReader reader = new BufferedReader(new FileReader(composerJson));
		StringBuilder c = new StringBuilder();
		String line = null;
		while ((line = reader.readLine()) != null) {
			c.append(line);
		}
		reader.close();
		return c.toString();
	}

	/**
	 * Copies streams.
	 * 
	 * @param in
	 *            Input stream to copy from
	 * @param out
	 *            Output stream to copy to
	 * @param size
	 * @throws IOException
	 */
	private static boolean copyInputStream(InputStream in, OutputStream out)
			throws IOException {
		byte[] buffer = new byte[4096];
		int len;
		try {
			while ((len = in.read(buffer)) > 0) {
				out.write(buffer, 0, len);
			}
		} finally {
			try {
				if (in != null) {
					in.close();
				}
				if (out != null) {
					out.close();
				}
			} catch (IOException e) {
				// TODO log
			}
		}
		return true;
	}

	private static File getTemp() {
		String tempDir = System.getProperty("java.io.tmpdir"); //$NON-NLS-1$
		File temp = new File(tempDir + File.separator + new Random().nextInt());
		temp.mkdir();
		return temp;
	}

	private static void copy(File lib, File vendor, String packageName,
			String version) throws IOException {
		String relativePath = lib.getAbsolutePath().substring(
				vendor.getAbsolutePath().length());
		if (version != null) {
			relativePath = packageName + File.separator + version
					+ relativePath;
		}
		File libFile = new File(SHARE_FOLDER, relativePath);
		if (lib.isDirectory()) {
			if (!libFile.exists()) {
				libFile.mkdirs();
			}
			File[] files = lib.listFiles();
			for (File file : files) {
				copy(file, vendor, packageName, version);
			}
		} else {
			if (!libFile.exists()) {
				libFile.createNewFile();
			}
			InputStream in = new FileInputStream(lib);
			OutputStream out = new FileOutputStream(libFile);
			copyInputStream(in, out);
		}
	}

	private static void delete(File toDelete) {
		if (toDelete != null) {
			if (toDelete.isDirectory()) {
				File[] files = toDelete.listFiles();
				for (File file : files) {
					delete(file);
				}
			} else {
				if (toDelete.exists()) {
					toDelete.delete();
				}
			}
		}
	}

}
