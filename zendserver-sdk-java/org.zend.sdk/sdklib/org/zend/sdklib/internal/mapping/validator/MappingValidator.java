/*******************************************************************************
 * Copyright (c) Jun 28, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.sdklib.internal.mapping.validator;

import static org.zend.sdklib.mapping.PropertiesBasedMappingLoader.CONTENT;
import static org.zend.sdklib.mapping.PropertiesBasedMappingLoader.EXCLUDES;
import static org.zend.sdklib.mapping.PropertiesBasedMappingLoader.GLOBAL;
import static org.zend.sdklib.mapping.PropertiesBasedMappingLoader.INCLUDES;
import static org.zend.sdklib.mapping.PropertiesBasedMappingLoader.SEPARATOR;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.zend.sdklib.mapping.validator.IMappingValidator;
import org.zend.sdklib.mapping.validator.MappingParseException;
import org.zend.sdklib.mapping.validator.MappingParseMessage;
import org.zend.sdklib.mapping.validator.MappingParseStatus;

/**
 * Default implementation of {@link IMappingValidator}.
 * 
 * @author Wojciech Galanciak, 2011
 * 
 */
public class MappingValidator implements IMappingValidator {

	private static final String KEY_SEPARATOR = "\\.";
	private static final String SCRIPTSDIR = "scriptsdir";
	private static final String APPDIR = "appdir";

	private File container;
	private boolean hasAppdir;

	public MappingValidator(File container) {
		super();
		this.container = container;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.zend.sdklib.mapping.validator.IMappingValidator#parse(java.io.InputStream
	 * )
	 */
	@Override
	public boolean parse(InputStream stream) throws MappingParseException {
		List<MappingParseStatus> result = new ArrayList<MappingParseStatus>();
		BufferedReader reader = new BufferedReader(
				new InputStreamReader(stream));
		String line = null;
		int i = 0;
		try {
			while ((line = reader.readLine()) != null) {
				i++;
				String[] parts = line.split("=");
				if (parts.length == 2) {
					MappingParseStatus keyStatus = checkValidKey(parts[0], i,
							line);
					if (keyStatus != null) {
						result.add(keyStatus);
					}
					List<MappingParseStatus> valueStatus = checkValidValues(
							parts[1], i, line);
					if (!valueStatus.isEmpty()) {
						result.addAll(valueStatus);
					}
				}
			}
			if (i == 0) {
				result.add(new MappingParseStatus(0, 0,
						MappingParseMessage.EMPTY_FILE));
			} else {
				if (!hasAppdir) {
					result.add(new MappingParseStatus(0, 0,
							MappingParseMessage.NO_APPDIR));
				}
			}
		} catch (IOException e) {
			result.add(new MappingParseStatus(0, 0,
					MappingParseMessage.CANNOT_READ));
		}
		if (result.isEmpty()) {
			return true;
		} else {
			throw new MappingParseException(result);
		}
	}

	private List<MappingParseStatus> checkValidValues(String value, int lineNo,
			String line) {
		List<MappingParseStatus> result = new ArrayList<MappingParseStatus>();
		String[] values = value.split(SEPARATOR);
		if (values.length == 0 || value.trim().length() == 0) {
			int offset = line.length() - value.length();
			result.add(new MappingParseStatus(lineNo, offset,
					MappingParseMessage.EMPTY_MAPPING));
			return result;
		}
		for (String entry : values) {
			entry = entry.trim();
			boolean isContent = entry.endsWith(CONTENT);
			if (isContent) {
				entry = entry.substring(0, entry.length() - SEPARATOR.length()
						- 1);
			}
			boolean isGlobal = entry.startsWith(GLOBAL);
			if (isGlobal) {
				entry = entry.substring(GLOBAL.length());
			} else {
				File file = new File(container, entry);
				if (!file.exists()) {
					int offset = line.indexOf(entry);
					result.add(new MappingParseStatus(lineNo, offset,
							MappingParseMessage.NOT_EXIST));
				}

			}
		}
		return result;
	}

	private MappingParseStatus checkValidKey(String key, int lineNo, String line) {
		key = key.trim();
		if (key.equals(APPDIR + INCLUDES)) {
			hasAppdir = true;
		}
		if (hasAppdir || key.equals(APPDIR + EXCLUDES)
				|| key.equals(SCRIPTSDIR + INCLUDES)
				|| key.equals(SCRIPTSDIR + EXCLUDES)) {
			return null;
		} else {
			String[] parts = key.split(KEY_SEPARATOR);
			if (parts.length != 2) {
				return new MappingParseStatus(lineNo, 0,
						MappingParseMessage.INVALID_KEY);
			}
			String folder = parts[0];
			if (APPDIR.equals(folder) || SCRIPTSDIR.equals(folder)) {
				String suffix = parts[1];
				if (INCLUDES.equals(suffix) || EXCLUDES.equals(suffix)) {
					return null;
				} else {
					return new MappingParseStatus(lineNo, 0,
							MappingParseMessage.INVALID_SUFFIX);
				}
			} else {
				return new MappingParseStatus(lineNo, 0,
						MappingParseMessage.INVALID_FOLDER);
			}
		}
	}

}
