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
import java.io.PrintStream;

import javax.xml.bind.JAXBException;
import javax.xml.bind.PropertyException;

import org.zend.sdklib.SdkException;
import org.zend.sdklib.internal.utils.JaxbHelper;
import org.zend.sdklib.repository.IRepository;
import org.zend.sdklib.repository.site.Application;
import org.zend.sdklib.repository.site.ObjectFactory;
import org.zend.sdklib.repository.site.Site;

public abstract class CachedRepository implements IRepository {

	private final IRepository proxy;

	/**
	 * gets the input stream for reading the cache from
	 * 
	 * @return
	 */
	protected abstract InputStream getCacheResource();

	/**
	 * Installs a new cache for a repository
	 * 
	 * @param toCache
	 * @param ps
	 * @throws SdkException
	 */
	public CachedRepository(IRepository toCache, PrintStream ps)
			throws SdkException {
		this.proxy = toCache;

		final Site site = new ObjectFactory().createSite();
		final Application[] listApplications = this.proxy.listApplications();
		for (Application application : listApplications) {
			site.getApplication().add(application);
		}
		try {
			JaxbHelper.marshal(ps, site);
		} catch (PropertyException e) {
			throw new SdkException(e);
		} catch (JAXBException e) {
			throw new SdkException(e);
		}
	}

	@Override
	public Application[] listApplications() throws SdkException {
		final InputStream cacheResource = getCacheResource();
		Application[] unmarshal = null;
		try {
			unmarshal = JaxbHelper.unmarshal(cacheResource);
		} catch (IOException e) {
			// ignore, shoulod have been found earlier
		} catch (JAXBException e) {
			// ignore, shoulod have been found earlier
		}

		return unmarshal;
	}

	@Override
	public InputStream getPackage(Application application) throws IOException {
		return proxy.getPackage(application);
	}

}
