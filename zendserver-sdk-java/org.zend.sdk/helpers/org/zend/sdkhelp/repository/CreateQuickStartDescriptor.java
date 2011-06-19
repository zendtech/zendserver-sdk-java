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

import javax.xml.bind.JAXBException;

import org.zend.sdk.test.AbstractTest;
import org.zend.sdklib.internal.project.DescriptorWriter;

/**
 * Create descriptor for quick start
 * 
 * @author Roy, 2011
 */
public class CreateQuickStartDescriptor extends AbstractTest {

	public static void main(String[] args) throws JAXBException, IOException {

		PrintStream printStream = System.out;
		if (args.length > 0) {
			printStream = new PrintStream(new File(args[0]));
		}

		DescriptorWriter w = new DescriptorWriter("quickstart", "data", "1.0.0");
		w.setDocroot("public");
		w.setZfMinMax("1.11.0", "1.13.0");
		w.setSummary("This QuickStart will introduce you to some of Zend Framework's "
				+ "most commonly used components, including Zend_Controller, Zend_Layout, "
				+ "Zend_Config, Zend_Db, Zend_Db_Table, Zend_Registry, along with a "
				+ "few view helpers.");
		w.write(printStream);
		printStream.close();
	}
}
