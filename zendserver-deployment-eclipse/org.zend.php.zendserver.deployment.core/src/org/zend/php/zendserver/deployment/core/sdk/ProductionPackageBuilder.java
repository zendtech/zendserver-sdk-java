/*******************************************************************************
 * Copyright (c) 2014 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.php.zendserver.deployment.core.sdk;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osgi.util.NLS;
import org.eclipse.php.internal.debug.core.preferences.PHPexeItem;
import org.eclipse.php.internal.debug.core.preferences.PHPexes;
import org.osgi.framework.Bundle;
import org.zend.php.zendserver.deployment.core.DeploymentCore;
import org.zend.sdklib.application.PackageBuilder;
import org.zend.sdklib.mapping.IMappingLoader;

/**
 * Provides ability to create ZPK application package for production. It
 * executes the ZF Deploy tool as additional step of exporting the project to a
 * ZPK file.
 * 
 * @author Kaloyan Raev
 */
public class ProductionPackageBuilder extends PackageBuilder {

	private static final String BUILD_COMMAND = "build"; //$NON-NLS-1$
	private static final String TARGET_OPTION = "--target"; //$NON-NLS-1$
	private static final String ZPK_DATA_OPTION = "--zpkdata"; //$NON-NLS-1$
	private static final String CONFIGS_OPTION = "--configs"; //$NON-NLS-1$
	private static final String GITIGNORE = ".gitignore"; //$NON-NLS-1$
	private static final String ZPK_DATA_DIR = "zpkdata"; //$NON-NLS-1$
	private static final String PHP = "php"; //$NON-NLS-1$
	private static final String DEPLOY_PHAR = "lib/zfdeploy.phar"; //$NON-NLS-1$
	private static final String TEMP_FILE_PREFIX = "zpk"; //$NON-NLS-1$

	/**
	 * The temporary directory to export the project before calling the ZF
	 * Deploy tool.
	 */
	private File zpkTempDir;

	/**
	 * The path to the directory with application configuration files to pack in
	 * the ZPK file.
	 */
	private String appConfigsPath;

	/**
	 * Constructor for the production package builder.
	 * 
	 * @param container
	 *            the root of the project to export
	 * @param loader
	 *            a mapping loader
	 * @param appConfigsPath
	 *            a path to the directory with application configuration files
	 */
	public ProductionPackageBuilder(File container, IMappingLoader loader,
			String appConfigsPath) {
		super(container, container, loader);
		this.appConfigsPath = appConfigsPath;
	}

	/**
	 * Creates the temporary directory for exporting the project.
	 * 
	 * <p>After resolving all mappings this folder will be passed to the ZF Deploy tool.</p>
	 */
	@Override
	protected void prepareOutputFile(File zpkFile) throws IOException {
		// create the temporary directory for exporting the project
		zpkTempDir = createTempDirectory();
	}

	/**
	 * Executes the ZF Deploy tool on the temporary directory.
	 */
	@Override
	protected void finishOutputFile(File zpkFile) throws IOException {
		File appDir = new File(zpkTempDir, getAppdirName(configLocation));

		// make sure the .gitignore file is copied if any
		File srcGitIgnore = new File(container, GITIGNORE);
		File destGitIgnore = new File(appDir, GITIGNORE);
		if (srcGitIgnore.exists() && !destGitIgnore.exists()) {
			FileUtils.copyFile(srcGitIgnore, destGitIgnore);
		}

		// move everything but the app dir into the zpkdata directory
		File zpkDataDir = new File(zpkTempDir, ZPK_DATA_DIR);
		for (File file : zpkTempDir.listFiles()) {
			if (!file.getName().equals(getAppdirName(configLocation))) {
				FileUtils.moveToDirectory(file, zpkDataDir, true);
			}
		}

		// construct the command to execute the zf-deploy tool
		String zpkFilePath = zpkFile.getPath();
		String projectPath = appDir.getPath();
		String zpkDataPath = zpkDataDir.getPath();
		String[] command = new String[] { findPhpExePath(),
				getDeployPharPath(), BUILD_COMMAND, zpkFilePath, TARGET_OPTION,
				projectPath, ZPK_DATA_OPTION, zpkDataPath, CONFIGS_OPTION,
				appConfigsPath };

		// delete any previous instance of the result ZPK file
		new File(zpkFilePath).delete();

		// execute the ZF Deploy tool
		try {
			Process process = new ProcessBuilder(command).start();
			// wait for the process to finish
			int exitCode = process.waitFor();
			if (exitCode != 0) {
				String message = NLS.bind(
						Messages.ProductionPackageBuilder_ErrorZFDeployTool,
						exitCode, IOUtils.toString(process.getInputStream()));
				throw new IOException(message);
			}
		} catch (InterruptedException e) {
			DeploymentCore.log(e);
		}

		// delete the temporary directory
		FileUtils.deleteDirectory(zpkTempDir);
	}
	/**
	 * Copies a file from a resolved mapping the the temporary directory.
	 */
	@Override
	protected void addFileToOutput(File file, String relativePath)
			throws IOException {
		FileUtils.copyFile(file, new File(zpkTempDir, relativePath));
	}

	/**
	 * Creates a temporary directory in the system temp directory.
	 * 
	 * @return a {@link File} reference to the created temporary directory
	 * 
	 * @throws IOException
	 *             if the temporary directory cannot be created
	 */
	private File createTempDirectory() throws IOException {
		final File tmp;

		tmp = File.createTempFile(TEMP_FILE_PREFIX,
				Long.toString(System.nanoTime()));

		if (!(tmp.delete())) {
			throw new IOException(
					Messages.ProductionPackageBuilder_ErrorDeleteTempFile
							+ tmp.getAbsolutePath());
		}

		if (!(tmp.mkdir())) {
			throw new IOException(
					Messages.ProductionPackageBuilder_ErrorDeleteTempDirectory
							+ tmp.getAbsolutePath());
		}

		return tmp;
	}

	/**
	 * Returns the path to the zfdeploy.phar packed in this OSGi bundle.
	 * 
	 * @return the path to the zfdeploy.phar
	 * 
	 * @throws IOException
	 *             if the path cannot be determined
	 */
	private String getDeployPharPath() throws IOException {
		Bundle bundle = Platform.getBundle(DeploymentCore.PLUGIN_ID);
		File bundleFile = FileLocator.getBundleFile(bundle);
		return new File(bundleFile, DEPLOY_PHAR).getPath();
	}

	/**
	 * Returns the path to the PHP executable.
	 * 
	 * <p>
	 * This method first checks if there are any PHP binaries packed in the IDE.
	 * If yes, then returns the first found. Otherwise returns "php" with the
	 * hope that there is PHP executable available on the executable path.
	 * </p>
	 * 
	 * @return the path to the PHP executable
	 */
	private String findPhpExePath() {
		PHPexeItem[] items = PHPexes.getInstance().getCLIItems();
		if (items.length > 0) {
			return items[0].getExecutable().getPath();
		} else {
			// no PHP binaries are available in IDE
			return PHP;
		}
	}

}
