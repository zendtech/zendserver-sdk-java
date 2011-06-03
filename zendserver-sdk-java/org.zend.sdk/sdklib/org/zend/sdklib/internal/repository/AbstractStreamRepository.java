/*******************************************************************************
 * Copyright (c) May 18, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.sdklib.internal.repository;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.zend.sdklib.SdkException;
import org.zend.sdklib.repository.IRepository;
import org.zend.sdklib.repository.site.Application;
import org.zend.sdklib.repository.site.Site;

/**
 * Base abstract class for all repository that can handle resources by opening
 * streams
 * 
 * @author Roy, 2011
 */
public abstract class AbstractStreamRepository implements IRepository {

	/**
	 * An access to the site descriptor. Must be implemented by sub classes
	 */
	public abstract InputStream getSiteStream() throws IOException;

	/**
	 * An access to artifacts in site. Must be implemented by sub classes
	 * 
	 * @param path
	 *            to the artifact
	 */
	public abstract InputStream getArtifactStream(String path)
			throws IOException;

	@Override
	public Application[] getAvailableApplications() throws SdkException {
		try {
			final InputStream siteStream = getSiteStream();
			Source source = new StreamSource(siteStream);
			JAXBContext jc = JAXBContext
					.newInstance("org.zend.sdklib.repository.site");
			Unmarshaller u = jc.createUnmarshaller();
			Site site = (Site) u.unmarshal(source);
			final List<Application> application = site.getApplication();
			return (Application[]) application
					.toArray(new Application[application.size()]);
		} catch (JAXBException e) {
			throw new SdkException(e);
		} catch (IOException e) {
			throw new SdkException(e);
		}
	}

	@Override
	public InputStream[] getApplication(String applicationId, String version) throws SdkException {
		// validate arguments
		if (applicationId == null || version == null) {
			throw new IllegalArgumentException("argments must be not null");
		}

		// search top level application
		final Application[] availableApplications = getAvailableApplications();
		for (Application application : availableApplications) {
			if (applicationId.equalsIgnoreCase(application.getId())
					&& version.equals(application.getVersion())) {
				final String url = application.getUrl();
				try {
					return new InputStream[] { getArtifactStream(url) };
				} catch (IOException e) {
					throw new SdkException(e);
				}
			}
		}
		
		// TODO: handle dependencies
		return null;
	}
}
