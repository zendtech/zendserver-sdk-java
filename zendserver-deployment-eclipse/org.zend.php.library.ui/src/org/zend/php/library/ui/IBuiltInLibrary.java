/*******************************************************************************
 * Copyright (c) 2013 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.php.library.ui;

import org.eclipse.dltk.core.IBuildpathEntry;

/**
 * @author Wojciech Galanciak, 2013
 *
 */
public interface IBuiltInLibrary {
	
	String getName();
	
	String getVersion();
	
	IBuildpathEntry[] getBuildpathEntries();
	
}
