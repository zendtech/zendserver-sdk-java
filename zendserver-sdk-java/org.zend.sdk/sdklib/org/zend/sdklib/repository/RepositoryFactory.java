/*******************************************************************************
 * Copyright (c) May 18, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.sdklib.repository;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.zend.sdklib.SdkException;
import org.zend.sdklib.internal.repository.http.HttpRepository;
import org.zend.sdklib.internal.repository.local.FileBasedRepository;
import org.zend.sdklib.repository.site.Application;
import org.zend.sdklib.repository.site.CategoryDef;
import org.zend.sdklib.repository.site.ObjectFactory;
import org.zend.sdklib.repository.site.ProviderDef;
import org.zend.sdklib.repository.site.Site;

/**
 * Creates a repository client for a given string
 * 
 * @author Roy, 2011
 */
public class RepositoryFactory {

	private final static String HTTP = "http://";
	private final static String HTTPS = "https://";
	private final static String FILE = "file:/";

	/**
	 * *
	 * 
	 * <pre>
	 * &quot;file:/&quot;
	 * </pre>
	 * 
	 * - for local repository
	 * 
	 * <pre>
	 * "http:/" or "https://"
	 * </pre>
	 * 
	 * - for remote repository
	 * 
	 * @param url
	 * @return
	 * @throws SdkException
	 */
	final public static IRepository createRepository(String url)
			throws SdkException {

		String path = path(url, false, HTTP, HTTPS);
		if (null != path) {
			try {
				return new HttpRepository(path, new URL(path));
			} catch (MalformedURLException e) {
				throw new SdkException(e);
			}
		}

		// fall back is always FILE protocol
		if (!url.startsWith("file")) {
			url += "file:/";
		}
		path = path(url, true, FILE);
		if (null != path) {
			return new FileBasedRepository(url, new File(path));
		}
		return null;
	}

	final public Site merge(String description, IRepository ... repositories) throws SdkException {
		ObjectFactory f = new ObjectFactory();
		final Site s = f.createSite();
		if (description != null) {
			s.setDescription(description);
		}
		
		List<Application> apps = new ArrayList<Application>(1);
		List<ProviderDef> pros = new ArrayList<ProviderDef>(1);
		List<CategoryDef> cats = new ArrayList<CategoryDef>(1);
		for (IRepository r : repositories) {
			final Site sr = r.getSite();
			apps.addAll(sr.getApplication());
			pros.addAll(sr.getProviderDef());
			cats.addAll(sr.getCategoryDef());
		}
		
		return s;
	}
	
	
	/**
	 * finds the path according to the given URL hints. Trims the hints or not
	 * according to the trim parameter
	 * 
	 * @param url
	 * @param trim
	 * @param startsWith
	 * @return
	 */
	private static String path(String url, boolean trim, String... startsWith) {
		for (String s : startsWith) {
			if (url.length() > s.length()
					&& s.equalsIgnoreCase(url.substring(0, s.length()))) {
				return trim ? url.substring(s.length()) : url;
			}
		}
		return null;
	}
}
