/*******************************************************************************
 * Copyright (c) May 16, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/

package org.zend.sdk.cli.commands;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 * Represents basic class for commands.
 * 
 * @author Wojciech Galanciak, 2011
 * 
 */
public class ZendCommand {

	protected Options options;
	private CommandLine commandLine;
	private CommandLineParser parser;

	public ZendCommand() {
		this.parser = new GnuParser();
	}

	public boolean execute(String[] arguments) throws ParseException {
		parse(arguments);
		return true;
	}

	protected void parse(String[] arguments) throws ParseException {
		commandLine = parser.parse(options, arguments);
	}

	protected String getValue(String name) {
		return commandLine.getOptionValue(name);
	}

	protected String[] getValues(String name) {
		return commandLine.getOptionValues(name);
	}

}
