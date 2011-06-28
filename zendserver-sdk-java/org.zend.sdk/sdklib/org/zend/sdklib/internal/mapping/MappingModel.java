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
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.zend.sdklib.mapping.IMapping;
import org.zend.sdklib.mapping.IMappingChangeEvent;
import org.zend.sdklib.mapping.IMappingChangeEvent.Kind;
import org.zend.sdklib.mapping.IMappingChangeListener;
import org.zend.sdklib.mapping.IMappingLoader;
import org.zend.sdklib.mapping.IMappingModel;
import org.zend.sdklib.mapping.IResourceMapping;
import org.zend.sdklib.mapping.MappingModelFactory;

/**
 * Default implementation of {@link IMappingModel}.
 * 
 * @author Wojciech Galanciak, 2011
 * 
 */
public class MappingModel implements IMappingModel {

	private IResourceMapping resourceMapping;
	private List<IMappingChangeListener> listeners;
	private IMappingLoader loader;
	private File container;

	public MappingModel(IMappingLoader loader, InputStream input, File container)
			throws IOException {
		this.loader = loader;
		this.container = container;
		this.listeners = new ArrayList<IMappingChangeListener>();
		this.resourceMapping = loader.load(input);

	}

	public MappingModel(InputStream input, File container) throws IOException {
		this(new DefaultMappingLoader(), input, container);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.zend.sdklib.mapping.IMappingModel#getResourceMapping()
	 */
	@Override
	public IResourceMapping getResourceMapping() {
		return resourceMapping;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.zend.sdklib.mapping.IMappingModel#addInclude(java.lang.String,
	 * org.zend.sdklib.mapping.IMapping)
	 */
	@Override
	public boolean addInclude(String folder, IMapping mapping) {
		Set<IMapping> includes = getInclusion(folder);
		if (folder != null && mapping != null) {
			if (!includes.isEmpty()) {
				includes.add(mapping);
			} else {
				Set<IMapping> value = new LinkedHashSet<IMapping>();
				value.add(mapping);
				Map<String, Set<IMapping>> inclusion = resourceMapping
						.getInclusion();
				inclusion.put(folder, value);
			}
			modelChanged(new MappingChangeEvent(Kind.ADD, mapping, folder));
			return true;
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.zend.sdklib.mapping.IMappingModel#addExclude(java.lang.String,
	 * org.zend.sdklib.mapping.IMapping)
	 */
	@Override
	public boolean addExclude(String folder, IMapping mapping) {
		Set<IMapping> excludes = getExclusion(folder);
		if (folder != null && mapping != null) {
			if (!excludes.isEmpty()) {
				excludes.add(mapping);
			} else {
				Set<IMapping> value = new LinkedHashSet<IMapping>();
				value.add(mapping);
				Map<String, Set<IMapping>> exclusion = resourceMapping
						.getExclusion();
				exclusion.put(folder, value);
			}
			modelChanged(new MappingChangeEvent(Kind.ADD, mapping, folder));
			return true;
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.zend.sdklib.mapping.IMappingModel#removeInclude(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public boolean removeInclude(String folder, String path) {
		Set<IMapping> includes = getInclusion(folder);
		if (includes != null) {
			for (IMapping include : includes) {
				if (include.getPath().equals(path)) {
					includes.remove(include);
					modelChanged(new MappingChangeEvent(Kind.REMOVE, include,
							folder));
					return true;
				}
			}
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.zend.sdklib.mapping.IMappingModel#removeExclude(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public boolean removeExclude(String folder, String path) {
		Set<IMapping> excludes = getExclusion(folder);
		if (excludes != null) {
			for (IMapping exclude : excludes) {
				if (exclude.getPath().equals(path)) {
					excludes.remove(exclude);
					modelChanged(new MappingChangeEvent(Kind.REMOVE, exclude,
							folder));
					return true;
				}
			}
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.zend.sdklib.mapping.IMappingModel#removeInclude(java.lang.String)
	 */
	@Override
	public boolean removeInclude(String folder) {
		Map<String, Set<IMapping>> inclusion = resourceMapping.getInclusion();
		if (inclusion.remove(folder) != null) {
			return true;
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.zend.sdklib.mapping.IMappingModel#removeExclude(java.lang.String)
	 */
	@Override
	public boolean removeExclude(String folder) {
		Map<String, Set<IMapping>> exclusion = resourceMapping.getExclusion();
		if (exclusion.remove(folder) != null) {
			return true;
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.zend.sdklib.mapping.IMappingModel#modifyInclude(java.lang.String,
	 * org.zend.sdklib.mapping.IMapping)
	 */
	@Override
	public boolean modifyInclude(String folder, IMapping mapping) {
		Set<IMapping> includes = getInclusion(folder);
		if (mapping != null && includes != null) {
			for (IMapping include : includes) {
				if (include.getPath().equals(mapping.getPath())) {
					include.setContent(mapping.isContent());
					include.setGlobal(mapping.isGlobal());
					modelChanged(new MappingChangeEvent(Kind.MODIFY, mapping,
							folder));
				}
			}
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.zend.sdklib.mapping.IMappingModel#modifyExclude(java.lang.String,
	 * org.zend.sdklib.mapping.IMapping)
	 */
	@Override
	public boolean modifyExclude(String folder, IMapping mapping) {
		Set<IMapping> excludes = getExclusion(folder);
		if (mapping != null && excludes != null) {
			for (IMapping exclude : excludes) {
				if (exclude.getPath().equals(mapping.getPath())) {
					exclude.setContent(mapping.isContent());
					exclude.setGlobal(mapping.isGlobal());
					modelChanged(new MappingChangeEvent(Kind.MODIFY, mapping,
							folder));
				}
			}
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.zend.sdklib.mapping.IMappingModel#getInclusion(java.lang.String)
	 */
	@Override
	public Set<IMapping> getInclusion(String folder) {
		if (folder != null) {
			Map<String, Set<IMapping>> rules = resourceMapping.getInclusion();
			Set<IMapping> mappings = rules.get(folder);
			if (mappings != null) {
				return mappings;
			}
		}
		return new LinkedHashSet<IMapping>();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.zend.sdklib.mapping.IMappingModel#getExclusion(java.lang.String)
	 */
	@Override
	public Set<IMapping> getExclusion(String folder) {
		if (folder != null) {
			Map<String, Set<IMapping>> rules = resourceMapping.getExclusion();
			Set<IMapping> mappings = rules.get(folder);
			if (mappings != null) {
				return mappings;
			}
		}
		return new LinkedHashSet<IMapping>();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.zend.sdklib.mapping.IMappingModel#persist()
	 */
	@Override
	public void store() throws IOException {
		OutputStream stream = loader.store(resourceMapping, new File(container,
				MappingModelFactory.DEPLOYMENT_PROPERTIES));
		stream.close();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.zend.sdklib.mapping.IMappingModel#isExcluded(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public boolean isExcluded(String folder, String path) throws IOException {
		if (isInternalExcluded(path, resourceMapping.getDefaultExclusion())) {
			return true;
		}
		return isInternalExcluded(path, getExclusion(folder));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.zend.sdklib.mapping.IMappingModel#getFolders()
	 */
	@Override
	public Set<String> getFolders() {
		return resourceMapping.getInclusion().keySet();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.zend.sdklib.mapping.IMappingModel#getFolder(java.lang.String)
	 */
	@Override
	public String getFolder(String path) throws IOException {
		Set<String> folders = getFolders();
		for (String folder : folders) {
			Set<IMapping> includes = getInclusion(folder);
			for (IMapping include : includes) {
				if (include.isGlobal()) {
					String fileName = path.substring(path
							.lastIndexOf(File.separator) + 1);
					if (include.getPath().equals(fileName)) {
						return folder;
					}
				} else if (include.isContent()) {
					String fullPath = new File(container, include.getPath())
							.getCanonicalPath();
					if (path.startsWith(fullPath)) {
						return folder;
					}
				} else {
					if (include.getPath().equals(path)) {
						return folder;
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

	protected void modelChanged(IMappingChangeEvent event) {
		for (IMappingChangeListener listener : listeners) {
			listener.mappingChanged(event);
		}
	}

	private boolean isInternalExcluded(String path, Set<IMapping> excludes)
			throws IOException {
		for (IMapping exclude : excludes) {
			if (exclude.isGlobal()) {
				String fileName = path.substring(path
						.lastIndexOf(File.separator) + 1);
				if (exclude.getPath().equals(fileName)) {
					return true;
				}
			} else {
				String fullPath = new File(container, exclude.getPath())
						.getCanonicalPath();
				if (fullPath.equals(path)) {
					return true;
				}
			}
		}
		return false;
	}

}
