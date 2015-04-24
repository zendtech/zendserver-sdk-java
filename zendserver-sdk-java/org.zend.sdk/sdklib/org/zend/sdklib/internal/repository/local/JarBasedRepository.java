/*******************************************************************************
 * Copyright (c) May 18, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.sdklib.internal.repository.local;

import java.io.IOException;
import java.io.InputStream;

import org.zend.sdklib.internal.repository.AbstractRepository;

/**
 * an implementation for local Application Repository, as described in
 * http://code.google.com/p/zend-sdk/wiki/RepositorySpec
 * 
 * Mainly created for testing, not recommended for usage
 * 
 * @author Roy, 2011
 */
public class JarBasedRepository extends AbstractRepository {

	private final Class class1;

	/**
	 * Base dir for the repository
	 */
	public JarBasedRepository(String id, String name, Class class1) {
		super(id, name);
		this.class1 = class1;
	}

	/**
	 * Base dir for the repository
	 */
	public JarBasedRepository(String id, Class class1) {
		super(id, "default-jar");
		this.class1 = class1;
	}

	@Override
	public InputStream getArtifactStream(String path) throws IOException {
		return class1.getResourceAsStream(path);
	}
	
	@Override
	public boolean isAccessible() {
		return class1 != null;
	}
	
}
