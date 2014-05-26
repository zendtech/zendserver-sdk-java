/*******************************************************************************
 * Copyright (c) 2012 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.php.zendserver.deployment.core.database;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.zend.php.zendserver.deployment.core.DeploymentCore;
import org.zend.sdklib.target.IZendTarget;

/**
 * Represents connection between target and connection profile. It can be used
 * for managing connection profile associated with the target.
 * 
 * @author Wojciech Galanciak, 2012
 * 
 */
public interface ITargetDatabase {

	static final String TARGET_ID = DeploymentCore.PLUGIN_ID + ".targetId"; //$NON-NLS-1$

	/**
	 * Create connection profile for the target.
	 * 
	 * @return <code>true</code> if profile was created successfully; otherwise
	 *         return <code>false</code>
	 */
	boolean createProfile();

	/**
	 * @return <code>true</code> if password is available; otherwise return
	 *         <code>false</code>
	 */
	boolean hasPassword();

	/**
	 * @return <code>true</code> if password should be saved; otherwise return
	 *         <code>false</code>
	 */
	boolean isSavePassword();

	/**
	 * Sets if password should be saved or not.
	 * 
	 * @param save
	 */
	void setSavePassword(boolean save);

	/**
	 * Connect to database using associated connection profile.
	 * 
	 * @param monitor
	 *            progress monitor
	 * @return <code>true</code> if connected successfully; otherwise return
	 *         <code>false</code>
	 */
	boolean connect(IProgressMonitor monitor);

	/**
	 * Disconnect the database connection using associated connection profile.
	 */
	void disconnect();

	/**
	 * @return current connection state
	 * @see ConnectionState
	 */
	ConnectionState getState();

	/**
	 * Set container password for associated connection profile.
	 * 
	 * @param password
	 *            container password
	 */
	void setPassword(String password);

	/**
	 * Remove associated connection profile. After that operation next call of
	 * {@link ITargetDatabase#createProfile()} will create new profile.
	 */
	void remove();

	/**
	 * @return associated zend target
	 */
	IZendTarget getTarget();

	/**
	 * @return result of connection process
	 */
	IStatus getResult();

}