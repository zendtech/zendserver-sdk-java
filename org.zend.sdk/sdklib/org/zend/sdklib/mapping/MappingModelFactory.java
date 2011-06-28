/*******************************************************************************
 * Copyright (c) Jun 27, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.sdklib.mapping;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

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
	public static IMappingModel createDefaultModel(File container)
			throws IOException {
		if (container == null || !container.exists()) {
			return null;
		}
		InputStream stream = new FileInputStream(new File(container,
				DEPLOYMENT_PROPERTIES));
		return new MappingModel(stream, container);
	}

	/**
	 * Creates empty mapping model using {@link DefaultMappingLoader} to get
	 * default exclusion list.
	 * 
	 * @param container
	 *            where properties file is located
	 * @return mapping model
	 * @throws IOException
	 */
	public static IMappingModel createEmptyDefaultModel(File container)
			throws IOException {
		if (container == null || !container.exists()) {
			return null;
		}
		return new MappingModel(null, container);
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
			File container) throws IOException {
		if (container == null || !container.exists()) {
			return null;
		}
		InputStream stream = new FileInputStream(new File(container,
				DEPLOYMENT_PROPERTIES));
		return new MappingModel(loader, stream, container);
	}

	/**
	 * Creates empty mapping model using provided mapping loader to get default
	 * exclusion list.
	 * 
	 * @param loader
	 *            instance of {@link IMappingLoader}
	 * @param container
	 *            where properties file is located
	 * @return mapping model
	 * @throws IOException
	 */
	public static IMappingModel createEmptyModel(IMappingLoader loader,
			File container) throws IOException {
		if (container == null || !container.exists()) {
			return null;
		}
		return new MappingModel(loader, null, container);
	}

}
