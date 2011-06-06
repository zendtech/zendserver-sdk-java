/*******************************************************************************
 * Copyright (c) May 18, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.sdkcli.internal.commands;

import org.zend.sdklib.repository.IRepository;

/**
 * List all available repositories 
 * 
 * @author Roy, 2011
 */
public class ListRepositoriesCommand extends RepositoryAwareCommand {

	@Override
	public boolean doExecute() {
		final IRepository[] list = getRepositoryManager().getRepositories();
		if (list.length == 0) {
			commandLine.getLog().info("No Available Repositories.");
			return true;
		}

		commandLine.getLog().info("Available Repositories:");
		for (IRepository r : list) {
			commandLine.getLog().info("id: " + r.getId());
		}
		return true;
	}
}
