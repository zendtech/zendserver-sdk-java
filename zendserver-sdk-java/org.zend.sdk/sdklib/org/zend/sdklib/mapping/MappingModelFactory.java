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

import org.zend.sdklib.internal.mapping.DefaultMappingLoader;
import org.zend.sdklib.internal.mapping.MappingModel;

/**
 * Factory for creating mapping models.
 * 
 * @author Wojciech Galanciak, 2011
 * 
 */
public class MappingModelFactory {

	public static final String DEPLOYMENT_PROPERTIES = "deployment.properties";

	/**
	 * Creates mapping model using {@link DefaultMappingLoader}.
	 * 
	 * @param container
	 *            where properties file is located
	 * @return mapping model
	 * @throws IOException
	 */
	public static IMappingModel createDefaultModel(File container) {
		if (container == null || !container.exists()) {
			return null;
		}
		try {
			File mappingFile = new File(container, DEPLOYMENT_PROPERTIES);
			return new MappingModel(mappingFile);
		} catch (IOException e) {
			return null;
		}
	}

	/**
	 * Creates mapping model using provided mapping loader.
	 * 
	 * @param loader
	 *            instance of {@link IMappingLoader}
	 * @param container
	 *            where properties file is located
	 * @return mapping model
	 * @throws IOException
	 */
	public static IMappingModel createModel(IMappingLoader loader,
			File container) {
		if (container == null || !container.exists()) {
			return null;
		}
		try {
			File mappingFile = new File(container, DEPLOYMENT_PROPERTIES);
			return new MappingModel(loader, mappingFile);
		} catch (IOException e) {
			return null;
		}
	}

}
