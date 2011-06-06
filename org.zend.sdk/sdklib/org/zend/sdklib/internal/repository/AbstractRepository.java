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

import javax.xml.bind.JAXBException;

import org.zend.sdklib.SdkException;
import org.zend.sdklib.internal.utils.JaxbHelper;
import org.zend.sdklib.repository.IRepository;
import org.zend.sdklib.repository.site.Application;

/**
 * Base abstract class for all repository that can handle resources by opening
 * streams
 * 
 * @author Roy, 2011
 */
public abstract class AbstractRepository implements IRepository {

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
	public Application[] listApplications() throws SdkException {
		try {
			return JaxbHelper.unmarshal(getSiteStream());
		} catch (JAXBException e) {
			throw new SdkException(e);
		} catch (IOException e) {
			throw new SdkException(e);
		}
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.zend.sdklib.repository.IRepository#getPackage(org.zend.sdklib.repository
	 * .site.Application)
	 */
	@Override
	public InputStream getPackage(Application application) throws IOException {
		return getArtifactStream(application.getUrl());
	}

}
