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

import org.zend.sdklib.internal.utils.JaxbHelper;
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
		JaxbHelper.marshalSite(printStream, s);

		printStream.close();
	}


	private static Site createDrupalSite() {
		ObjectFactory factory = new ObjectFactory();
		final Site s = factory.createSite();
		s.setDescription("The Zend SDK official repository");
		s.setVersion("1.0");
		final Application a = factory.createApplication();
		
		// a.setUpdateRange("[5.0, 7.0)");
		a.setUrl("/drupal/drupal-6.19.zpk");
		a.setVersion("6.19");
		a.setLabel("Drupal");
		a.setName("drupal");
		a.setCategory("cms");
		a.setProvider("Drupal");
		
		s.getApplication().add(a);

		final CategoryDef c = factory.createCategoryDef();
		c.setName("cms");
		c.setLabel("Content Managment System");
		s.getCategoryDef().add(c);

		final ProviderDef p = factory.createProviderDef();
		p.setName("Drupal");
		p.setLabel("Drupal");
		p.setUrl("http://drupal.org/");
		s.getProviderDef().add(p);
		return s;
	}
}
