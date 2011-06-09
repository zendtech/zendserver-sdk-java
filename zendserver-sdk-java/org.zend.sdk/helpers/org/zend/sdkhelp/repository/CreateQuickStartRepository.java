/*******************************************************************************
 * Copyright (c) May 18, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.sdkhelp.repository;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.security.NoSuchAlgorithmException;

import javax.xml.bind.JAXBException;

import org.zend.sdkcli.internal.logger.CliLogger;
import org.zend.sdklib.logger.Log;
import org.zend.sdklib.repository.RepositoryFactory;

public class CreateQuickStartRepository {

	public static void main(String[] args) throws NoSuchAlgorithmException,
			IOException, JAXBException {
		Log.getInstance().registerLogger(new CliLogger());
		// Log.getInstance().getLogger(CreateZendDescriptor.class.getName());

		if (args.length < 2) {
			throw new IllegalArgumentException("need at least 2 parameters");
		}
		PrintStream printStream = new PrintStream(new File(args[0]));

		String baseURL = null;
		if (args.length == 3) {
			baseURL = args[2];
		}

		RepositoryFactory.createRepository(printStream,
				CreateQuickStartRepository.class
						.getResourceAsStream("quickstart.xml"), new File(
						args[1]), baseURL != null ? baseURL : "");
		
		printStream.close();
	}

}
