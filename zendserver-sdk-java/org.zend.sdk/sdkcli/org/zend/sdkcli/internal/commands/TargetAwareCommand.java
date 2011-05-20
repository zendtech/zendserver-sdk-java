/*******************************************************************************
 * Copyright (c) May 18, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.sdkcli.internal.commands;

import org.zend.sdkcli.ParseError;
import org.zend.sdklib.internal.target.UserBasedTargetLoader;
import org.zend.sdklib.manager.TargetsManager;

/**
 * Base class for all command lines that need access to target manager
 * 
 * @author Roy, 2011
 */
public abstract class TargetAwareCommand extends AbstractCommand {

	private final TargetsManager manager;

	public TargetAwareCommand(CommandLine commandLine) throws ParseError {
		super(commandLine);
		manager = new TargetsManager(new UserBasedTargetLoader());
	}

	/**
	 * @return the target manager
	 */
	public TargetsManager getTargetManager() {
		return manager;
	}

}
