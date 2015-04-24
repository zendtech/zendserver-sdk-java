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
import java.util.List;
import java.util.Properties;

import org.zend.sdklib.target.ITargetLoader;
import org.zend.sdklib.target.IZendTarget;

/**
 * Default persistence layer for targets
 * 
 * @author Roy, 2011
 */
public class UserBasedTargetLoader implements ITargetLoader {

	private static final String PROPERTY = "target";
	private static final String INI_EXTENSION = ".ini";
	private static final String CONF_FILENAME = "conf";
	private final File baseDir;

	public UserBasedTargetLoader() {
		this(getDefaultTargetsDirectory());
	}

	public UserBasedTargetLoader(File baseDir) {
		this.baseDir = baseDir;

		if (!baseDir.exists()) {
			throw new IllegalStateException("error finding user home directory");
		}
	}

	public static File getDefaultTargetsDirectory() {
		final String property = System.getProperty("user.home");
		final File user = new File(property);
		if (user.exists()) {
			final File targetsDir = new File(user, ".zend" + File.separator
					+ "targets");
			if (!targetsDir.isDirectory()) {
				targetsDir.mkdirs();
			}
			return targetsDir;
		} else {
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

		final File df = getDescriptorFile(target.getId());
		TargetDescriptor descriptor = loadTargetDescriptor(df);
		if (descriptor != null) {
			File conf = new File(descriptor.path, CONF_FILENAME);
			if (conf.exists() && conf.length() == 0) {
				return target;
			} else {
				throw new IllegalArgumentException("target already exists");
			}
		}

		// create descriptor
		descriptor = storeTargetDescriptor(target);
		if (null == descriptor) {
			return null;
		}

		File confFile = new File(descriptor.path, CONF_FILENAME);
		try {
			confFile.createNewFile();
			confFile.setReadable(false, false);
			confFile.setReadable(true, true);
			FileOutputStream fos = new FileOutputStream(confFile);
			target.store(fos);
			fos.close();
		} catch (IOException e1) {
			return null;
		}

		return target;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.zend.sdklib.target.ITargetLoader#remove(org.zend.sdklib.target.
	 * IZendTarget)
	 */
	@Override
	public IZendTarget remove(IZendTarget target) {
		final File df = getDescriptorFile(target.getId());
		TargetDescriptor d = loadTargetDescriptor(df);
		if (null == d) {
			throw new IllegalArgumentException("cannot find target"
					+ target.getId());
		}
		final File descriptorFile = df;

		delete(d.path);
		final boolean delete2 = delete(descriptorFile);

		if (!delete2) {
			throw new IllegalArgumentException("error deleting data");
		}

		return target;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.zend.sdklib.target.ITargetLoader#update(org.zend.sdklib.target.
	 * IZendTarget)
	 */
	@Override
	public IZendTarget update(IZendTarget target) {
		if (target == null) {
			throw new IllegalArgumentException("target is null");
		}
		if (target.isTemporary()) {
			return target;
		}
		final File df = getDescriptorFile(target.getId());
		TargetDescriptor descriptor = loadTargetDescriptor(df);
		if (descriptor == null) {
			throw new IllegalArgumentException("target does not exists");
		}

		File confFile = new File(descriptor.path, CONF_FILENAME);
		if (confFile.exists()) {
			confFile.delete();
			try {
				confFile.createNewFile();
				confFile.setReadable(false, false);
				confFile.setReadable(true, true);
			} catch (IOException e) {
			}
		}
		try {
			FileOutputStream fos = new FileOutputStream(confFile);
			target.store(fos);
			fos.close();
		} catch (IOException e1) {
			return null;
		}

		return target;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.zend.sdklib.target.ITargetLoader#loadAll()
	 */
	@Override
	public IZendTarget[] loadAll() {
		File[] targets = baseDir.listFiles(new FileFilter() {

			@Override
			public boolean accept(File file) {
				return file.getName().endsWith(INI_EXTENSION) && file.isFile();
			}

		});

		final ArrayList<IZendTarget> arrayList = new ArrayList<IZendTarget>(
				targets.length);
		targets = sortTargets(targets);
		for (File file : targets) {
			final TargetDescriptor d = loadTargetDescriptor(file);
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

		return (IZendTarget[]) arrayList.toArray(new IZendTarget[arrayList
				.size()]);
	}

	private TargetDescriptor storeTargetDescriptor(IZendTarget target) {
		try {
			final File file = getDescriptorFile(target.getId());
			if (!file.createNewFile()) {
				return null;
			}
			final TargetDescriptor targetDescriptor = new TargetDescriptor();
			targetDescriptor.target = target.getId();
			targetDescriptor.path = new File(baseDir, targetDescriptor.target);

			final Properties properties = new Properties();
			properties.put(PROPERTY, targetDescriptor.target);
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

	public TargetDescriptor loadTargetDescriptor(File file) {
		try {
			if (!file.exists()) {
				return null;
			}

			final Properties properties = new Properties();
			final FileInputStream fileInputStream = new FileInputStream(file);
			properties.load(fileInputStream);

			final TargetDescriptor targetDescriptor = new TargetDescriptor();
			targetDescriptor.target = properties.getProperty(PROPERTY);
			targetDescriptor.path = new File(properties.getProperty("path"));

			fileInputStream.close();
			return targetDescriptor.isValid() ? targetDescriptor : null;
		} catch (IOException e) {
			// can't be identified as valid target - ignore
			return null;
		}
	}

	public File getDescriptorFile(String target) {
		if (!target.endsWith(INI_EXTENSION)) {
			target = target + INI_EXTENSION;
		}
		final File file = new File(baseDir, target);
		return file;
	}

	protected boolean delete(File file) {
		if (file == null || !file.exists()) {
			return true;
		}
		if (file.isDirectory()) {
			String[] children = file.list();
			for (int i = 0; i < children.length; i++) {
				boolean result = delete(new File(file, children[i]));
				if (!result) {
					return false;
				}
			}
		}
		return file.delete();
	}

	/**
	 * Holds the name and path of a target
	 */
	public class TargetDescriptor {

		/**
		 * Name of the target
		 */
		public String target;

		/**
		 * Path of the target directory
		 */
		public File path;

		public boolean isValid() {
			return this.target != null && this.path.exists();
		}
	}
	
	private File[] sortTargets(File[] targets) {
		try {
			List<File> result = new ArrayList<File>();
			for (File file : targets) {
				int[] fileId = getId(file);
				if (result.size() == 0) {
					result.add(file);
				} else {
					int index = -1;
					for (File sorted : result) {
						int[] sortedId = getId(sorted);
						if (sortedId[0] > fileId[0]) {
							index = result.indexOf(sorted);
							break;
						} else if (sortedId[0] == fileId[0]
								&& sortedId[1] > fileId[1]) {
							index = result.indexOf(sorted);
							break;
						}
					}
					if (index == -1) {
						result.add(file);
					} else {
						result.add(index, file);
					}
				}
			}
			return result.toArray(new File[0]);
		} catch (Exception e) {
			return targets;
		}
	}

	private int[] getId(File file) {
		String name = file.getName();
		int index = name.lastIndexOf('.');
		name = name.substring(0, index);
		index = name.lastIndexOf('_');
		if (index != -1) {
			String[] segments = name.split("_");
			return new int[] { Integer.valueOf(segments[0]),
					Integer.valueOf(segments[1]) };
		}
		return new int[] { Integer.valueOf(name), 0 };
	}

}
