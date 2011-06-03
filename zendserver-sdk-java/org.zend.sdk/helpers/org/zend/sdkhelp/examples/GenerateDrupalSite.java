/*******************************************************************************
 * Copyright (c) May 18, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.sdkhelp.examples;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.zend.sdklib.repository.site.Application;
import org.zend.sdklib.repository.site.CategoryDef;
import org.zend.sdklib.repository.site.ObjectFactory;
import org.zend.sdklib.repository.site.Site;

public class GenerateDrupalSite {

	
	public static void main(String[] args) throws JAXBException {
		ObjectFactory factory = new ObjectFactory();
		final Site s = factory.createSite();
		s.setDescription("This is my first drupal site");
		s.setVersion("1.0");
		final Application a = factory.createApplication();
		a.setId("my.drupal");
		a.setName("drupal");
		a.setCategory("cms");
		s.getApplication().add(a);
		
		final CategoryDef c = factory.createCategoryDef();
		c.setName("cms");
		c.setLabel("Content Managment System");
		s.getCategoryDef().add(c);
		
		JAXBContext jc = JAXBContext.newInstance( "org.zend.sdklib.repository.site" );
		Marshaller m = jc.createMarshaller();
		m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, 
		  Boolean.TRUE);
		m.marshal( s, System.out );
	}

}
