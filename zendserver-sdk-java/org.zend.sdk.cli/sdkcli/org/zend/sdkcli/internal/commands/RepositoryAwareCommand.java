/*******************************************************************************
 * Copyright (c) May 18, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.sdkcli.internal.commands;

import org.zend.sdklib.internal.repository.UserBasedRepositoryLoader;
import org.zend.sdklib.manager.RepositoryManager;

/**
 * Base class for all command lines that need access to repository manager
 * 
 * @author Roy, 2011
 */
public abstract class RepositoryAwareCommand extends AbstractCommand {

	private final RepositoryManager manager;

	public RepositoryAwareCommand() {
		manager = new RepositoryManager(new UserBasedRepositoryLoader());
	}

	/**
	 * @return the target manager
	 */
	public RepositoryManager getRepositoryManager() {
		return manager;
	}

}
