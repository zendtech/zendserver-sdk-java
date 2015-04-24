/*******************************************************************************
 * Copyright (c) May 16, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/

package org.zend.sdkcli.internal.commands;

import java.io.File;

import org.zend.sdkcli.internal.mapping.CliMappingLoader;
import org.zend.sdkcli.internal.options.Option;
import org.zend.sdklib.application.ZendProject;

/**
 * Updates project.
 * 
 * @author Wojciech Galanciak, 2011
 * 
 */
public class UpdateProjectCommand extends AbstractCommand {

	public static final String SCRIPTS = "s";
	public static final String DESTINATION = "d";

	/**
	 * @return The project destination
	 */
	@Option(opt = DESTINATION, required = false, description = "The project destination", argName = "path")
	public File getDestination() {
		String value = getValue(DESTINATION);
		if (value == null) {
			value = getCurrentDirectory();
		}
		return new File(value);
	}

	@Option(opt = SCRIPTS, required = false, description = "Generate deployment scripts, "
			+ "consider using one of these options [all|postActivate|postDeactivate|postStage|postUnstage|preActivate|preDeactivate|preStage|preUnstage]")
	public String getScripts() {
		return getValue(SCRIPTS);
	}

	@Override
	public boolean doExecute() {
		ZendProject project = new ZendProject(getDestination(),
				new CliMappingLoader());

		try {
			final boolean update = project.update(getScripts());

			if (update) {
				getLogger().info(
						"Project is updated with deployment descriptor and properties");
			}
			return update;
		} catch (IllegalArgumentException e) {
			getLogger().error(e.getMessage());
			return false;
		}
	}
}
