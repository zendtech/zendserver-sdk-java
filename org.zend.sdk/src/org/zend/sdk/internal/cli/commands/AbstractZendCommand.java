/*******************************************************************************
 * Copyright (c) May 16, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/

package org.zend.sdk.internal.cli.commands;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.ParseException;
import org.zend.sdk.cli.commands.IZendCommand;
import org.zend.sdk.internal.cli.options.CommandOptions;

/**
 * Represents basic class for commands.
 * 
 * @author Wojciech Galanciak, 2011
 * 
 */
public abstract class AbstractZendCommand implements IZendCommand {

	private CommandOptions options;
	private CommandLine commandLine;
	private CommandLineParser parser;

	public AbstractZendCommand() {
		this.parser = new GnuParser();
	}

	public boolean execute(String[] arguments) {
		try {
			parse(arguments);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		parseParameters(arguments);
		return execute();
	}

	public String getParameterValue(String parameterName) {
		return commandLine.getOptionValue(parameterName);
	}

	public String[] getParameterValues(String parameterName) {
		return commandLine.getOptionValues(parameterName);
	}

	protected void setOptions(CommandOptions options) {
		this.options = options;
	}

	protected abstract boolean execute();

	protected abstract void parseParameters(String[] arguments);

	private void parse(String[] arguments) throws ParseException {
		commandLine = parser.parse(options, arguments);
	}

}
