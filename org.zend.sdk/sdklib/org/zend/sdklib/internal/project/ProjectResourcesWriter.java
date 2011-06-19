package org.zend.sdklib.internal.project;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBException;
import javax.xml.bind.PropertyException;

import org.zend.sdklib.application.ZendProject.SampleApplications;
import org.zend.sdklib.internal.project.ScriptsWriter.DeploymentScriptTypes;

/**
 * Project creation and update handling including descriptor, scripts and
 * application resources
 */
public class ProjectResourcesWriter {

	public static final String DESCRIPTOR = "deployment.xml";

	// properties of the new project
	private final String name;
	private final boolean withScripts;

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
	public ProjectResourcesWriter(String name, boolean withScripts) {
		this.name = name;
		this.withScripts = withScripts;
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
	 * Write all scripts under a given destination directory
	 * 
	 * @param destination
	 * @throws IOException
	 */
	public void writeAllScripts(File destination) throws IOException {
		final ScriptsWriter w = new ScriptsWriter();
		w.writeAllScripts(destination);
	}

	/**
	 * Write specific script to a given destination directory
	 * 
	 * @param type
	 * @param destination
	 * @throws IOException
	 */
	public void writeSpecificScript(DeploymentScriptTypes type, File destination)
			throws IOException {
		final ScriptsWriter w = new ScriptsWriter();
		w.writeSpecificScript(destination, type);
	}

	/**
	 * Write specific script to a given destination directory
	 * 
	 * @param type
	 * @param destination
	 * @throws IOException
	 */
	public void writeSpecificScript(String type, File destination)
			throws IOException {
		final ScriptsWriter w = new ScriptsWriter();
		w.writeSpecificScript(destination, DeploymentScriptTypes.byName(type));
	}

	/**
	 * Writes an application to a given destination directory
	 * 
	 * @param destination
	 * @param app
	 * @throws IOException
	 */
	public void writeApplication(File destination, SampleApplications app)
			throws IOException {

		final List<String> allResources = getAllResources(app);

		for (String path : allResources) {

			// file handling
			if (!path.endsWith("/")) {
				copyFile(destination, path);
			} else {
				createFolder(destination, path);
			}

		}
	}

	private boolean createFolder(File destination, String path) {
		final File file = new File(destination, path);
		return file.mkdirs();
	}

	private void copyFile(File destination, String path) throws IOException,
			FileNotFoundException {
		final InputStream is = this.getClass().getResourceAsStream(path);
		File outputFile = new File(destination, path);
		if (!outputFile.createNewFile()) {
			throw new IOException("Cannot create file "
					+ outputFile.getAbsolutePath());
		}

		InputOutputResource ior = new InputOutputResource(is,
				new FileOutputStream(outputFile));
		ior.copy();
	}

	private List<String> getAllResources(SampleApplications app)
			throws IOException {
		final InputStream iStream = this.getClass().getResourceAsStream(
				app.getMap());
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				iStream));

		List<String> paths = new ArrayList<String>();
		String readLine = reader.readLine();
		while (readLine != null) {
			paths.add(readLine.trim());
		}
		return paths;
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

}
