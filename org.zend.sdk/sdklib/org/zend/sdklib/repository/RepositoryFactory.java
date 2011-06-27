/*******************************************************************************
 * Copyright (c) May 18, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.sdklib.repository;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.zend.sdklib.SdkException;
import org.zend.sdklib.internal.repository.http.HttpRepository;
import org.zend.sdklib.internal.repository.local.FileBasedRepository;
import org.zend.sdklib.internal.utils.JaxbHelper;
import org.zend.sdklib.internal.utils.Md5Util;
import org.zend.sdklib.internal.utils.VersionsUtils;
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
	final public static IRepository getRepository(String url)
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

	/**
	 * Prints the new site according to information provided on the package
	 * 
	 * @param newSiteStream
	 *            print stream of the new site
	 * @param baseSiteStream
	 *            basic site, this should include all information except for the
	 *            URL, Size, version, md5
	 * @param pkgFile
	 *            the package file
	 * @param baseURL
	 *            base url
	 * @throws IOException
	 * @throws JAXBException
	 * @throws NoSuchAlgorithmException
	 */
	public static void createRepository(PrintStream newSiteStream,
			InputStream baseSiteStream, File pkgFile, String baseURL)
			throws IOException, JAXBException, NoSuchAlgorithmException {
		final Site baseSite = JaxbHelper.unmarshalSite(baseSiteStream);
		final List<Application> apps = baseSite.getApplication();
		final Application a = apps.get(0);
		a.setUrl(baseURL + "/" + pkgFile.getName());
		a.setSize(String.valueOf(pkgFile.length()));
		a.setVersion(VersionsUtils.getVersion(pkgFile.getName()));
		a.setSignature(Md5Util.getMd5(pkgFile));
		JaxbHelper.marshalSite(newSiteStream, baseSite);
	}

	/**
	 * Merge several repositories into one repository
	 * 
	 * @param description
	 * @param repositories
	 * @return the merged repository
	 * 
	 * @throws SdkException
	 */
	public static Site merge(String description, IRepository... repositories)
			throws SdkException {
		ObjectFactory f = new ObjectFactory();
		final Site s = f.createSite();
		if (description != null) {
			s.setDescription(description);
		}

		List<Application> apps = new ArrayList<Application>(1);
		List<ProviderDef> pros = new ArrayList<ProviderDef>(1);
		List<CategoryDef> cats = new ArrayList<CategoryDef>(1);

		// TODO: filtering and merging
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
