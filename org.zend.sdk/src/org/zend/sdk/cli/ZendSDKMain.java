/*******************************************************************************
 * Copyright (c) May 16, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/

package org.zend.sdk.cli;

import org.zend.sdk.cli.commands.IZendCommand;
import org.zend.sdk.internal.cli.commands.CreateProjectCommand;

/**
 * Main class which is responsible for handling command line requests.
 * 
 * @author Wojciech Galanciak, 2011
 * 
 */
public class ZendSDKMain {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		IZendCommand command = null;
		if (CreateProjectCommand.NAME.equals(args[0])) {
			command = new CreateProjectCommand();
		}
		if (command != null) {
			command.execute(args);
		}
	}

}
