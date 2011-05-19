/*******************************************************************************
 * Copyright (c) May 18, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.sdklib.internal.target;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import org.zend.sdklib.target.ITargetLoader;
import org.zend.sdklib.target.IZendTarget;
import org.zend.sdklib.target.ZendTarget;

public class UserBasedTargetLoader implements ITargetLoader {

	private static final String CONF_FILENAME = "conf";
	private final File baseDir;

	private static File getDefaultTargetsDirectory() {
		final String property = System.getProperty("user.home");
		final File user = new File(property);
		if (user.exists()) {
			final File targetsDir = new File(user.getAbsolutePath()
					+ File.separator + ".zend" + File.separator + "targets");
			if (!targetsDir.exists()) {
				targetsDir.mkdir();
			}
			return targetsDir;
		} else {
			throw new IllegalStateException("error finding user home directory");
		}
	}

	public UserBasedTargetLoader() {
		this(getDefaultTargetsDirectory());
	}

	public UserBasedTargetLoader(File baseDir) {
		this.baseDir = baseDir;

		if (!baseDir.exists()) {
			throw new IllegalStateException("error finding user home directory");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.zend.sdklib.target.ITargetLoader#add(org.zend.sdklib.target.IZendTarget
	 * )
	 */
	@Override
	public IZendTarget add(IZendTarget target) {
		if (target == null) {
			throw new IllegalArgumentException("target is null");
		}		
		
		File targetPath = getTargetPath(target);
		if (targetPath.exists()) {
			throw new IllegalArgumentException("target already exists");
		}

		final boolean mkdir = targetPath.mkdir();
		if (!mkdir) {
			return null;
		}

		final File confFile = createNewFileUnderPath(targetPath,
				CONF_FILENAME);
		if (null == confFile) {
			return null;
		}

		Properties properties = new Properties();
		properties.put("_id", target.getId());
		properties.put("_key", target.getKey());
		properties.put("_secretKey", target.getSecretKey());
		properties.put("_host", target.getHost().toString());
		properties.putAll(target.getProperties());
		
		FileOutputStream fos;
		try {
			fos = new FileOutputStream(confFile);
			properties.store(fos, "target properties for " + target.getId());
			fos.close();
		} catch (IOException e) {
			return null;
		}

		return target;
	}

	private File getTargetPath(IZendTarget target) {
		final File file = new File(this.baseDir.getAbsolutePath() + File.separator + target.getId());
		if (!file.exists()) {
			return null;
		}
		
		
		
		final String id = target.getId();
		File targetPath = new File(this.baseDir.getAbsolutePath()
				+ File.separator + id);
		return targetPath;
	}

	private File createNewFileUnderPath(File targetPath, String fileName) {
		final File file = new File(targetPath + File.separator + fileName);
		try {
			file.createNewFile();
		} catch (IOException e) {
			targetPath.deleteOnExit();
			return null;
		}
		return file;
	}

	@Override
	public IZendTarget remove(IZendTarget target) {
		File targetPath = getTargetPath(target);
		if (targetPath.exists()) {
			targetPath.delete();
		}
		 
		
		return null;
		
	}

	@Override
	public ZendTarget[] loadAll() {
		// TODO Auto-generated method stub
		return null;
	}
}
