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
import java.util.Set;

/**
 * Interface for mapping loaders. Mapping loader is responsible for low level
 * operations during loading and storing resource mappings.
 * 
 * @author Wojciech Galanciak, 2011
 * 
 */
public interface IMappingLoader {

	/**
	 * Loads resource mappings from provided input stream.
	 * 
	 * @param stream
	 *            for the properties file
	 * @return parsed mapping from the properties file
	 * @throws IOException
	 */
	IResourceMapping load(InputStream stream) throws IOException;

	/**
	 * Stores specified resource mapping in the output file,
	 * 
	 * @param mapping
	 * @param output
	 * @return
	 * @throws IOException
	 */
	OutputStream store(IResourceMapping mapping, File output)
			throws IOException;

	/**
	 * Returns list of default exclusion.
	 * 
	 * @return parsed mapping for default exclusion
	 * @throws IOException
	 */
	Set<IMapping> getDefaultExclusion() throws IOException;

}
