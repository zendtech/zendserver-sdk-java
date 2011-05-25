/*******************************************************************************
 * Copyright (c) May 18, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.sdk.docs.generate;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.Collection;

import org.apache.commons.cli.Options;
import org.zend.sdkcli.internal.commands.AbstractCommand;
import org.zend.sdkcli.internal.commands.CreateProjectCommand;
import org.zend.sdkcli.internal.commands.CreateTargetCommand;
import org.zend.sdkcli.internal.commands.DeleteTargetCommand;
import org.zend.sdkcli.internal.commands.DetectTargetCommand;
import org.zend.sdkcli.internal.commands.ListTargetsCommand;
import org.zend.sdkcli.internal.options.DetectOptionUtility;

/**
 * A tool to generate the most updated command line tables
 * 
 * @author Roy, 2011
 * 
 */
public class GenerateManual {

	public static void main(String[] args) throws FileNotFoundException {

		PrintStream printStream = System.out;
		if (args.length > 0) {
			printStream = new PrintStream(new File(args[0]));
		}

		tableCommands(printStream, ListTargetsCommand.class,
				CreateTargetCommand.class, DetectTargetCommand.class,
				DeleteTargetCommand.class);

	}

	private static void tableCommands(PrintStream printStream, Class... commands) {

		for (Class class1 : commands) {
			final Options options = new Options();
			DetectOptionUtility.addOption(class1, options);

			final Collection os = options.getOptions();
			for (Object object : os) {
				printStream.println(object);
			}
		}
	}
}
