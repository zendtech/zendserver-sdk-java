/*******************************************************************************
 * Copyright (c) May 16, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.sdkcli;

import java.util.logging.Handler;
import java.util.logging.LogManager;

import org.slf4j.bridge.SLF4JBridgeHandler;
import org.zend.sdkcli.internal.commands.CommandLine;
import org.zend.sdkcli.internal.commands.UsageCommand;
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
		CommandLine commandLine = new CommandLine(args, log);
		try {
			// Manager for the command line tool
			ICommand command = CommandFactory.createCommand(commandLine);
			command.execute(commandLine);
		} catch (ParseError e) {
			log.error("An error occured: " + e.getMessage());
			UsageCommand helpCmd = (UsageCommand) CommandFactory
					.createCommand(CommandType.HELP);
			helpCmd.execute(commandLine);
		}
	}

	private static void initLogger() {
		Log.getInstance().registerLogger(new CliLogger());
		log = Log.getInstance().getLogger(Main.class.getName());
		java.util.logging.Logger rootLogger = LogManager.getLogManager()
				.getLogger("");
		Handler[] handlers = rootLogger.getHandlers();
		for (int i = 0; i < handlers.length; i++) {
			rootLogger.removeHandler(handlers[i]);
		}
		SLF4JBridgeHandler.install();

	}

}
