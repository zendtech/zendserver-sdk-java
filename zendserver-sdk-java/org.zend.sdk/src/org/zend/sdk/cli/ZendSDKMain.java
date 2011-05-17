/*******************************************************************************
 * Copyright (c) May 16, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/

package org.zend.sdk.cli;

import org.apache.commons.cli.ParseException;
import org.zend.sdk.cli.commands.CreateProjectCommand;
import org.zend.sdk.cli.commands.ZendCommand;

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
		ZendCommand command = null;
		if (CreateProjectCommand.CREATE_PROJECT.equals(args[0])) {
			command = new CreateProjectCommand();
		}
		if (command != null) {
			try {
				command.execute(args);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
