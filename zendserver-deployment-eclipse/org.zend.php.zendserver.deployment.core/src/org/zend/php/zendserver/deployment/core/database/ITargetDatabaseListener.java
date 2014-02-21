/*******************************************************************************
 * Copyright (c) 2012 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.php.zendserver.deployment.core.database;

/**
 * Listener for {@link ITargetDatabase} state changes.
 * 
 * @author Wojciech Galanciak, 2012
 * 
 */
public interface ITargetDatabaseListener {

	/**
	 * Connection state changed.
	 * 
	 * @param targetDatabase
	 *            instance of {@link ITargetDatabase} which changed its state
	 * @param state
	 *            new state
	 */
	void stateChanged(ITargetDatabase targetDatabase, ConnectionState state);

}
