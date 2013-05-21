/*******************************************************************************
 * Copyright (c) 2013 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.php.library.core;

import org.eclipse.core.runtime.IPath;
import org.eclipse.dltk.core.BuildpathContainerInitializer;

/**
 * @author Wojciech Galanciak, 2013
 *
 */
public abstract class CustomBuildpathContainerInitializer extends
		BuildpathContainerInitializer {
	
	public CustomBuildpathContainerInitializer() {
		super();
	}

	public abstract IPath getPath();

}
