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
import org.zend.sdklib.repository.site.Site;

/**
 * Base abstract class for all repository that can handle resources by opening
 * streams
 * 
 * @author Roy, 2011
 */
public abstract class AbstractRepository implements IRepository {

	public static final String SITE_XML = "site.xml";

	/**
	 * An id of the repository
	 */
	private final String id;

	/**
	 * Descriptive name of this repository
	 */
	private final String name;

	/**
	 * An access to artifacts in site. Must be implemented by sub classes
	 * 
	 * @param path
	 *            to the artifact
	 */
	public abstract InputStream getArtifactStream(String path)
			throws IOException;

	public AbstractRepository(String id, String name) {
		this.id = id;
		this.name = name;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.zend.sdklib.repository.IRepository#getSite()
	 */
	@Override
	public Site getSite() throws SdkException {
		try {
			return JaxbHelper.unmarshalSite(getArtifactStream(SITE_XML));
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.zend.sdklib.repository.IRepository#getId()
	 */
	@Override
	public String getId() {
		return this.id;
	}

	public String getName() {
		return name;
	}
}
