/*******************************************************************************
 * Copyright (c) Jun 27, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.sdklib.mapping;

import java.io.IOException;
import java.util.Set;

/**
 * Interface for a mapping model. It provides all necessary operations for
 * getting information about a resource mapping. It also allows for a mapping
 * model modifications.
 * 
 * @author Wojciech Galanciak, 2011
 * 
 */
public interface IMappingModel {

	/**
	 * @return resource mapping for the model
	 */
	IResourceMapping getResourceMapping();

	/**
	 * Adds new inclusion mapping for specified folder.
	 * 
	 * @param folder
	 *            for which mapping should be added
	 * @param mapping
	 *            new mapping definition
	 * @return
	 */
	boolean addInclude(String folder, IMapping mapping);

	/**
	 * Adds new exclusion mapping for specified folder.
	 * 
	 * @param folder
	 *            for which mapping should be added
	 * @param mapping
	 *            new mapping definition
	 * @return
	 */
	boolean addExclude(String folder, IMapping mapping);

	/**
	 * Removes specified inclusion mapping for the folder.
	 * 
	 * @param folder
	 *            for which mapping should be removed
	 * @param path
	 *            of a mapping which should be removed
	 * @return
	 */
	boolean removeInclude(String folder, String path);

	/**
	 * Removes specified exclusion mapping for the folder.
	 * 
	 * @param folder
	 *            for which mapping should be removed
	 * @param path
	 *            of a mapping which should be removed
	 * @return
	 */
	boolean removeExclude(String folder, String path);

	/**
	 * Modifies specified inclusion mapping for the folder
	 * 
	 * @param folder
	 *            for which mapping should be modified
	 * @param mapping
	 *            which should be modified
	 * @return
	 */
	boolean modifyInclude(String folder, IMapping mapping);

	/**
	 * Modifies specified exclusion mapping for the folder
	 * 
	 * @param folder
	 *            for which mapping should be modified
	 * @param mapping
	 *            which should be modified
	 * @return
	 */
	boolean modifyExclude(String folder, IMapping mapping);

	/**
	 * Saves all changes in the resource mapping
	 * 
	 */
	void persist();

	/**
	 * Returns inclusion list for specified folder name
	 * 
	 * @param folder
	 * @return
	 */
	public Set<IMapping> getInclusion(String folder);

	/**
	 * Returns exclusion list for specified folder name
	 * 
	 * @param folder
	 * @return
	 */
	public Set<IMapping> getExclusion(String folder);

	/**
	 * Adds mapping change listener. It is notified about any change in the
	 * model.
	 * 
	 * @param listener
	 */
	void addMappingChangeListener(IMappingChangeListener listener);

	/**
	 * Removes specified mapping change listener.
	 * 
	 * @param listener
	 */
	void removeMappingChangeListener(IMappingChangeListener listener);

	/**
	 * Check if specified path is excluded for the folder
	 * 
	 * @param path
	 * @param folder
	 * @return
	 * @throws IOException
	 */
	boolean isExcluded(String folder, String path) throws IOException;

	/**
	 * @return all folder names specified in the resource mapping
	 */
	Set<String> getFolders();

	/**
	 * Return mapping for specified path.
	 * 
	 * @param path
	 * @return mapped folder name
	 * @throws IOException
	 */
	public String getFolder(String path) throws IOException;

}
