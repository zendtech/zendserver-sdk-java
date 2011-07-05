/*******************************************************************************
 * Copyright (c) May 18, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.sdklib.repository;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import javax.xml.bind.JAXBException;
import javax.xml.bind.PropertyException;

import org.zend.sdklib.SdkException;
import org.zend.sdklib.internal.repository.AbstractRepository;
import org.zend.sdklib.internal.repository.http.HttpRepository;
import org.zend.sdklib.internal.repository.local.FileBasedRepository;
import org.zend.sdklib.internal.utils.JaxbHelper;
import org.zend.sdklib.internal.utils.Md5Util;
import org.zend.sdklib.internal.utils.VersionsUtils;
import org.zend.sdklib.repository.site.Application;
import org.zend.sdklib.repository.site.ObjectFactory;
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
	 * @param name 
	 * @return
	 * @throws SdkException
	 */
	final public static IRepository createRepository(String url, String name)
			throws SdkException {

		IRepository r = null;
		
		String path = path(url, false, HTTP, HTTPS);
		if (null != path) {
			try {
				r = new HttpRepository(path, name, new URL(path));
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
			r = new FileBasedRepository(url, name, new File(path));
		}
		
		return r;
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
	 * @param repository
	 * @param site
	 * @throws SdkException
	 * @throws PropertyException
	 * @throws JAXBException
	 * @throws FileNotFoundException 
	 */
	public static void merge(FileBasedRepository repository, Site site)
			throws SdkException {
		
		ObjectFactory f = new ObjectFactory();
		final Site s = f.createSite();
		
		final Site sr = repository.getSite();

		s.setDescription(sr.getDescription());
		s.getApplication().addAll(sr.getApplication());
		s.getProviderDef().addAll(sr.getProviderDef());
		s.getCategoryDef().addAll(sr.getCategoryDef());
		
		s.getApplication().addAll(site.getApplication());
		s.getProviderDef().addAll(site.getProviderDef());
		s.getCategoryDef().addAll(site.getCategoryDef());
		
		// TODO: filter out duplications
		
		final File repoFile = new File(repository.getBasedir(), AbstractRepository.SITE_XML);
		PrintStream os;
		try {
			os = new PrintStream (repoFile);
			JaxbHelper.marshalSite(os, s);
		} catch (Exception e) {
			throw new SdkException(e);
		}
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
