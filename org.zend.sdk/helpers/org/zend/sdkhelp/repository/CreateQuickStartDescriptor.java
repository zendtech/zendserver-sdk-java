/*******************************************************************************
 * Copyright (c) May 18, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.sdkhelp.repository;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;

import javax.xml.bind.JAXBException;

import org.zend.sdkcli.internal.logger.CliLogger;
import org.zend.sdklib.descriptor.pkg.Dependencies;
import org.zend.sdklib.descriptor.pkg.Dependencies.Required;
import org.zend.sdklib.descriptor.pkg.ObjectFactory;
import org.zend.sdklib.descriptor.pkg.Package;
import org.zend.sdklib.descriptor.pkg.Version;
import org.zend.sdklib.descriptor.pkg.Zendframework;
import org.zend.sdklib.internal.utils.JaxbHelper;
import org.zend.sdklib.logger.Log;

/**
 * Create descriptor for quick start
 * 
 * @author Roy, 2011
 */
public class CreateQuickStartDescriptor {

	public static void main(String[] args) throws JAXBException,
			FileNotFoundException {
		
		Log.getInstance().registerLogger(new CliLogger());
		// Log.getInstance().getLogger(CreateZendDescriptor.class.getName());
		
		PrintStream printStream = System.out;
		if (args.length > 0) {
			printStream = new PrintStream(new File(args[0]));
		}

		final Package p = createZendPackae();
		JaxbHelper.marshalPackage(printStream, p);
		printStream.close();
	}

	private static Package createZendPackae() {

		ObjectFactory f = new ObjectFactory();
		final Package p = f.createPackage();
		p.setName("quickstart");
		p.setAppdir("data");
		p.setDocroot("public");
		p.setDescriptorVersion("1.0");
		
		p.setSummary("This QuickStart will introduce you to some of Zend Framework's "
				+ "most commonly used components, including Zend_Controller, Zend_Layout, "
				+ "Zend_Config, Zend_Db, Zend_Db_Table, Zend_Registry, along with a "
				+ "few view helpers.");
		final Version v = f.createVersion();
		v.setRelease("1.0");
		p.setVersion(v);

		final Zendframework z = f.createZendframework();
		z.setMin("1.11.0");
		z.setMax("1.13.0");

		final Required r = f.createDependenciesRequired();
		r.getZendframework().add(z);

		final Dependencies d = f.createDependencies();
		d.setRequired(r);

		p.setDependencies(d);

		return p;
	}
}
