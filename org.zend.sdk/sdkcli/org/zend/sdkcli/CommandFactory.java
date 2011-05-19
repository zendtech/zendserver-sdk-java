/*******************************************************************************
 * Copyright (c) May 17, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/

package org.zend.sdkcli;

import org.zend.sdkcli.internal.commands.CommandLine;
import org.zend.sdkcli.internal.commands.CreateProjectCommand;

/**
 * Creates command instance.
 * 
 * @author Wojciech Galanciak, 2011
 * 
 */
public class CommandFactory {

	/**
	 * Creates {@link ICommand} implementor instance based on a given command
	 * line.
	 * 
	 * @param line
	 *            - command line
	 * @return command instance
	 * @throws ParseError
	 */
	public static ICommand createCommand(CommandLine line) throws ParseError {
		CommandType type = CommandType.byCommandLine(line);
		ICommand command = null;
		switch (type) {
		case CREATE_PROJECT:
			command = new CreateProjectCommand(line);
			break;
		default:
			break;
		}
		return command;
	}

}
