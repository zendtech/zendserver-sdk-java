/*******************************************************************************
 * Copyright (c) 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.php.zendserver.deployment.debug.ui;

public interface HelpContextIds {

	public static final String PREFIX = "http://files.zend.com/help/Zend-Studio-13.6/content/"; //$NON-NLS-1$
	public static final String SUFFIX = ".htm?zs"; //$NON-NLS-1$

	public static final String LAUNCHING_AN_APPLICATION = PREFIX
			+ "launching_an_application" + SUFFIX; //$NON-NLS-1$

	public static final String DEPLOYING_AN_APPLICATION = PREFIX
			+ "deploying_an_application"+ SUFFIX;; //$NON-NLS-1$

	public static final String DEBUGGING_AN_APPLICAITON = PREFIX
			+ "debugging_an_applicaiton_"+ SUFFIX; //$NON-NLS-1$

	public static final String DEPLOY_PHP_LIBRARY = PREFIX
			+ "deploying_libraries" + SUFFIX;//$NON-NLS-1$

	public static final String DEPLOY_PHP_LIBRARY_ON_TARGET = PREFIX
			+ "deploying_libraries" + SUFFIX;//$NON-NLS-1$

}
