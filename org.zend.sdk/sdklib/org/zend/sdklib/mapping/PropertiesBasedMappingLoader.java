/*******************************************************************************
 * Copyright (c) Jun 27, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.sdklib.mapping;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

import org.zend.sdklib.internal.mapping.Mapping;
import org.zend.sdklib.internal.mapping.MappingEntry;
import org.zend.sdklib.mapping.IMappingEntry.Type;

/**
 * Abstract implementation of {@link IMappingLoader}. It is basic loader for
 * resource mapping stored in a properties file.
 * 
 * @author Wojciech Galanciak, 2011
 * 
 */
public abstract class PropertiesBasedMappingLoader implements IMappingLoader {

	public static final String EXCLUDES = ".excludes";
	public static final String SEPARATOR = ",";
	public static final String INCLUDES = ".includes";
	public static final String CONTENT = "/*";
	public static final String GLOBAL = "**/";

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.zend.sdklib.mapping.IMappingLoader#load(java.io.InputStream)
	 */
	@Override
	public List<IMappingEntry> load(InputStream stream) throws IOException {
		List<IMappingEntry> mapping = new ArrayList<IMappingEntry>();
		if (stream != null) {
			Properties props = loadProperties(stream);
			mapping.addAll(getMapping(props, INCLUDES));
			mapping.addAll(getMapping(props, EXCLUDES));
			stream.close();
		}
		return mapping;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.zend.sdklib.mapping.IMappingLoader#store(org.zend.sdklib.mapping.
	 * IResourceMapping, java.io.File)
	 */
	@Override
	public void store(IMappingModel model, File output)
			throws IOException {
		byte[] bytes = getByteArray(model);
		OutputStream out = new FileOutputStream(output);
		out.write(bytes);
		out.close();
	}

	protected List<IMapping> getMappings(String[] result) throws IOException {
		List<IMapping> mappings = new ArrayList<IMapping>();
		for (int i = 0; i < result.length; i++) {
			String file = result[i].trim();
			if (file.isEmpty()) {
				continue;
			}
			boolean isContent = file.endsWith(CONTENT);
			if (isContent) {
				file = file
						.substring(0, file.length() - SEPARATOR.length() - 1);
			}
			boolean isGlobal = file.startsWith(GLOBAL);
			if (isGlobal) {
				file = file.substring(GLOBAL.length());
			}
			mappings.add(new Mapping(file, isContent, isGlobal));
		}
		return mappings;
	}

	protected Properties loadProperties(InputStream stream) throws IOException {
		Properties props = new Properties();
		props.load(stream);
		return props;
	}

	protected byte[] getByteArray(IMappingModel model) throws IOException {
		ByteArrayOutputStream result = new ByteArrayOutputStream();
		List<IMappingEntry> entries = model.getEnties();
		for (IMappingEntry entry : entries) {
			String entryString = getEntry(entry);
			result.write(entryString.getBytes());
			result.write('\n');
		}
		return result.toByteArray();
	}

	private List<IMappingEntry> getMapping(Properties props, String kind)
			throws IOException {
		List<IMappingEntry> result = new ArrayList<IMappingEntry>();
		Enumeration<?> e = props.propertyNames();
		while (e.hasMoreElements()) {
			String folderName = (String) e.nextElement();
			if (folderName.endsWith(kind)) {
				String[] files = ((String) props.getProperty(folderName))
						.split(SEPARATOR);
				List<IMapping> mappings = getMappings(files);
				folderName = folderName.substring(0, folderName.indexOf("."));
				Type type = INCLUDES.equals(kind) ? Type.INCLUDE : Type.EXCLUDE;
				result.add(new MappingEntry(folderName, mappings, type));
			}
		}
		return result;
	}

	private String getEntry(IMappingEntry entry) {
		StringBuilder result = new StringBuilder();
		result.append(entry.getFolder());
		result.append(entry.getType() == Type.INCLUDE ? INCLUDES : EXCLUDES);
		result.append(" = ");
		result.append(getValue(entry.getMappings()));
		return result.toString();
	}

	private String getValue(List<IMapping> mappings) {
		StringBuilder result = new StringBuilder();
		int size = mappings.size() - 1;
		for (IMapping entry : mappings) {
			String file = entry.getPath();
			if (entry.isContent()) {
				file += CONTENT;
			}
			if (entry.isGlobal()) {
				file = GLOBAL + file;
			}
			result.append(file);
			if (size-- > 0) {
				result.append(SEPARATOR);
				result.append(" ");
			}
		}
		return result.toString();
	}

}
