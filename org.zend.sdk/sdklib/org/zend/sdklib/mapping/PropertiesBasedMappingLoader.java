/*******************************************************************************
 * Copyright (c) Jun 27, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.sdklib.mapping;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;

import org.zend.sdklib.internal.mapping.Mapping;
import org.zend.sdklib.internal.mapping.ResourceMapping;

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
	public IResourceMapping load(InputStream stream) throws IOException {
		ResourceMapping mapping = new ResourceMapping();
		if (stream != null) {
			Properties props = loadProperties(stream);
			mapping.setInclusion(getMapping(props, INCLUDES));
			mapping.setExclusion(getMapping(props, EXCLUDES));
			stream.close();
		}
		mapping.setDefaultExclusion(getDefaultExclusion());
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
	public void store(IResourceMapping mapping, File output) throws IOException {
		OutputStream stream = new FileOutputStream(output);
		Map<String, Set<IMapping>> includes = mapping.getInclusion();
		Set<Entry<String, Set<IMapping>>> entrySet = includes.entrySet();
		for (Entry<String, Set<IMapping>> entry : entrySet) {
			String line = getEntry(entry.getKey(), entry.getValue(), INCLUDES);
			stream.write(line.getBytes());
		}
		stream.write('\n');
		Map<String, Set<IMapping>> excludes = mapping.getExclusion();
		entrySet = excludes.entrySet();
		for (Entry<String, Set<IMapping>> entry : entrySet) {
			String line = getEntry(entry.getKey(), entry.getValue(), EXCLUDES);
			stream.write(line.getBytes());
		}
		stream.close();
	}

	protected Set<IMapping> getMappings(String[] result) throws IOException {
		Set<IMapping> mappings = new LinkedHashSet<IMapping>();
		for (int i = 0; i < result.length; i++) {
			String file = result[i].trim();
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

	private Map<String, Set<IMapping>> getMapping(Properties props, String kind)
			throws IOException {
		Map<String, Set<IMapping>> result = new TreeMap<String, Set<IMapping>>();
		Enumeration<?> e = props.propertyNames();
		while (e.hasMoreElements()) {
			String folderName = (String) e.nextElement();
			if (folderName.endsWith(kind)) {
				String[] files = ((String) props.getProperty(folderName))
						.split(SEPARATOR);
				Set<IMapping> mappings = getMappings(files);
				folderName = folderName.substring(0, folderName.indexOf("."));
				result.put(folderName, mappings);
			}
		}
		return result;
	}

	private String getEntry(String key, Set<IMapping> mappings, String suffix) {
		StringBuilder result = new StringBuilder();
		result.append(key);
		result.append(suffix);
		result.append(" = ");
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
			}
		}
		return result.toString();
	}

}
