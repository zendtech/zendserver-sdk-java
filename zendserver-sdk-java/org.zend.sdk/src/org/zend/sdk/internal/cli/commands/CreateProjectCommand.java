/*******************************************************************************
 * Copyright (c) May 16, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/

package org.zend.sdk.internal.cli.commands;

import org.zend.sdk.internal.cli.options.CreateProjectOptions;

/**
 * Concrete implementation of {@link AbstractZendCommand}. It represents
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
public class CreateProjectCommand extends AbstractZendCommand {

	public static final String NAME = "create-project";

	private String target;
	private String name;
	private String index;
	private String path;

	public CreateProjectCommand() {
		setOptions(CreateProjectOptions.createOptions());
	}

	@Override
	protected void parseParameters(String[] arguments) {
		target = getParameterValue(CreateProjectOptions.TARGET);
		name = getParameterValue(CreateProjectOptions.NAME);
		index = getParameterValue(CreateProjectOptions.INDEX);
		path = getParameterValue(CreateProjectOptions.PATH);
	}

	@Override
	protected boolean execute() {
		// TODO Auto-generated method stub
		return true;
	}
}
