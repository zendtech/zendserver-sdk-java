/*******************************************************************************
 * Copyright (c) May 16, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.sdkcli;

import org.zend.sdkcli.internal.commands.CommandLine;

/**
 * Main class which is responsible for handling command line requests.
 * 
 * @author Wojciech Galanciak, 2011
 * 
 */
public class Main {

	public static void main(String[] args) {

		// TODO logger should be assigned here

		try {
			// Manager for the command line tool
			CommandLine commandLine = new CommandLine(args);
			ICommand command = CommandFactory.createCommand(commandLine);
			command.execute();
		} catch (ParseError e) {
			CommandLine.printUsage(e);

			// TODO: use logger here to log status
		}
	}

}
