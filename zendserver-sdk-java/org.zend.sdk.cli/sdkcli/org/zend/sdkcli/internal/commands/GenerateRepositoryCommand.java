/*******************************************************************************
 * Copyright (c) May 18, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.sdkcli.internal.commands;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;

import org.zend.sdkcli.internal.options.Option;
import org.zend.sdklib.internal.repository.AbstractRepository;
import org.zend.sdklib.internal.repository.local.FileBasedRepository;
import org.zend.sdklib.internal.utils.JaxbHelper;
import org.zend.sdklib.repository.IRepository;
import org.zend.sdklib.repository.RepositoryFactory;
import org.zend.sdklib.repository.site.Site;

/**
 * Creating a new repository
 * 
 * @author Roy, 2011
 * 
 */
public class GenerateRepositoryCommand extends RepositoryAwareCommand {

	// options
	private static final String OUTPUT_PATH = "o";
	private static final String TEMPLATE = "t";
	private static final String PKG = "p";
	private static final String EXISTING = "e";

	@Option(opt = OUTPUT_PATH, required = true, description = "Directory where the repository will be installed in", argName = "path")
	public String getOutputDirectory() {
		return getValue(OUTPUT_PATH);
	}

	@Option(opt = TEMPLATE, required = true, description = "Base repository which will be used as a template for the generated repository", argName = "path")
	public String getBaseRepository() {
		return getValue(TEMPLATE);
	}

	@Option(opt = PKG, required = true, description = "The package to include in the repository", argName = "path")
	public String getPackae() {
		return getValue(PKG);
	}

	@Option(opt = EXISTING, required = false, description = "Existing repository which this ", argName = "path")
	public String getExisting() {
		return getValue(EXISTING);
	}

	@Override
	public boolean doExecute() {

		// Template - input stream
		FileInputStream template;
		try {
			template = new FileInputStream(new File(getBaseRepository()));
		} catch (FileNotFoundException e1) {
			getLogger().error(e1);
			return false;
		}

		// Repository - output stream
		FileOutputStream fileStream;
		try {
			final File file = new File(getOutputDirectory(),
					AbstractRepository.SITE_XML);
			file.createNewFile();
			fileStream = new FileOutputStream(file);
		} catch (FileNotFoundException e) {
			getLogger().error(e);
			return false;
		} catch (IOException e) {
			getLogger().error(e);
			return false;
		}

		PrintStream printStream;

		if (getExisting() == null) {

			// if there is not existing repository - just generate the site
			printStream = new PrintStream(fileStream);
			try {
				RepositoryFactory.createRepository(printStream, template,
						new File(getPackae()), "apps");
			} catch (Exception e) {
				getLogger().error(e);
				return false;
			}

		} else {

			// add the site to the repository
			final ByteArrayOutputStream bytes = new ByteArrayOutputStream();
			printStream = new PrintStream(bytes);
			try {
				RepositoryFactory.createRepository(printStream, template,
						new File(getPackae()), "apps");

				InputStream is = new ByteArrayInputStream(bytes.toByteArray());
				final Site addition = JaxbHelper.unmarshalSite(is);

				final IRepository repository = RepositoryFactory
						.createRepository(getExisting(), "temp");
				if (repository instanceof FileBasedRepository) {
					RepositoryFactory.merge((FileBasedRepository) repository,
							addition);
				} else {
					getLogger().error(
							"Error finding local repository in path "
									+ getExisting());
					return false;
				}

			} catch (Exception e) {
				getLogger().error(e);
				return false;
			}
		}

		try {
			printStream.close();
			template.close();
		} catch (IOException e) {
			getLogger().error(e);
			return false;
		}

		return true;
	}
}
