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
 * TODO document
 */
public class CommandOptions extends Options {

	private static final long serialVersionUID = -2778783894326385212L;

	public static final String CURR_DIR = "currDir";

	public CommandOptions() {
		addArgumentOption(CURR_DIR, false);
	}

	public void addOption(String name, boolean hasArgs) {
		Option option = new Option(name, hasArgs, "");
		this.addOption(option);
	}

	public void addArgumentOption(String name, boolean isRequired) {
		Option option = new Option(name, true, "");
		option.setArgName(name);
		option.setArgs(1);
		option.setRequired(isRequired);
		this.addOption(option);
	}

}
