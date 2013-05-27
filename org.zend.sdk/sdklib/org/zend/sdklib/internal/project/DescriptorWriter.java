/*******************************************************************************
 * Copyright (c) May 18, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.sdklib.internal.project;

import java.io.IOException;
import java.io.OutputStream;

import javax.xml.bind.JAXBException;
import javax.xml.bind.PropertyException;

import org.zend.sdklib.descriptor.pkg.Dependencies;
import org.zend.sdklib.descriptor.pkg.Dependencies.Required;
import org.zend.sdklib.descriptor.pkg.ObjectFactory;
import org.zend.sdklib.descriptor.pkg.Package;
import org.zend.sdklib.descriptor.pkg.Version;
import org.zend.sdklib.descriptor.pkg.Zendframework;
import org.zend.sdklib.internal.utils.JaxbHelper;

/**
 * Writes a simple descriptor to a given printStream
 * 
 * @author Roy, 2011
 * 
 */
public class DescriptorWriter {

	private String appName;
	private String appDir = "data";
	private String descriptorVersion = "1.0";
	private String appVersion = "1.0.0";
	private String type;
	private String docroot;
	private String summary;
	private String zfMin;
	private String zfMax;
	private String scripts;

	/**
	 * @param appName
	 * @param appDir
	 * @param summary
	 * @param appVersion
	 */
	public DescriptorWriter(String appName, String appDir, String scripts,
			String appVersion) {
		super();
		this.appName = appName;
		this.appDir = appDir;
		this.scripts = scripts;
		this.appVersion = appVersion;
	}

	/**
	 * @param docroot
	 *            the docroot to set
	 */
	public final void setDocroot(String docroot) {
		this.docroot = docroot;
	}

	/**
	 * @param descriptorVersion
	 *            the descriptorVersion to set
	 */
	public final void setDescriptorVersion(String descriptorVersion) {
		this.descriptorVersion = descriptorVersion;
	}

	/**
	 * @param summary
	 *            the summary to set
	 */
	public final void setSummary(String summary) {
		this.summary = summary;
	}

	/**
	 * @param appVersion
	 *            the appVersion to set
	 */
	public final void setAppVersion(String appVersion) {
		this.appVersion = appVersion;
	}

	/**
	 * @param zfMin
	 *            the zfMin to set
	 */
	public final void setZfMinMax(String zfMin, String zfMax) {
		this.zfMin = zfMin;
		this.zfMax = zfMax;
	}

	/**
	 * @param scripts
	 *            the script dir to use
	 */
	public void setScripts(String scripts) {
		this.scripts = scripts;
	}

	/**
	 * @param outStream
	 * @throws PropertyException
	 * @throws JAXBException
	 * @throws IOException
	 */
	public void write(OutputStream outStream) throws PropertyException,
			JAXBException, IOException {
		final Package p = createZendPackae();
		JaxbHelper.marshalPackage(outStream, p);
	}

	private Package createZendPackae() {

		ObjectFactory f = new ObjectFactory();
		final Package p = f.createPackage();
		p.setName(appName);
		p.setType(type);
		p.setAppdir(appDir);
		if (docroot != null)
			p.setDocroot(docroot);

		p.setDescriptorVersion(descriptorVersion);

		if (scripts != null)
			p.setScriptsdir(scripts);

		if (summary != null)
			p.setSummary(summary);

		final Version v = f.createVersion();
		v.setRelease(appVersion);
		p.setVersion(v);

		if (zfMax != null) {
			final Zendframework z = f.createZendframework();
			z.setMin(zfMin);
			z.setMax(zfMax);
			final Required r = f.createDependenciesRequired();
			r.getZendframework().add(z);
			final Dependencies d = f.createDependencies();
			d.setRequired(r);
			p.setDependencies(d);
		}

		return p;
	}

}
