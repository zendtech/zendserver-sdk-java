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
 * Concrete implementation of {@link AbstractCommand}. It represents
 * create-project command. In the result of calling it new PHP project is
 * created in defined location.
 * 
 * Command Parameters:
 * <table border="1">
 * <tr>
 * <th>Parameter</th>
 * <th>Required</th>
 * <th>Argument</th>
 * <th>Description</th>
 * </tr>
 * <tr>
 * <td>name</td>
 * <td>true</td>
 * <td>String</td>
 * <td>Project name.</td>
 * </tr>
 * <tr>
 * <td>target</td>
 * <td>false</td>
 * <td>String</td>
 * <td>Target ID.</td>
 * </tr>
 * <tr>
 * <td>index</td>
 * <td>false</td>
 * <td>String</td>
 * <td>Index name.</td>
 * </tr>
 * <tr>
 * <td>path</td>
 * <td>false</td>
 * <td>String</td>
 * <td>Path to the location where project should be created. If it is not
 * specified, project will be created in the current location.</td>
 * </tr>
 * </table>
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

	@Option(opt = DESTINATION, required = false, description = "The path to the project or application package")
	public String getDestination() {
		return getValue(DESTINATION);
	}

	@Option(opt = NAME, required = false, description = "The project name")
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
