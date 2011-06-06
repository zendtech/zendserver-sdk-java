/*******************************************************************************
 * Copyright (c) May 18, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.sdklib.internal.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.zend.sdklib.repository.site.Application;
import org.zend.sdklib.repository.site.Site;

/**
 * Helps marshal and un-marshal using jaxb APIs
 * 
 * @author Roy, 2011
 */
public class JaxbHelper {

	private static final String ORG_ZEND_SDKLIB_REPOSITORY_SITE = "org.zend.sdklib.repository.site";

	public static Application[] unmarshal(InputStream siteStream)
			throws IOException, JAXBException {
		Source source = new StreamSource(siteStream);
		JAXBContext jc = JAXBContext
				.newInstance(ORG_ZEND_SDKLIB_REPOSITORY_SITE);
		Unmarshaller u = jc.createUnmarshaller();
		Site site = (Site) u.unmarshal(source);
		final List<Application> application = site.getApplication();
		return (Application[]) application.toArray(new Application[application
				.size()]);
	}

	public static void marshal(PrintStream printStream, final Site s)
			throws JAXBException, PropertyException {
		JAXBContext jc = JAXBContext
				.newInstance(ORG_ZEND_SDKLIB_REPOSITORY_SITE);
		Marshaller m = jc.createMarshaller();
		m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		m.marshal(s, printStream);
	}
}
