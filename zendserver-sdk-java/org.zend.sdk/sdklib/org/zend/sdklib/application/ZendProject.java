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

/**
 * Create a simple project with the tool with all required meta data and scripts
 */
public class ZendProject extends AbstractChangeNotifier {

	protected String name;
	protected File path;

	/**
	 * @param name
	 * @param scripts
	 *            list of scripts to generate (all or null are options as well)
	 * @param path
	 * @param nest
	 *            true if the project should be one level under the destination
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
	public boolean create(String name, SampleApplications app, String generateScripts) {
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

	public boolean update(String generateScripts) {
		ProjectResourcesWriter tw = new ProjectResourcesWriter(path);

		try {
			final File d = tw.writeDescriptor(path);
			if (generateScripts != null) {
				tw.writeScriptsByName(d, generateScripts);
			}
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

		return true;
	}

	/**
	 * sample applications
	 */
	public enum SampleApplications {
		HELLO_WORLD("applications/helloworld/map"),

		ZF_QUICKSTART("applications/zf/map");

		private final String mapPath;

		private SampleApplications(String mapPath) {
			this.mapPath = mapPath;
		}

		public String getMap() {
			return mapPath;
		}
	}

}
