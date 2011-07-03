/*******************************************************************************
 * Copyright (c) Jun 27, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.sdklib.mapping;

import java.io.IOException;
import java.util.List;

import org.zend.sdklib.mapping.IMappingEntry.Type;

/**
 * Interface for a mapping model. It provides all necessary operations for
 * getting information about a resource mapping. It also allows for a mapping
 * model modifications.
 * 
 * @author Wojciech Galanciak, 2011
 * 
 */
public interface IMappingModel {

	static final String APPDIR = "appdir";
	static final String SCRIPTSDIR = "scriptsdir";

	/**
	 * Adds specified entry to the model.
	 * 
	 * @param toAdd
	 *            entry which should be added to the model
	 * @return <code>true</code> if entry was added successfully; otherwise
	 *         return <code>false</code>.
	 */
	boolean addEntry(IMappingEntry toAdd);

	/**
	 * Removes entry with specified name from the model.
	 * 
	 * @param toRemove
	 * @return <code>true</code> if entry was removed successfully; otherwise
	 *         return <code>false</code>.
	 */
	boolean removeEntry(String toRemove);

	/**
	 * Adds new mapping for specified folder and entry type.
	 * 
	 * @param folder
	 * @param type
	 * @param mapping
	 * @return <code>true</code> if mapping was added successfully; otherwise
	 *         return <code>false</code>.
	 */
	boolean addMapping(String folder, Type type, IMapping mapping);

	/**
	 * Removes mapping for specified folder and entry type.
	 * 
	 * @param folder
	 * @param type
	 * @param path
	 * @return <code>true</code> if mapping was removed successfully; otherwise
	 *         return <code>false</code>.
	 */
	boolean removeMapping(String folder, Type type, String path);

	/**
	 * Modifies mapping for specified folder and entry type.
	 * 
	 * @param folder
	 * @param type
	 * @param mapping
	 * @return <code>true</code> if mapping was modified successfully; otherwise
	 *         return <code>false</code>.
	 */
	boolean modifyMapping(String folder, Type type, IMapping mapping);

	/**
	 * @param folder
	 * @param type
	 * @return entry for specified folder and entry type
	 */
	IMappingEntry getEntry(String folder, Type type);

	/**
	 * @return all entries in the model
	 */
	List<IMappingEntry> getEnties();

	/**
	 * Saves all changes in the resource mapping
	 * 
	 * @throws IOException
	 * 
	 */
	void store() throws IOException;

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
	List<String> getFolders();

	/**
	 * Return mapping for specified path.
	 * 
	 * @param path
	 * @return mapped folder name
	 * @throws IOException
	 */
	String getFolder(String path) throws IOException;

}
