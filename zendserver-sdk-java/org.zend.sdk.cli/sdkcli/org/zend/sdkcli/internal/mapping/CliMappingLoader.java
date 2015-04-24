/*******************************************************************************
 * Copyright (c) Sep 6, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.sdkcli.internal.mapping;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;

import org.zend.sdklib.mapping.IMapping;
import org.zend.sdklib.mapping.IMappingEntry;
import org.zend.sdklib.mapping.PropertiesBasedMappingLoader;

public class CliMappingLoader extends PropertiesBasedMappingLoader {

	private static final String MAPPING_DEFAULT = "tools/conf/excludes.default";

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.zend.sdklib.mapping.IMappingLoader#getDefaultExclusion()
	 */
	@Override
	public List<IMapping> getDefaultExclusion() throws IOException {
		final InputStream stream = getDefaultExclusionStream();
		if (stream != null) {
			List<IMappingEntry> entries = loadMapping(stream);
			if (entries != null) {
				for (IMappingEntry entry : entries) {
					return entry.getMappings();
				}
			}
		}
		return Collections.emptyList();
	}

	private InputStream getDefaultExclusionStream()
			throws FileNotFoundException {
		File zendSDKJarFile = new File(getClass().getProtectionDomain()
				.getCodeSource().getLocation().getPath());

		File zendSDKroot = zendSDKJarFile.getParentFile().getParentFile();
		File mapping = new File(zendSDKroot, MAPPING_DEFAULT);

		// in development-time scenario, classes are in "sdklib", instead of
		// "lib/zend_sdk.jar"
		if (!mapping.exists()) {
			zendSDKroot = zendSDKJarFile.getParentFile();
			mapping = new File(zendSDKroot, MAPPING_DEFAULT);
		}
		return mapping.exists() ? new FileInputStream(mapping) : null;
	}

}
