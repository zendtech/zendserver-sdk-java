/*******************************************************************************
 * Copyright (c) May 17, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/

package org.zend.sdk.cli.commands;

/**
 * This interface represents Zend SDK command which can be executed by command
 * line call.
 * 
 * @author Wojciech Galanciak, 2011
 * 
 */
public interface IZendCommand {

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
	 * @param arguments
	 *            - array of arguments passed for a command line call
	 * @return boolean value, <code>true</code> - if execution was performed
	 *         successfully, <code>false</code> - otherwise.
	 */
	boolean execute(String[] arguments);

	/**
	 * Retrieves the value, if any, of parameter with specified name.
	 * 
	 * @param parameterName
	 *            - the name of the parameter
	 * @return Value of the argument if parameter is set, and has an value,
	 *         otherwise <code>null</code>.
	 */
	String getParameterValue(String parameterName);

	/**
	 * Retrieves the array of values, if any, of parameter with specified name.
	 * 
	 * @param parameterName
	 *            - the name of the parameter
	 * @return Value of the argument if parameter is set, and has an value,
	 *         otherwise <code>null</code>.
	 */
	String[] getParameterValues(String parameterName);

}
