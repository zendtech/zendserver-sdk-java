/*******************************************************************************
 * Copyright (c) Dec 11, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.sdkcli;

import java.net.URISyntaxException;

import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.transport.URIish;
import org.zend.sdklib.internal.target.ZendDevCloud;

/**
 * 
 * Git helper provides methods method to get specific information about git
 * repository.
 * 
 * @author Wojciech Galanciak, 2011
 * 
 */
public class GitHelper {

	public static final String ZEND_CLOUD_REMOTE = "zendCloudRemote";

	/**
	 * Returns remote valued based on provided git repository URL.
	 * 
	 * @param url
	 * @return {@link GitHelper#ZEND_CLOUD_REMOTE} if it is phpCloud repository;
	 *         otherwise return {@link Constants#DEFAULT_REMOTE_NAME}
	 */
	public static String getRemote(String url) {
		try {
			URIish uri = new URIish(url);
			if (uri.getHost().endsWith(ZendDevCloud.DEVPASS_HOST)) {
				return ZEND_CLOUD_REMOTE;
			}
		} catch (URISyntaxException e) {
			// return default Constants.DEFAULT_REMOTE_NAME
		}
		return Constants.DEFAULT_REMOTE_NAME;
	}

}
