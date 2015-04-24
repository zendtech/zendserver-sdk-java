/*******************************************************************************
 * Copyright (c) May 16, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.sdklib.application;

import java.io.File;
import java.io.IOException;

import javax.xml.bind.JAXBException;
import javax.xml.bind.PropertyException;

import org.zend.sdklib.internal.library.AbstractChangeNotifier;
import org.zend.sdklib.internal.project.ProjectResourcesWriter;
import org.zend.sdklib.mapping.IMappingLoader;
import org.zend.webapi.core.progress.BasicStatus;
import org.zend.webapi.core.progress.StatusCode;

/**
 * Create a simple project with the tool with all required meta data and scripts
 */
public class ZendProject extends AbstractChangeNotifier {

	protected String name;
	protected File path;
	protected IMappingLoader loader;

	/**
	 * @param path
	 * @param loader
	 */
	public ZendProject(File path, IMappingLoader loader) {
		if (path == null) {
			throw new IllegalArgumentException(
					"can't handle project under given destination");
		}
		this.loader = loader;
		this.path = path;
	}

	/**
	 * @param path
	 * 
	 */
	public ZendProject(File path) {
		if (path == null) {
			throw new IllegalArgumentException(
					"can't handle project under given destination");
		}

		this.path = path;
	}

	/**
	 * Writes project to file system.
	 * 
	 * @return true on success, false otherwise.
	 */
	public boolean create(String name, TemplateApplications app,
			String generateScripts) {
		ProjectResourcesWriter tw = new ProjectResourcesWriter(name);

		// first create the base application
		try {
			tw.writeApplication(path, app);
			tw.writeDescriptor(path);
		} catch (IOException e) {
			log.error(e);
			return false;
		} catch (PropertyException e) {
			log.error(e);
			return false;
		} catch (JAXBException e) {
			log.error(e);
			return false;
		}

		// update with deployment resources
		return update(generateScripts);
	}

	public boolean update(String generateScripts, boolean updateDescriptor, boolean onlyScriptsdir) {
		ProjectResourcesWriter tw = new ProjectResourcesWriter(path, this);

		try {
			if (generateScripts != null) {
				tw.writeScriptsByName(path, generateScripts);
			}
			if (updateDescriptor) {
				tw.writeDescriptor(path);
			}
			tw.writeDeploymentProperties(path, loader, onlyScriptsdir);
		} catch (IOException e) {
			notifier.statusChanged(new BasicStatus(StatusCode.ERROR, "Application Update",
					"Error during updating application.", e));
			log.error(e);
			return false;
		} catch (PropertyException e) {
			log.error(e);
			return false;
		} catch (JAXBException e) {
			log.error(e);
			return false;
		}

		return true;
	}

	public boolean update(String generateScripts) {
		return update(generateScripts, true, false);
	}

	/**
	 * sample applications
	 */
	public enum TemplateApplications {
		ZEND("applications/zf/"),

		SIMPLE("applications/helloworld/"),

		QUICKSTART("applications/quickstart/"),
		
		ZF2("git://github.com/zendframework/ZendSkeletonApplication.git");

		private final String basePath;

		private TemplateApplications(String mapPath) {
			this.basePath = mapPath;
		}

		public String getMap() {
			return getBasePath() + "map";
		}

		public String getBasePath() {
			return basePath;
		}

		public static TemplateApplications getDefault() {
			return ZEND;
		}
	}
}
