/*******************************************************************************
 * Copyright (c) May 25, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.sdkcli.internal.commands;

import org.zend.sdkcli.internal.options.Option;
import org.zend.sdklib.application.ZendApplication;
import org.zend.sdklib.internal.target.UserBasedTargetLoader;

/**
 * Base class for all command lines that need access to zend application.
 * 
 * @author Wojciech Galanciak, 2011
 * 
 */
public abstract class ApplicationAwareCommand extends TargetAwareCommand {

	/**
	 * The target id is mandatory for all application operations
	 */
	private static final String TARGET = "t";

	private final ZendApplication application;

	public ApplicationAwareCommand() {
		application = new ZendApplication(new UserBasedTargetLoader());
	}

	public ZendApplication getApplication() {
		return application;
	}

	@Option(opt = TARGET, required = false, description = "The target id to use, if target is not provided then the default taget is used", argName = "id")
	public String getTargetId() {
		String t = getValue(TARGET);
		if (t == null) {
			t = getDefaultTargetId();
		}
		return t;
	}
}
