/*******************************************************************************
 * Copyright (c) May 16, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/

package org.zend.sdkcli.internal.commands;

import org.apache.commons.cli.Options;
import org.zend.sdkcli.ICommand;
import org.zend.sdkcli.Main;
import org.zend.sdkcli.ParseError;
import org.zend.sdklib.logger.ILogger;
import org.zend.sdklib.logger.Log;

/**
 * Represents basic class for commands.
 * 
 * @author Wojciech Galanciak, 2011
 * 
 */
public abstract class AbstractCommand implements ICommand {

	final protected CommandLine commandLine;
	final protected CommandOptions options;

	/**
	 * @param commandLine
	 * @throws ParseError
	 */
	public AbstractCommand(CommandLine commandLine) throws ParseError {
		// build options
		this.options = new CommandOptions();
		setupOptions();

		// parse command line according to options
		this.commandLine = commandLine;
		commandLine.parse(options);
	}

	/**
	 * Commands setup their {@link Options}
	 */
	protected abstract void setupOptions();

	/**
	 * Helper method for {@link AbstractCommand#setupOptions()} method
	 * 
	 * @see CommandOptions#addArgumentOption(Sting, boolean, String)
	 * @param name
	 * @param isRequired
	 * @param description
	 */
	protected void addArgumentOption(String name, boolean isRequired,
			String description) {
		options.addArgumentOption(name, isRequired, description);
	}

	/**
	 * Helper method for {@link AbstractCommand#setupOptions()} method
	 * 
	 * @see CommandOptions#addBooleanOption(Sting, boolean, String)
	 * @param name
	 * @param isRequired
	 * @param description
	 */
	protected void addBooleanOption(String name, boolean isRequired,
			String description) {
		options.addBooleanOption(name, isRequired, description);
	}

	public String getValue(String parameterName) {
		return commandLine.getParameterValue(parameterName);
	}

	public boolean hasOption(String parameterName) {
		return commandLine.hasOption(parameterName);
	}

	public String[] getValues(String parameterName) {
		return commandLine.getParameterValues(parameterName);
	}

	/**
	 * @return the available logger for command line
	 */
	public ILogger getLogger() {
		return Log.getInstance().getLogger(Main.class.getName());
	}

}
