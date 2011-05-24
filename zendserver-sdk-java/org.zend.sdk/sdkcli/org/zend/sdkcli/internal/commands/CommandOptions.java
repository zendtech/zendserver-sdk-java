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

/**
 * Represents command line options. It extends {@link Options} to provide some
 * helper methods for adding new options.
 * 
 * @author Wojciech Galanciak, 2011
 * 
 */
public class CommandOptions extends Options {

	private static final long serialVersionUID = -2778783894326385212L;

	public static final String CURR_DIR = "currDir";

	public CommandOptions() {
		addArgumentOption(CURR_DIR, false, "current directory");
	}

	/**
	 * Adds options which has one argument and has no description.
	 * 
	 * @param name
	 *            - option name
	 * @param isRequired
	 *            - boolean value which defines if option is required
	 */
	public void addOption(String name, boolean isRequired) {
		addArgumentOption(name, isRequired, "");
	}

	/**
	 * Adds options which has one argument.
	 * 
	 * @param name
	 *            - option name
	 * @param isRequired
	 *            - boolean value which defines if option is required
	 * @param description
	 *            - option description
	 */
	public void addArgumentOption(String name, boolean isRequired,
			String description) {
		Option option = new Option(name, true, description);
		option.setArgName(name);
		option.setArgs(1);
		option.setRequired(isRequired);
		this.addOption(option);
	}

	/**
	 * Adds options which has no arguments.
	 * 
	 * @param name
	 *            - option name
	 * @param isRequired
	 *            - boolean value which defines if option is required
	 * @param description
	 *            - option description
	 */
	public void addBooleanOption(String name, boolean isRequired,
			String description) {
		Option option = new Option(name, false, description);
		option.setRequired(isRequired);
		this.addOption(option);
	}

}
