/*******************************************************************************
 * Copyright (c) 2014 Zend Technologies Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Zend Technologies Ltd. - initial API and implementation
 *******************************************************************************/
package org.zend.php.server.internal.ui;

/**
 * @author Wojciech Galanciak, 2014
 * 
 */
public interface IHelpContextIds {

	String PREFIX = "http://files.zend.com/help/Zend-Studio-11/zend-studio.htm#"; //$NON-NLS-1$
	String SUFFIX = ".htm?zs"; //$NON-NLS-1$

	String ZEND_SERVER = PREFIX + "zend_certified_php_distribution" + SUFFIX; //$NON-NLS-1$

	String ADDING_A_SERVER_REMOTE_ZEND_SERVER = PREFIX
			+ "adding_a_remote_zend_server" + SUFFIX; //$NON-NLS-1$

	String ADDING_A_SERVER_APACHE_HTTP_SERVER = PREFIX
			+ "adding_a_local_apache_server" + SUFFIX; //$NON-NLS-1$

	String ADDING_A_SERVER_ZEND_DEVELOPER_CLOUD_SERVER = PREFIX
			+ "adding_a_zend_developer_cloud_php_server" + SUFFIX; //$NON-NLS-1$

	String ADDING_A_SERVER_LOCAL_ZEND_SERVER = PREFIX
			+ "adding_a_local_zend_server" + SUFFIX; //$NON-NLS-1$

}
