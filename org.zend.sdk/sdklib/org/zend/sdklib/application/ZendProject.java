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
	protected boolean withScripts;
	protected File destination;

	/**
	 * sample applications
	 */
	public enum SampleApplications {
		HELLO_WORLD("/applications/helloworld/map"),

		ZF_QUICKSTART("/applications/zf/map");

		private final String mapPath;

		private SampleApplications(String mapPath) {
			this.mapPath = mapPath;
		}

		public String getMap() {
			return mapPath;
		}
	}

	/**
	 * Project handler allowing users to create and update projects
	 * 
	 * @param name
	 * @param withScripts
	 * @param destination
	 * @param nest
	 *            true if the project will be created under a nested directory
	 *            with the project name
	 */
	public ZendProject(String name, boolean withScripts, File destination,
			boolean nest) {
		this.name = name;
		this.withScripts = withScripts;
		this.destination = resolveDestination(nest, destination);
		if (destination == null) {
			throw new IllegalArgumentException(
					"can't handle project under given destination");
		}
	}

	/**
	 * 
	 * @param name
	 * @param withScripts
	 * @param destination
	 */
	public ZendProject(String name, boolean withScripts, File destination) {
		this(name, withScripts, destination, false);
	}

	/**
	 * Writes project to file system.
	 * 
	 * @return true on success, false otherwise.
	 */
	public boolean create(SampleApplications app) {
		ProjectResourcesWriter tw = new ProjectResourcesWriter(name,
				withScripts);

		// first create the base application
		try {
			tw.writeApplication(destination, app);
		} catch (IOException e) {
			log.error(e);
			return false;
		}

		// update with deployment resources
		return update();
	}

	public boolean update() {
		ProjectResourcesWriter tw = new ProjectResourcesWriter(name,
				withScripts);

		try {
			tw.writeDescriptor(destination);
			if (withScripts) {
				tw.writeAllScripts(destination);
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
	 * If nest flag is on, resolve the nested destination folder
	 * 
	 * @param destination2
	 * @param nest2
	 * @return
	 */
	private File resolveDestination(boolean nest, File destination) {
		if (!destination.exists()) {
			log.error("Invalid destination path: provided location does not exist.");
			return null;
		}
		// resolve project root
		File projectRoot = nest ? new File(destination, name) : destination;
		if (!projectRoot.exists()) {
			final boolean mkdir = projectRoot.mkdir();
			if (!mkdir) {
				return null;
			}
		}
		return projectRoot;
	}
}
