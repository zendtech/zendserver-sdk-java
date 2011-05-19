/*******************************************************************************
 * Copyright (c) May 18, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.sdklib.internal.target;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Properties;

import org.zend.sdklib.target.ITargetLoader;
import org.zend.sdklib.target.IZendTarget;
import org.zend.sdklib.target.ZendTarget;

/**
 * Default persistence layer for targets
 * 
 * @author Roy, 2011
 */
public class UserBasedTargetLoader implements ITargetLoader {

	private static final String INI_EXTENSION = ".ini";
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

		TargetDescriptor descriptor = loadTargetDescriptor(target.getId());
		if (descriptor != null) {
			throw new IllegalArgumentException("target already exists");
		}

		// create descriptor
		descriptor = storeTargetDescriptor(target);
		if (null == descriptor) {
			return null;
		}

		File confFile = new File(descriptor.path, CONF_FILENAME);
		try {
			confFile.createNewFile();
			FileOutputStream fos = new FileOutputStream(confFile);
			target.store(fos);
			fos.close();
		} catch (IOException e1) {
			return null;
		}

		return target;
	}

	private TargetDescriptor storeTargetDescriptor(IZendTarget target) {
		try {
			final File file = getDescriptorFile(target.getId());
			if (!file.createNewFile()) {
				return null;
			}
			final TargetDescriptor targetDescriptor = new TargetDescriptor();
			targetDescriptor.target = target.getId();
			targetDescriptor.path = new File(this.baseDir.getAbsolutePath(),
					targetDescriptor.target);

			final Properties properties = new Properties();
			properties.put("target", targetDescriptor.target);
			properties.put("path", targetDescriptor.path.getAbsolutePath());

			final FileOutputStream fileOutputStream = new FileOutputStream(file);
			properties.store(fileOutputStream, "descriptor for target "
					+ target.getId());

			fileOutputStream.close();
			targetDescriptor.path.mkdir();

			return targetDescriptor.isValid() ? targetDescriptor : null;
		} catch (IOException e) {
			// can't be identified as valid target - ignore
			return null;
		}
	}

	private TargetDescriptor loadTargetDescriptor(String target) {
		try {
			final File file = getDescriptorFile(target);
			if (!file.exists()) {
				return null;
			}

			final Properties properties = new Properties();
			final FileInputStream fileInputStream = new FileInputStream(file);
			properties.load(fileInputStream);

			final TargetDescriptor targetDescriptor = new TargetDescriptor();
			targetDescriptor.target = properties.getProperty("target");
			targetDescriptor.path = new File(properties.getProperty("path"));

			fileInputStream.close();
			return targetDescriptor.isValid() ? targetDescriptor : null;
		} catch (IOException e) {
			// can't be identified as valid target - ignore
			return null;
		}
	}

	private File getDescriptorFile(String target) {
		if (!target.endsWith(INI_EXTENSION)) {
			target = target + INI_EXTENSION;
		}
		final File file = new File(this.baseDir, target);
		return file;
	}

	private File getTargetPath(IZendTarget target) {
		final File file = new File(this.baseDir, target.getId());
		if (!file.exists()) {
			return null;
		}

		final String id = target.getId();
		File targetPath = new File(this.baseDir, id);
		return targetPath;
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
	public IZendTarget[] loadAll() {
		final File[] targets = baseDir.listFiles(new FileFilter() {

			@Override
			public boolean accept(File file) {
				return file.getName().endsWith(INI_EXTENSION) && file.isFile();
			}

		});

		final ArrayList<IZendTarget> arrayList = new ArrayList<IZendTarget>(
				targets.length);
		for (File file : targets) {
			final TargetDescriptor d = loadTargetDescriptor(file.getName());
			if (d.isValid()) {
				File confFile = new File(d.path, CONF_FILENAME);
				try {
					InputStream is = new FileInputStream(confFile);
					final ZendTarget target = new ZendTarget();
					target.load(is);
					arrayList.add(target);
				} catch (IOException e1) {
					// skip target loading
				}
			}
		}

		return (IZendTarget[]) arrayList.toArray(new IZendTarget[arrayList.size()]);
	}

	/**
	 * Holds the name and path of a target
	 */
	public class TargetDescriptor {

		public String target;
		public File path;

		public boolean isValid() {
			return this.target != null && this.path.exists();
		}
	}

}
