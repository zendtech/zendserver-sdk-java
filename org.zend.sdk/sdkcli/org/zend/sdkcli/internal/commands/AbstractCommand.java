/*******************************************************************************
 * Copyright (c) May 16, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/

package org.zend.sdkcli.internal.commands;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.zend.sdkcli.ICommand;
import org.zend.sdkcli.ParseError;

/**
 * Represents basic class for commands.
 * 
 * @author Wojciech Galanciak, 2011
 * 
 */
public abstract class AbstractCommand implements ICommand {

	final protected CommandLine commandLine;
	final protected Options options;

	/**
	 * @param commandLine
	 * @throws ParseError
	 */
	public AbstractCommand(CommandLine commandLine) throws ParseError {
		// build options
		this.options = new Options();
		createOptions();

		// parse command line according to options
		this.commandLine = commandLine;
		commandLine.parse(options);
	}

	/**
	 * Commands setup their {@link Options}
	 * 
	 * @return
	 */
	protected abstract Options createOptions();

	/**
	 * Helper method for {@link AbstractCommand#createOptions()} method
	 * 
	 * @param name
	 * @param hasArgs
	 */
	protected void addOption(String name, boolean hasArgs) {
		Option option = new Option(name, hasArgs, "");
		options.addOption(option);
	}

	/**
	 * Helper method for {@link AbstractCommand#createOptions()} method
	 * 
	 * @param name
	 * @param isRequired
	 */
	protected void addArgumentOption(String name, boolean isRequired) {
		Option option = new Option(name, true, "");
		option.setArgName(name);
		option.setArgs(1);
		option.setRequired(isRequired);
		options.addOption(option);
	}

	public String getValue(String parameterName) {
		return this.commandLine.getParameterValue(parameterName);
	}

	public String[] getValues(String parameterName) {
		return this.commandLine.getParameterValues(parameterName);
	}

}
