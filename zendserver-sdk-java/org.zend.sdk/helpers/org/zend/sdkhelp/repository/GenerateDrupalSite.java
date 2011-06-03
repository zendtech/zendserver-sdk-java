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

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;

import org.zend.sdklib.repository.site.Application;
import org.zend.sdklib.repository.site.CategoryDef;
import org.zend.sdklib.repository.site.ObjectFactory;
import org.zend.sdklib.repository.site.ProviderDef;
import org.zend.sdklib.repository.site.Site;

/**
 * Generates a sample repository site descriptor
 * 
 * @author Roy, 2011
 */
public class GenerateDrupalSite {

	public static void main(String[] args) throws JAXBException,
			FileNotFoundException {
		PrintStream printStream = System.out;
		if (args.length > 0) {
			printStream = new PrintStream(new File(args[0]));
		}

		final Site s = createDrupalSite();
		printSite(printStream, s);
	}

	private static void printSite(PrintStream printStream, final Site s)
			throws JAXBException, PropertyException {
		JAXBContext jc = JAXBContext
				.newInstance("org.zend.sdklib.repository.site");
		Marshaller m = jc.createMarshaller();
		m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		m.marshal(s, printStream);
	}

	private static Site createDrupalSite() {
		ObjectFactory factory = new ObjectFactory();
		final Site s = factory.createSite();
		s.setDescription("This is my first drupal site");
		s.setVersion("1.0");
		final Application a = factory.createApplication();
		a.setId("my.drupal");
		a.setName("drupal");
		a.setCategory("cms");
		a.setProvider("me");
		s.getApplication().add(a);

		final CategoryDef c = factory.createCategoryDef();
		c.setName("cms");
		c.setLabel("Content Managment System");
		s.getCategoryDef().add(c);

		final ProviderDef p = factory.createProviderDef();
		p.setName("me");
		p.setLabel("I am \"Me\"");
		s.getProviderDef().add(p);
		return s;
	}
}
