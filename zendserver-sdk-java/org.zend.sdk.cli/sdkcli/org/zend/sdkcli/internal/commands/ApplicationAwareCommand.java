/*******************************************************************************
 * Copyright (c) May 25, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.sdkcli.internal.commands;

import org.zend.sdkcli.internal.mapping.CliMappingLoader;
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
		application = new ZendApplication(new UserBasedTargetLoader(),
				new CliMappingLoader());
	}

	public ZendApplication getApplication() {
		return application;
	}

	@Option(opt = TARGET, required = false, description = "Target id of the this application, if target id is not specified then the default target is used", argName = "id")
	public String getTargetId() {
		String t = getValue(TARGET);
		if (t == null) {
			t = getDefaultTargetId();
			getLogger().debug("Used target ID: " + t);
		}
		return t;
	}
}
