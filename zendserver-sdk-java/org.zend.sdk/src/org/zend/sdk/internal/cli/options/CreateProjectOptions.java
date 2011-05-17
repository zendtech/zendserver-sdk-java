/*******************************************************************************
 * Copyright (c) May 16, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/

package org.zend.sdk.internal.cli.options;

public class CreateProjectOptions extends CommandOptions {

	private static final long serialVersionUID = 3070200452844304885L;

	public static final String TARGET = "target";
	public static final String NAME = "name";
	public static final String INDEX = "index";
	public static final String PATH = "path";

	private CreateProjectOptions() {
		super();
	}

	public static CommandOptions createOptions() {
		CommandOptions options = new CreateProjectOptions();
		options.prepareOptions();
		return options;
	}

	protected void prepareOptions() {
		super.prepareOptions();
		addArgumentOption(TARGET, false);
		addArgumentOption(NAME, true);
		addArgumentOption(INDEX, false);
		addArgumentOption(PATH, false);
	}

}
