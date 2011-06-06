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

	public FileBasedRepository(String id, File basedir) {
		super(id);
		this.basedir = basedir;
	}

	@Override
	public InputStream getArtifactStream(String path) throws IOException {
		final File file = new File(basedir, path);
		return new FileInputStream(file);
	}

	@Override
	public String getId() {
		return basedir.getAbsolutePath();
	}

}
