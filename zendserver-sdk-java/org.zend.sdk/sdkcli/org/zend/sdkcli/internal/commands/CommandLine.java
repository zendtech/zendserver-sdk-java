/*******************************************************************************
 * Copyright (c) May 16, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.sdkcli.internal.commands;

import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.zend.sdkcli.ParseError;

/**
 * Helps forming the command line life cycle. At first it parses the right
 * command options according to the verb @see {@link CommandLine#verb} and
 * directObject {@link CommandLine#directObject} and then retrieving the command
 * values.
 * 
 * @author Roy, 2011
 */
public class CommandLine {

	/**
	 * Original arguments array
	 */
	final private String[] arguments;

	/**
	 * Resolved verb of this command
	 */
	private String verb = null;

	/**
	 * Resolved direct object of this command
	 */
	private String directObject = null;

	/**
	 * Low-level command line instance 
	 */
	private org.apache.commons.cli.CommandLine parse;

	public CommandLine(String[] args) throws ParseError {
		this.arguments = args;
		heuristicParse();
	}

	private void heuristicParse() {
		if (arguments.length > 0) {
			this.verb = arguments[0];
		}
		if (arguments.length > 1) {
			this.directObject = arguments[1];
		}
	}

	public String getDirectObject() {
		return directObject;
	}

	public String getVerb() {
		return verb;
	}

	/**
	 * 
	 * @param options
	 * @return
	 * @throws ParseError
	 */
	public void parse(Options options) throws ParseError {
		CommandLineParser parser = new GnuParser();

		try {
			parse = parser.parse(options, arguments);

		} catch (ParseException e) {
			throw new ParseError(e);
		}

	}

	public void printUsage() {
		// TODO Auto-generated method stub

	}

	public static void printUsage(ParseError e) {
		// TODO Auto-generated method stub

	}

	/**
	 * @param parameterName
	 * @return
	 */
	public String getParameterValue(String parameterName) {
		return parse.getOptionValue(parameterName);
	}

	/**
	 * @param parameterName
	 * @return
	 */
	public String[] getParameterValues(String parameterName) {
		return parse.getOptionValues(parameterName);
	}

}
