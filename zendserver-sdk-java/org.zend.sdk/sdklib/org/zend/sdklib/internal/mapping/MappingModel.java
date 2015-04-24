/*******************************************************************************
 * Copyright (c) Jun 27, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.sdklib.internal.mapping;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.zend.sdklib.mapping.IMapping;
import org.zend.sdklib.mapping.IMappingChangeEvent;
import org.zend.sdklib.mapping.IMappingChangeEvent.Kind;
import org.zend.sdklib.mapping.IMappingChangeListener;
import org.zend.sdklib.mapping.IMappingEntry;
import org.zend.sdklib.mapping.IMappingEntry.Type;
import org.zend.sdklib.mapping.IMappingLoader;
import org.zend.sdklib.mapping.IMappingModel;
import org.zend.sdklib.mapping.MappingModelFactory;

/**
 * Default implementation of {@link IMappingModel}.
 * 
 * @author Wojciech Galanciak, 2011
 * 
 */
public class MappingModel implements IMappingModel {

	private List<IMappingEntry> entries;
	private List<IMapping> defaultExclusion;
	private List<IMappingChangeListener> listeners;
	private IMappingLoader loader;
	private File mappingFile;
	private boolean isLoaded;

	public MappingModel(IMappingLoader loader, File mappingFile)
			throws IOException {
		this.loader = loader;
		this.mappingFile = mappingFile;
		this.listeners = new ArrayList<IMappingChangeListener>();
		this.isLoaded = mappingFile.exists();
		this.entries = mappingFile.exists() ? loader.load(new FileInputStream(mappingFile))
				: loader.load(null);
		this.defaultExclusion = loader.getDefaultExclusion();
	}

	public MappingModel(File mappingFile) throws IOException {
		this(new DefaultMappingLoader(), mappingFile);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.zend.sdklib.mapping.IMappingModel#addEntry(org.zend.sdklib.mapping
	 * .IMappingEntry)
	 */
	@Override
	public boolean addEntry(IMappingEntry toAdd) {
		if (toAdd == null || entries.contains(toAdd)) {
			return false;
		}
		entries.add(toAdd);
		modelChanged(new MappingChangeEvent(Kind.ADD_ENTRY, toAdd));
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.zend.sdklib.mapping.IMappingModel#removeEntry(java.lang.String)
	 */
	@Override
	public boolean removeEntry(String folder, Type type) {
		for (IMappingEntry entry : entries) {
			if (entry.getFolder().equals(folder) && entry.getType() == type) {
				entries.remove(entry);
				modelChanged(new MappingChangeEvent(Kind.REMOVE_ENTRY, entry));
				return true;
			}
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.zend.sdklib.mapping.IMappingModel#addMapping(java.lang.String,
	 * org.zend.sdklib.mapping.IMapping)
	 */
	@Override
	public boolean addMapping(String folder, Type type, String path, boolean isGlobal) {
		if (folder == null || path == null) {
			return false;
		}
		IMapping toAdd = new Mapping(path, isGlobal);
		for (IMappingEntry entry : entries) {
			if (entry.getFolder().equals(folder) && entry.getType() == type) {
				List<IMapping> mappings = entry.getMappings();
				for (IMapping mapping : mappings) {
					if (mapping.equals(toAdd)) {
						return false;
					}
					String exisitngPath = mapping.getPath();
					if (path.startsWith(mapping.getPath())) {
						if (exisitngPath.endsWith("/")) {
							return false;
						} else {
							String subPath = path.substring(exisitngPath
									.length());
							if (subPath.length() > 0 && subPath.startsWith("/")) {
								return false;
							}
						}
					}
				}
				if (entry.getMappings().add(toAdd)) {
					modelChanged(new MappingChangeEvent(Kind.ADD_MAPPING, entry));
					return true;
				} else {
					return false;
				}
			}
		}
		List<IMapping> mappings = new ArrayList<IMapping>();
		mappings.add(toAdd);
		return addEntry(new MappingEntry(folder, mappings, type));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.zend.sdklib.mapping.IMappingModel#removeMapping(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public boolean removeMapping(String folder, Type type, String path) {
		if (folder == null || path == null) {
			return false;
		}
		for (IMappingEntry entry : entries) {
			if (entry.getFolder().equals(folder) && entry.getType() == type) {
				List<IMapping> mappings = entry.getMappings();
				for (IMapping mapping : mappings) {
					if (mapping.getPath().equals(path)) {
						if (mappings.remove(mapping)) {
							if (mappings.size() == 0) {
								return removeEntry(folder, type);
							} else {
								modelChanged(new MappingChangeEvent(Kind.REMOVE_MAPPING, entry));
								return true;
							}
						} else {
							return false;
						}
					}
				}
			}
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.zend.sdklib.mapping.IMappingModel#modifyMapping(java.lang.String,
	 * org.zend.sdklib.mapping.IMapping)
	 */
	@Override
	public boolean modifyMapping(String folder, Type type, IMapping toModify) {
		if (folder == null || toModify == null) {
			return false;
		}
		for (IMappingEntry entry : entries) {
			if (entry.getFolder().equals(folder) && entry.getType() == type) {
				List<IMapping> mappings = entry.getMappings();
				for (IMapping mapping : mappings) {
					if (mapping.getPath().equals(toModify.getPath())) {
						mapping.setGlobal(toModify.isGlobal());
						modelChanged(new MappingChangeEvent(
								Kind.MODIFY_MAPPING, entry));
						return true;
					}
				}
			}
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.zend.sdklib.mapping.IMappingModel#getEntry(java.lang.String,
	 * org.zend.sdklib.mapping.IMappingEntry.Type)
	 */
	@Override
	public IMappingEntry getEntry(String folder, Type type) {
		for (IMappingEntry entry : entries) {
			if (entry.getFolder().equals(folder) && entry.getType() == type) {
				return entry;
			}
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.zend.sdklib.mapping.IMappingModel#getEnties()
	 */
	@Override
	public List<IMappingEntry> getEnties() {
		return entries;
	}
	
	@Override
	public List<IMappingEntry> getEnties(Type type, String tag) {
		List<IMappingEntry> result = new ArrayList<IMappingEntry>(entries.size());
		for (IMappingEntry entry : entries) {
			if (entry.getType() == type
					&& entry.getFolder().equals(tag)) {
				result.add(entry);
			}
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.zend.sdklib.mapping.IMappingModel#persist()
	 */
	@Override
	public void store() throws IOException {
		loader.store(this, new File(mappingFile.getParentFile(),
				MappingModelFactory.DEPLOYMENT_PROPERTIES));
		modelChanged(new MappingChangeEvent(Kind.STORE, null));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.zend.sdklib.mapping.IMappingModel#isExcluded(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public boolean isExcluded(String folder, String path) throws IOException {
		if (path != null && folder != null) {
			if (isInternalExcluded(path, defaultExclusion)) {
				return true;
			}
			IMappingEntry entry = getEntry(folder, Type.EXCLUDE);
			if (entry != null) {
				return isInternalExcluded(path, entry.getMappings());
			}
		}
		return false;
	}

	@Override
	public boolean isLoaded() {
		return isLoaded;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.zend.sdklib.mapping.IMappingModel#load(java.io.InputStream,
	 * java.io.File)
	 */
	@Override
	public void load(InputStream stream, File mappingFile) throws IOException {
		this.mappingFile = mappingFile;
		this.isLoaded = mappingFile != null && mappingFile.exists();
		this.entries = isLoaded ? loader.load(new FileInputStream(mappingFile))
				: loader.load(null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.zend.sdklib.mapping.IMappingModel#load(java.io.InputStream)
	 */
	@Override
	public void load(InputStream stream) throws IOException {
		this.entries = loader.load(stream);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.zend.sdklib.mapping.IMappingModel#getFolders()
	 */
	@Override
	public List<String> getFolders() {
		List<String> result = new ArrayList<String>();
		for (IMappingEntry entry : entries) {
			String folder = entry.getFolder();
			if (!result.contains(folder)) {
				result.add(entry.getFolder());
			}
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.zend.sdklib.mapping.IMappingModel#getFolder(java.lang.String)
	 */
	@Override
	public String[] getFolders(String path) throws IOException {
		List<String> result = new ArrayList<String>();
		for (IMappingEntry entry : entries) {
			if (entry.getType() == Type.INCLUDE) {
				if (isExcluded(entry.getFolder(), path)) {
					continue;
				}
				List<IMapping> mappings = entry.getMappings();
				path = new File(path).toString();
				for (IMapping include : mappings) {
					if (include.isGlobal()) {
						String fileName = path.substring(path
								.lastIndexOf(File.separator) + 1);
						if (include.getPath().equals(fileName)) {
							result.add(entry.getFolder());
							continue;
						}
					} else {
						File pathFile = new File(path);
						if (pathFile.isAbsolute()) {
							File includeFile = new File(mappingFile.getParentFile(), include.getPath());
							if (pathFile.getCanonicalPath().startsWith(
									includeFile.getCanonicalPath())) {
								result.add(entry.getFolder());
							}
						}
						if (include.getPath().equals(path)) {
							result.add(entry.getFolder());
							continue;
						} else {
							int index = path.lastIndexOf(File.separator);
							if (index > -1) {
								String fileName = path.substring(0,
										path.lastIndexOf(File.separator));
								if (include.getPath().equals(fileName)) {
									result.add(entry.getFolder());
									continue;
								}
							}
						}
					}
				}
			}
		}
		return result.toArray(new String[0]);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.zend.sdklib.mapping.IMappingModel#getPath(java.lang.String)
	 */
	@Override
	public String getPath(String name) throws IOException {
		for (IMappingEntry entry : entries) {
			if (entry.getType() == Type.INCLUDE) {
				List<IMapping> mappings = entry.getMappings();
				for (IMapping include : mappings) {
					File includeFile = new File(mappingFile.getParentFile(), include.getPath());
					if (includeFile.isDirectory()) {
						File[] members = includeFile.listFiles();
						for (File member : members) {
							String result = findFileInDirectory(member, name, entry.getFolder());
							if (result != null) {
								return result;
							}
						}
					} else {
						if (includeFile.getName().equals(name)) {
							String result = includeFile.getCanonicalPath();
							if (!isExcluded(entry.getFolder(), result)) {
								return result;
							}
						}
					}
				}
			}
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.zend.sdklib.mapping.IMappingModel#getPackagePath(java.lang.String)
	 */
	@Override
	public String getPackagePath(String folder, String path) throws IOException {
		for (IMappingEntry entry : entries) {
			if (entry.getFolder().equals(folder) && entry.getType() == Type.INCLUDE) {
				List<IMapping> mappings = entry.getMappings();
				for (IMapping include : mappings) {
					if (isExcluded(entry.getFolder(),
							new File(mappingFile.getParentFile(), path).getCanonicalPath())) {
						continue;
					}
					String includePath = new File(include.getPath()).getPath();
					path = new File(path).getPath();
					if (includePath.equals(path)) {
						return new File(entry.getFolder(), path).getPath();
					} else {
						String fileName = path;
						while (fileName != null && fileName.length() > 0) {
							int index = fileName.lastIndexOf(File.separator);
							fileName = index != -1 ? fileName.substring(0,
									index) : "";
							if (includePath.equals(fileName)) {
								return new File(entry.getFolder(), path)
										.getPath();
							}
						}
					}
				}
			}
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.zend.sdklib.mapping.IMappingModel#addMappingChangeListener(org.zend
	 * .sdklib.mapping.IMappingChangeListener)
	 */
	@Override
	public void addMappingChangeListener(IMappingChangeListener listener) {
		listeners.add(listener);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.zend.sdklib.mapping.IMappingModel#removeMappingChangeListener(org
	 * .zend.sdklib.mapping.IMappingChangeListener)
	 */
	@Override
	public void removeMappingChangeListener(IMappingChangeListener listener) {
		listeners.remove(listener);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.zend.sdklib.mapping.IMappingModel#getDefaultExclusion()
	 */
	@Override
	public List<IMapping> getDefaultExclusion() {
		return defaultExclusion;
	}

	protected void modelChanged(IMappingChangeEvent event) {
		for (IMappingChangeListener listener : listeners) {
			listener.mappingChanged(event);
		}
	}

	private boolean isInternalExcluded(String path, List<IMapping> mappings)
			throws IOException {
		for (IMapping mapping : mappings) {
			if (mapping.isGlobal()) {
				int index = path.lastIndexOf("/");
				if (index == -1) {
					index = path.lastIndexOf("\\");
				}
				String fileName = path.substring(index + 1);
				if (mapping.getPath().equals(fileName)) {
					return true;
				}
			} else {
				String fullPath = new File(mappingFile.getParentFile(), mapping.getPath())
						.getCanonicalPath();
				if (fullPath.equals(path)) {
					return true;
				}
			}
		}
		return false;
	}

	private String findFileInDirectory(File file, String name, String folder) throws IOException {
		String result = null;
		if (!isExcluded(folder, file.getCanonicalPath())) {
			if (file.isDirectory()) {
				File[] members = file.listFiles();
				for (File member : members) {
					result = findFileInDirectory(member, name, folder);
					if (result != null) {
						return result;
					}
				}
			} else {
				if (file.getName().equals(name)) {
					result = file.getPath();
				}
			}
		}
		return result;
	}

	@Override
	public IMappingLoader getLoader() {
		return loader;
	}

	public File getMappingFile() {
		return mappingFile;
	}
}
