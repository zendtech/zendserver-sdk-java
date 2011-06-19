package org.zend.sdklib.internal.project;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBException;
import javax.xml.bind.PropertyException;

/**
 * Writes files from template
 * 
 */
public class ProjectResourcesWriter {

	private static final String TEMPLATES_DIR = "resources/templates";
	private static final String SCRIPTS_DIR = "scripts";
	public static final String DESCRIPTOR = "deployment.xml";

	// properties of the new project
	private final String name;
	private final boolean withScripts;
	private final boolean welcomePgae;
	private final boolean isZend;

	/**
	 * @param name
	 *            of the application
	 * @param destination
	 *            project root
	 * @param withScripts
	 *            true if scripts are added to the project
	 * @param WelcomePgae
	 * @param isZend
	 */
	public ProjectResourcesWriter(String name, boolean withScripts,
			boolean WelcomePgae, boolean isZend) {
		this.name = name;
		this.withScripts = withScripts;
		this.welcomePgae = WelcomePgae;
		this.isZend = isZend;
	}

	/**
	 * Writing descriptor file to the root project
	 * 
	 * @param name
	 *            - name of the project
	 * @param withContent
	 *            - whether to write other contents than scripts and descriptor
	 * @param withScripts
	 *            - whether to write scripts
	 * @param destination
	 *            - destination directory
	 * @throws IOException
	 * @throws JAXBException
	 * @throws PropertyException
	 */
	public void writeDescriptor(File destination) throws IOException,
			PropertyException, JAXBException {
		File descrFile = new File(destination, DESCRIPTOR);

		if (!descrFile.exists()) {
			writeDescriptor(new FileOutputStream(descrFile));
		}
	}

	/**
	 * Writing project descriptor to a given output stream
	 * 
	 * @param outputStream
	 * @throws IOException
	 * @throws PropertyException
	 * @throws JAXBException
	 */
	public void writeDescriptor(OutputStream outputStream) throws IOException,
			PropertyException, JAXBException {
		if (name == null) {
			throw new IllegalArgumentException(
					"Failed to create deployment descriptor. Project name is missing");
		}

		DescriptorWriter w = new DescriptorWriter(xmlEscape(name), "data",
				"1.0");
		if (withScripts) {
			w.setScripts("scripts");
		}
		w.write(outputStream);
		outputStream.close();
	}

	/**
	 * Write all scripts under a given destination
	 * @param destination
	 * @throws IOException
	 */
	public void writeAllScripts(File destination) throws IOException {
		final ScriptsWriter w = new ScriptsWriter();
		w.writeAllScripts(destination);
	}

	private boolean isScript(String path) {
		return path.startsWith(SCRIPTS_DIR);
	}

	private String xmlEscape(String name) {
		for (int i = 0; i < name.length(); i++) {
			char c = name.charAt(i);
			if (c == '&' || c == '<' || c == '>') {
				return "<![CDATA[" + name.replaceAll("]]>", "]]>]]><![CDATA[")
						+ "]]>";
			}
		}

		return name;
	}

	private void writeStaticResource(String resourceName, File destination)
			throws IOException {
		URL url = getTemplateResource(resourceName);

		File destFile = new File(destination, resourceName);
		File dir = destFile.getParentFile();
		if (!dir.exists()) {
			dir.mkdirs();
		}

		if (destFile.exists()) { // don't overwrite already existing files
			return;
		}

		if (!destFile.getParentFile().canWrite()) { // skip if parent directory
													// is not writeable
			return;
		}

		FileOutputStream out = new FileOutputStream(destFile);

		InputStream is = null;
		try {
			is = url.openStream();
			byte[] buf = new byte[4098];
			int c;
			while ((c = is.read(buf)) > 0) {
				out.write(buf, 0, c);
			}
		} finally {
			if (is != null) {
				is.close();
			}
			if (out != null) {
				out.close();
			}
		}
	}

	private File getTemplatesRoot() {
		// <somewhere>/org.zend.sdk/lib/zend_sdk.jar
		File zendSDKJarFile = new File(getClass().getProtectionDomain()
				.getCodeSource().getLocation().getPath());

		// <somewhere>/org.zend.sdk
		File zendSDKroot = zendSDKJarFile.getParentFile().getParentFile();
		File templates = new File(zendSDKroot, TEMPLATES_DIR);

		// in development-time scenario, classes are in "sdklib", instead of
		// "lib/zend_sdk.jar"
		if (!templates.exists()) {
			zendSDKroot = zendSDKJarFile.getParentFile();
			templates = new File(zendSDKroot, TEMPLATES_DIR);
		}

		return templates.exists() ? templates : null;
	}

	private URL getTemplateResource(String resourceName) {
		File root = getTemplatesRoot();
		if (root == null) {
			return null;
		}

		File resource = new File(root, resourceName);
		// System.out.println(resource);
		try {
			return resource.toURI().toURL();
		} catch (MalformedURLException e) {
			return null;
		}
	}

	private void recursiveFindFiles(File dir, File root, List<String> out) {
		File[] files = dir.listFiles();
		if (files == null) {
			return;
		}

		for (int i = 0; i < files.length; i++) {
			if (files[i].isFile()) {
				String relativePath = getRelativePath(files[i], root);
				out.add(relativePath);
			} else if (files[i].isDirectory()) {
				// String fileName = files[i].getName();
				// if (! fileName.startsWith(".")) { // ignore hidden files
				recursiveFindFiles(files[i], root, out);
				// }
			}
		}
	}

	private String getRelativePath(File file, File root) {
		String filePath = file.getAbsolutePath();
		String rootPath = root.getAbsolutePath();

		if (filePath.startsWith(rootPath)) {
			return filePath.substring(rootPath.length() + 1);
		}

		return filePath;
	}

	private String[] getTemplateResources() {
		File root = getTemplatesRoot();
		if (root == null) {
			return null;
		}

		List<String> files = new ArrayList<String>();

		recursiveFindFiles(root, root, files);

		return files.toArray(new String[files.size()]);
	}
}
