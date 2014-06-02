/*******************************************************************************
 * Copyright (c) 2014 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.php.server.internal.ui.startup;

import org.eclipse.osgi.util.NLS;

/**
 * @author Wojciech Galanciak, 2014
 *
 */
public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.zend.php.server.internal.ui.startup.messages"; //$NON-NLS-1$
	public static String LocalZendServerStartup_FoundMessage;
	public static String LocalZendServerStartup_FoundTitle;
	public static String LocalZendServerStartup_NotFoundMessage;
	public static String LocalZendServerStartup_NotFoundTitle;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
