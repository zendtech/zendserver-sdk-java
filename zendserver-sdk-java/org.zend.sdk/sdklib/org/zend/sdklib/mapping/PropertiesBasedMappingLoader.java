/*******************************************************************************
 * Copyright (c) Jun 27, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.sdklib.mapping;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.Map;
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

	protected static final String EXCLUDES = ".excludes";
	protected static final String SEPARATOR = ",";

	private static final String INCLUDES = ".includes";
	private static final String CONTENT = "/*";
	private static final String GLOBAL = "**/";

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.zend.sdklib.mapping.IMappingLoader#load(java.io.InputStream)
	 */
	@Override
	public IResourceMapping load(InputStream stream) throws IOException {
		ResourceMapping mapping = new ResourceMapping();
		Properties props = loadProperties(stream);
		mapping.setInclusion(getMapping(props, INCLUDES));
		mapping.setExclusion(getMapping(props, EXCLUDES));
		mapping.setDefaultExclusion(getDefaultExclusion());
		stream.close();
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
	public OutputStream store(IResourceMapping mapping, File output)
			throws IOException {
		// TODO Auto-generated method stub
		return null;
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

}
