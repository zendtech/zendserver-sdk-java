/*******************************************************************************
 * Copyright (c) May 24, 2011 Zend Technologies Ltd. 
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
 * Delete an existing repository
 * 
 * @author Roy, 2011
 * 
 */
public class RemoveRepositoryCommand extends RepositoryAwareCommand {

	private static final String URL = "u";

	@Option(opt = URL, required = true, description = "Url of the repository to remove", argName = "url")
	public String getUrl() {
		return getValue(URL);
	}

	@Override
	public boolean doExecute() {
		final String url = getUrl();

		RepositoryManager tm = getRepositoryManager();
		IRepository target = tm.getRepositoryById(url);
		if (target == null) {
			getLogger().error(
					"Repository '" + url + "' does not exist.");
			return false;
		}

		IRepository removed = tm.remove(target);
		if (removed == null) {
			getLogger().error("Failed to remove repository '" + url + "'");
			return false;
		}

		getLogger().info("Repository removed ('" + url + "').");
		return true;
	}

}
