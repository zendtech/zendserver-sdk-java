/*******************************************************************************
 * Copyright (c) May 29, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.sdkcli.internal.commands;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Properties;

import org.zend.sdkcli.internal.options.Option;
import org.zend.sdklib.target.IZendTarget;
import org.zend.sdklib.target.LicenseExpiredException;

/**
 * Update an existing target.
 * 
 * @author Wojciech Galanciak, 2011
 * 
 */
public class UpdateTargetCommand extends TargetAwareCommand {

	// properties file keys
	private static final String PROP_SECRETKEY = "secretkey";
	private static final String PROP_KEY = "key";

	// options
	private static final String ID = "t";
	private static final String KEY = "k";
	private static final String SECRETKEY = "s";
	private static final String HOST = "h";
	private static final String DEFAULT_SERVER = "d";
	private static final String PROPERTIES = "p";

	@Option(opt = ID, required = true, description = "Target id", argName = "id")
	public String getId() {
		return getValue(ID);
	}

	@Option(opt = KEY, required = false, description = "Target environment API Key name", argName = PROP_KEY)
	public String getKey() {
		Properties p = getProperties();
		if (p != null) {
			return p.getProperty(PROP_KEY);
		}

		return getValue(KEY);
	}

	@Option(opt = SECRETKEY, required = false, description = "Target environment API Key secret value", argName = "secret-key")
	public String getSecretKey() {
		Properties p = getProperties();
		if (p != null) {
			return p.getProperty(PROP_SECRETKEY);
		}

		return getValue(SECRETKEY);
	}

	@Option(opt = DEFAULT_SERVER, required = false, description = "Default Server URL", argName = "defaultServer")
	public String getDefaultServerURL() {
		return getValue(DEFAULT_SERVER);
	}

	@Option(opt = HOST, required = false, description = "Target host URL", argName = "host")
	public String getHost() {
		return getValue(HOST);
	}

	@Option(opt = PROPERTIES, required = false, description = "The properties file", argName = "file")
	public File getPropertiesFile() {
		final String filename = getValue(PROPERTIES);

		if (filename == null || filename.length() == 0) {
			return null;
		}
		final File file = new File(filename);
		return file;
	}

	@Override
	public boolean doExecute() {
		if (getHost() == null && getKey() == null && getSecretKey() == null
				&& getDefaultServerURL() == null) {
			getLogger()
					.error("To update a target at least one of the following options is required: h, k, s, p.");
			return true;
		}
		IZendTarget result = null;;
		try {
			result = getTargetManager().updateTarget(getId(), getHost(),
					getDefaultServerURL(), getKey(), getSecretKey());
		} catch (LicenseExpiredException e) {
			getLogger()
					.error(MessageFormat
							.format("Cannot update target {0}. Check if license has not exipred.",
									getId()));
		}
		if (result == null) {
			return false;
		}
		return true;
	}

	/**
	 * Reads properties files and return values
	 * 
	 * @return Properties loaded object
	 */
	private Properties getProperties() {
		final File file = getPropertiesFile();
		if (file == null) {
			return null;
		}
		try {
			Properties p = new Properties();
			p.load(new FileInputStream(file));
			getLogger().info("Loading file " + file.getAbsolutePath());
			return p;
		} catch (FileNotFoundException e) {
			getLogger().error("File not found " + file.getAbsolutePath());
		} catch (IOException e) {
			getLogger().error("Error during reading " + file.getAbsolutePath());
			getLogger().error(e);
		}
		return null;
	}

}
