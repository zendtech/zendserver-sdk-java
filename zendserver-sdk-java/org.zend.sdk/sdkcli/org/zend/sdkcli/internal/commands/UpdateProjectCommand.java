/*******************************************************************************
 * Copyright (c) May 16, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/

package org.zend.sdkcli.internal.commands;

import org.zend.sdkcli.internal.options.Option;
import org.zend.sdklib.ZendProject;

/**
 * Updates project.
 * 
 * @author Wojciech Galanciak, 2011
 * 
 */
public class UpdateProjectCommand extends AbstractCommand {

	public static final String NAME = "n";
	public static final String IGNORE_SCRIPTS = "i";
	public static final String DESTINATION = "d";

	@Option(opt = IGNORE_SCRIPTS, required = false, description = "ignore scripts")
	public boolean isIgnoreScripts() {
		return hasOption(IGNORE_SCRIPTS);
	}

	@Option(opt = DESTINATION, required = true, description = "The path to the project folder", argName = "path")
	public String getDestination() {
		return getValue(DESTINATION);
	}

	@Option(opt = NAME, required = false, description = "The project name", argName="name")
	public String getName() {
		return getValue(NAME);
	}

	@Override
	public boolean doExecute() {
		String path = getDestination();
		ZendProject project = new ZendProject(getName(),
				!isIgnoreScripts(), path);

		try {
			return project.update();
		} catch (IllegalArgumentException e) {
			getLogger().error(e.getMessage());
			return false;
		}
	}
}
