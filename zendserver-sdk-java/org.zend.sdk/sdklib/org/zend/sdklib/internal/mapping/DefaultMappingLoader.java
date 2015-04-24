/*******************************************************************************
 * Copyright (c) Jun 27, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.sdklib.internal.mapping;

import java.io.IOException;
import java.util.List;

import org.zend.sdklib.mapping.IMapping;
import org.zend.sdklib.mapping.PropertiesBasedMappingLoader;

public class DefaultMappingLoader extends PropertiesBasedMappingLoader {

	private static final String[] defaultExclusion = new String[] { "**/.svn",
			"**/.cvs", "**/.git" };

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.zend.sdklib.mapping.IMappingLoader#getDefaultExclusion()
	 */
	@Override
	public List<IMapping> getDefaultExclusion() throws IOException {
		return getMappings(defaultExclusion);
	}

}
