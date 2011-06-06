/*******************************************************************************
 * Copyright (c) May 18, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.sdkcli.internal.commands;

import org.zend.sdkcli.internal.options.Option;
import org.zend.sdklib.manager.RepositoryManager;
import org.zend.sdklib.repository.IRepository;

/**
 * Creating a new repository
 * 
 * @author Roy, 2011
 * 
 */
public class AddRepositoryCommand extends RepositoryAwareCommand {

	// options
	private static final String URL = "u";

	@Option(opt = URL, required = true, description = "Repository url", argName = "url")
	public String getUrl() {
		return getValue(URL);
	}

	@Override
	public boolean doExecute() {
		final String url = getUrl();

		if (url == null || url.length() == 0) {
			getLogger()
					.error("To add a repository it is required to provide a full URL.");
			return false;
		}

		final RepositoryManager repositoryManager = getRepositoryManager();
		final IRepository r = repositoryManager.createRepository(url);
		if (r == null) {
			return false;
		}

		getLogger().info("Repository added ('" + url + "').");
		return true;
	}
}
