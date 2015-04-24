/*******************************************************************************
 * Copyright (c) May 18, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.sdkcli.internal.commands;

import org.zend.sdkcli.internal.options.Option;
import org.zend.sdklib.SdkException;
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
	private static final String NAME = "n";

	@Option(opt = URL, required = true, description = "Url of the repository", argName = "url")
	public String getUrl() {
		return getValue(URL);
	}
	
	@Option(opt = NAME, required = false, description = "Name of the repository", argName = "name")
	public String getName() {
		return getValue(NAME);
	}

	@Override
	public boolean doExecute() {
		final String url = getUrl();
		final String name = getName();

		final RepositoryManager repositoryManager = getRepositoryManager();
		final IRepository r = repositoryManager.createRepository(url, name);
		if (r == null) {
			return false;
		}
		try {
			repositoryManager.add(r);
		} catch (SdkException e) {
			getLogger().error("Error adding repository ('" + url + "').");
			getLogger().error(e);
			return false;
		}
		
		getLogger().info("Repository was added ('" + url + "').");
		return true;
	}
}
