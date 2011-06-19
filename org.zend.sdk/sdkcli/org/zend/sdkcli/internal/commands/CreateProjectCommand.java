/*******************************************************************************
 * Copyright (c) May 16, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.sdkcli.internal.commands;

import java.io.File;

import org.zend.sdkcli.internal.options.Option;
import org.zend.sdklib.application.ZendProject;
import org.zend.sdklib.application.ZendProject.SampleApplications;

/**
 * Represents create-project command. In the result of calling it new PHP
 * project is created in defined location or a current location.
 * 
 * @author Wojciech Galanciak, 2011
 * 
 */
public class CreateProjectCommand extends AbstractCommand {

	public static final String NAME = "n";
	public static final String OMIT_SCRIPTS = "a";
	public static final String DESTINATION = "d";

	/**
	 * @return The project name
	 */
	@Option(opt = NAME, required = true, description = "The project name", argName = "name")
	public String getName() {
		return getValue(NAME);
	}

	/**
	 * @return The project destination
	 */
	@Option(opt = DESTINATION, required = false, description = "The project destination", argName = "path")
	public String getDestionation() {
		return getValue(DESTINATION);
	}

	/**
	 * @return Whether to create sample deployment scripts
	 */
	@Option(opt = OMIT_SCRIPTS, required = false, description = "Omit deployment scripts")
	public boolean isOmitScript() {
		return hasOption(OMIT_SCRIPTS);
	}

	@Override
	public boolean doExecute() {
		String path = getDestionation();
		if (path == null) {
			path = getCurrentDirectory();
		}
		ZendProject project = new ZendProject(getName(), !isOmitScript(), new File(path));
		return project.create(SampleApplications.HELLO_WORLD);
	}
}
