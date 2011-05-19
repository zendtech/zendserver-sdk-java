/*******************************************************************************
 * Copyright (c) May 18, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.sdklib.internal.target;

import java.io.File;
import java.io.IOException;

import org.zend.sdklib.target.ITargetLoader;
import org.zend.sdklib.target.Target;

public class UserTargetLoader implements ITargetLoader {

	private final File baseDir;

	private static File getUserHomeDirectory() {
		final String property = System.getProperty("user.home");
		final File user = new File(property);
		if (user.exists()) {
			final File targetsDir = new File(user.getAbsolutePath()
					+ File.pathSeparator + ".targets");
			if (!targetsDir.exists()) {
				targetsDir.mkdir();
			}
			return targetsDir;
		} else {
			throw new IllegalStateException("error finding user home directory");
		}
	}

	public UserTargetLoader() {
		this(getUserHomeDirectory());
	}

	public UserTargetLoader(File baseDir) {
		this.baseDir = baseDir;

		if (!baseDir.exists()) {
			throw new IllegalStateException("error finding user home directory");
		}
	}

	@Override
	public Target add(Target target) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Target remove(Target target) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Target[] loadAll() {
		// TODO Auto-generated method stub
		return null;
	}
}
