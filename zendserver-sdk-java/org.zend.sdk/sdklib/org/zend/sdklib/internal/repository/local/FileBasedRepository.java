/*******************************************************************************
 * Copyright (c) May 18, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.sdklib.internal.repository.local;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.zend.sdklib.internal.repository.AbstractRepository;

/**
 * an implementation for local Application Repository, as described in
 * http://code.google.com/p/zend-sdk/wiki/RepositorySpec
 * 
 * Structure of this repository:
 * 
 * <pre>
 *  	<root>
 *  	   site.xml - repository descriptor
 *  	   /applications
 *              /<application_id>_<application_version>
 *                 <application_id>_<application_version>.zpk
 *              /<application_id>_<application_version>
 *                 <application_id>_<application_version>.zpk
 *              /<application_id>_<application_version>
 *                 <application_id>_<application_version>.zpk
 *  	   /resources
 *              myicon.png
 *              mytext.txt
 * 
 * </pre>
 * 
 * @author Roy, 2011
 */
public class FileBasedRepository extends AbstractRepository {

	/**
	 * Base dir for the repository
	 */
	private final File basedir;

	public FileBasedRepository(String id, String name, File basedir) {
		super(id, name);
		this.basedir = basedir;
	}

	public FileBasedRepository(String id, File basedir) {
		this(id, "default-file", basedir);
	}

	@Override
	public InputStream getArtifactStream(String path) throws IOException {
		if (!isAccessible()) {
			return null;
		}

		final File file = new File(getBasedir(), path);
		if (!file.isFile()) {
			throw new IllegalArgumentException(
					"path is not a valid product in site: " + path);
		}
		return new FileInputStream(file);
	}

	@Override
	public boolean isAccessible() {
		return getBasedir() != null && getBasedir().isDirectory();
	}

	/**
	 * @return base dir of this local repository
	 */
	public File getBasedir() {
		return basedir;
	}
}
