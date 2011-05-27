/*******************************************************************************
 * Copyright (c) May 17, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/

package org.zend.sdkcli;

import org.zend.sdkcli.internal.commands.CommandLine;

/**
 * This interface represents Zend SDK command which can be executed by command
 * line call.
 * 
 * @author Wojciech Galanciak, 2011
 * 
 */
public interface ICommand {

	/**
	 * Performs execution of a command which implements this interface. As an
	 * argument it takes array of Strings which were passed as arguments for
	 * command line call. The list of arguments should have following structure:
	 * <blockquote><b>command_name [-parameter_name
	 * [parameter_value]]*</b></blockquote>where:<br>
	 * <b>command_name</b> - name of the command<br>
	 * <b>parameter_name</b> - parameter name; number and optionality of
	 * parameters depend on a particular command implementation.<br>
	 * <b>parameter_value</b> - parameter value.<br>
	 * <br>
	 * 
	 * @return boolean value, <code>true</code> - if execution was performed
	 *         successfully, <code>false</code> - otherwise.
	 */
	public boolean execute(CommandLine cmdLine)  throws ParseError;
	
}
