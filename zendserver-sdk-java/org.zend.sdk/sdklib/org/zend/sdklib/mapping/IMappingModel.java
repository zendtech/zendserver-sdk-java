/*******************************************************************************
 * Copyright (c) Jun 27, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.sdklib.mapping;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
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

	public static final String APPDIR = "appdir";
	public static final String SCRIPTSDIR = "scriptsdir";
	public static final String LIBRARY = "library";

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
	 * @param type
	 * @return <code>true</code> if entry was removed successfully; otherwise
	 *         return <code>false</code>.
	 */
	boolean removeEntry(String toRemove, Type type);

	/**
	 * Adds new mapping for specified folder and entry type.
	 * 
	 * @param folder
	 * @param type
	 * @param mapping
	 * @return <code>true</code> if mapping was added successfully; otherwise
	 *         return <code>false</code>.
	 */
	boolean addMapping(String folder, Type type, String path, boolean isGlobal);

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
	 * @return all entries in the model with given type (e.g. INCLUDE) and tag (eg. 'scriptsdir') 
	 */
	List<IMappingEntry> getEnties(Type type, String tag);

	/**
	 * Saves all changes in the resource mapping
	 * 
	 * @throws IOException
	 * 
	 */
	void store() throws IOException;


	/**
	 * Load mapping (overwrite existing one) from the given input stream and set
	 * new mapping file.
	 * 
	 * @param stream
	 *            input stream
	 * @param mappingFile
	 *            mapping file
	 * @throws IOException
	 */
	void load(InputStream stream, File mappingFile) throws IOException;

	/**
	 * Load mapping (overwrite existing one) from the given input stream for the
	 * same mapping file.
	 * 
	 * @param stream
	 *            input stream
	 * @throws IOException
	 */
	void load(InputStream stream) throws IOException;

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
	 * @return return <code>true</code> if model was loaded from the provided
	 *         mapping file; otherwise return <code>false</code>
	 */
	boolean isLoaded();

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
	String[] getFolders(String path) throws IOException;

	/**
	 * Get project relative path to the given file based on the mapping model.
	 * 
	 * @param name
	 *            - file name
	 * @return relative path to the file or <code>null</code> if file is not
	 *         mapped
	 * @throws IOException
	 */
	String getPath(String name) throws IOException;

	/**
	 * Get package relative path to the given file based on the mapping model
	 * 
	 * @param folder
	 *            - folder name
	 * @param path
	 *            - project relative path to the file
	 * @return path in the deployment package or <code>null</code> if file is
	 *         not mapped
	 * @throws IOException
	 */
	String getPackagePath(String folder, String path) throws IOException;

	/**
	 * @return default exclusion list
	 */
	List<IMapping> getDefaultExclusion();

	IMappingLoader getLoader();

	File getMappingFile();


}
