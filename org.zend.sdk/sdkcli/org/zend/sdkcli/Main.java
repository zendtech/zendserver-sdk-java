/*******************************************************************************
 * Copyright (c) May 16, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.sdkcli;

import org.zend.sdkcli.internal.commands.CommandLine;
import org.zend.sdkcli.internal.commands.CreateProjectCommand;

/**
 * Main class which is responsible for handling command line requests.
 * 
 * @author Wojciech Galanciak, 2011
 * 
 */
public class Main {

	public static void main(String[] args) {

		// TODO logger should be assigned here
		
		// resolve verb and directObject
		try {

			// Manager for the command line tool
			CommandLine commandLine = new CommandLine(args);

			if ("create".equals(commandLine.getVerb())) {
				
				if ("project".equals(commandLine.getDirectObject())) {
					new CreateProjectCommand(commandLine).execute();
				}
				
			}

			else if ("update ".equals(commandLine.getVerb())) {

			}

			else if ("delete".equals(commandLine.getVerb())) {

			}

			else if ("list".equals(commandLine.getVerb())) {
				
			} else {
				commandLine.printUsage();
			}

		} catch (ParseError e) {
			CommandLine.printUsage(e);
			
			// TODO: use logger here to log status
		}
	}

}
