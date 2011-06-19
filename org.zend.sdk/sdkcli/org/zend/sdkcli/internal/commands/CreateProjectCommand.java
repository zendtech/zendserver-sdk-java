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
public class CreateProjectCommand extends AbstractCommand  {

	public static final String NAME = "n";
	public static final String SCRIPTS = "s";
	public static final String DESTINATION = "d";

	/**
	 * @return The project destination
	 */
	@Option(opt = DESTINATION, required = false, description = "The project destination", argName = "path")
	public File getDestination() {
		final String value = getValue(DESTINATION);
		return value == null ? resolveDestination(getCurrentDirectory(),
				getName()) : new File(value);
	}

	@Option(opt = SCRIPTS, required = false, description = "Generate deployment scripts, "
			+ "consider using one of these options [all|postActivate|postDeactivate|postStage|postUnstage|preActivate|preDeactivate|preStage|preUnstage]")
	public String getScripts() {
		return getValue(SCRIPTS);
	}

	/**
	 * @return The project name
	 */
	@Option(opt = NAME, required = true, description = "The project name", argName = "name")
	public String getName() {
		return getValue(NAME);
	}

	@Override
	public boolean doExecute() {
		ZendProject project = new ZendProject(getScripts(), getDestination());
		return project.create(getName(), getProjectType());
	}

	/**
	 * @return
	 */
	protected SampleApplications getProjectType() {
		return SampleApplications.HELLO_WORLD;
	}

	/**
	 * If nest is on, resolve the nested destination folder
	 * 
	 * @param destination2
	 * @param nest2
	 * @return
	 */
	private File resolveDestination(String destination, String name) {
		File projectRoot = new File(destination, name);
		if (!projectRoot.exists()) {
			final boolean mkdir = projectRoot.mkdir();
			if (!mkdir) {
				return null;
			}
		}
		return projectRoot;
	}

}
