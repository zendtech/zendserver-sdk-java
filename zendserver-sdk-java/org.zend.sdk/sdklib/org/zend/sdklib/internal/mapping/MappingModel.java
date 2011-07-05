/*******************************************************************************
 * Copyright (c) Jun 27, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.sdklib.internal.mapping;

import java.io.File;
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
	private File container;

	public MappingModel(IMappingLoader loader, InputStream input, File container)
			throws IOException {
		this.loader = loader;
		this.container = container;
		this.listeners = new ArrayList<IMappingChangeListener>();
		this.entries = loader.load(input);
		this.defaultExclusion = loader.getDefaultExclusion();
	}

	public MappingModel(InputStream input, File container) throws IOException {
		this(new DefaultMappingLoader(), input, container);
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
	public boolean addMapping(String folder, Type type, String path,
			boolean isGlobal, boolean isContent) {
		if (folder == null || path == null) {
			return false;
		}
		IMapping toAdd = new Mapping(path, isContent, isGlobal);
		for (IMappingEntry entry : entries) {
			if (entry.getFolder().equals(folder) && entry.getType() == type) {
				List<IMapping> mappings = entry.getMappings();
				for (IMapping mapping : mappings) {
					if (mapping.equals(toAdd)) {
						return false;
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
						mapping.setContent(toModify.isContent());
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.zend.sdklib.mapping.IMappingModel#persist()
	 */
	@Override
	public void store() throws IOException {
		loader.store(this, new File(container,
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
			for (IMapping mapping : defaultExclusion) {
				System.out.println(mapping.getPath() + " is global: " + mapping.isGlobal());
			}
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
	public String getFolder(String path) throws IOException {
		for (IMappingEntry entry : entries) {
			if (entry.getType() == Type.INCLUDE) {
				List<IMapping> mappings = entry.getMappings();
				for (IMapping include : mappings) {
					if (include.isGlobal()) {
						String fileName = path.substring(path
								.lastIndexOf(File.separator) + 1);
						if (include.getPath().equals(fileName)) {
							return entry.getFolder();
						}
					} else if (include.isContent()) {
						String fullPath = new File(container, include.getPath())
								.getCanonicalPath();
						path = new File(container, path).getCanonicalPath();
						if (path.startsWith(fullPath)) {
							return entry.getFolder();
						}
					} else {
						if (include.getPath().equals(path)) {
							return entry.getFolder();
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
				String fileName = path.substring(path
						.lastIndexOf(File.separator) + 1);
				System.out.println("filename: " + fileName);
				if (mapping.getPath().equals(fileName)) {
					return true;
				}
			} else {
				String fullPath = new File(container, mapping.getPath())
						.getCanonicalPath();
				if (fullPath.equals(path)) {
					return true;
				}
			}
		}
		return false;
	}

}
