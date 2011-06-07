/*******************************************************************************
 * Copyright (c) May 18, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.sdkhelp.validation;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.cli.Options;
import org.zend.sdkcli.CommandFactory;
import org.zend.sdkcli.CommandType;
import org.zend.sdkcli.ICommand;
import org.zend.sdkcli.internal.logger.CliLogger;
import org.zend.sdkcli.internal.options.DetectOptionUtility;
import org.zend.sdklib.logger.Log;

/**
 * A tool to generate the most updated command line tables
 * 
 * @author Roy, 2011
 * 
 */
public class ValidationReport {

	public static void main(String[] args) throws IOException {

		Log.getInstance().registerLogger(new CliLogger());
		
		PrintStream printStream = System.out;
		if (args.length > 0) {
			printStream = new PrintStream(new File(args[0]));
		}
		
		printStream.println("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>");
		
		// conflicts
		checkConflicts(printStream);		

		printStream.close();
	}

	private static void checkConflicts(PrintStream printStream) {
		printStream.println("<conflicts>");
		final CommandType[] values = CommandType.values();
		for (CommandType commandType : values) {
			final ICommand command = CommandFactory.createCommand(commandType);
			final Class<? extends ICommand> class1 = command.getClass();

			final Options options = new Options();
			final List<org.zend.sdkcli.internal.options.Option> all = DetectOptionUtility.getOptions(class1, false);

			
			Set<String> opts = new HashSet<String>();
			for (org.zend.sdkcli.internal.options.Option object : all) {
				if (opts.contains(object.opt())) {
					printStream.print("<error message=\"Option conflict in ");
					printStream.print(command.getClass().getName());
					printStream.println("\">");
					printStream.print("\tconflict with option: -");
					printStream.println(object.opt());
					printStream.print("</error>");
				} 
				opts.add(object.opt());
			}
		}
		printStream.println("</conflicts>");
	}

}
