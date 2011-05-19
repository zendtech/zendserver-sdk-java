/*******************************************************************************
 * Copyright (c) May 16, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.sdkcli;

import org.zend.sdkcli.internal.commands.CommandLine;
import org.zend.sdkcli.internal.logger.CliLogger;
import org.zend.sdklib.logger.ILogger;
import org.zend.sdklib.logger.Log;

/**
 * Main class which is responsible for handling command line requests.
 * 
 * @author Wojciech Galanciak, 2011
 * 
 */
public class Main {

	private static ILogger log;

	public static void main(String[] args) {
		initLogger();
		try {
			// Manager for the command line tool
			CommandLine commandLine = new CommandLine(args, log);
			ICommand command = CommandFactory.createCommand(commandLine);
			command.execute();
		} catch (ParseError e) {
			CommandLine.printUsage(e);
			log.error(e);
		}
	}

	private static void initLogger() {
		Log.getInstance().registerLogger(new CliLogger());
		log = Log.getInstance().getLogger(Main.class.getName());
	}

}
