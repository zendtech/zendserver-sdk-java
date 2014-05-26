/*******************************************************************************
 * Copyright (c) 2014 Zend Technologies Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Zend Technologies Ltd. - initial API and implementation
 *******************************************************************************/
package org.zend.php.server.ui;

/**
 * Listener for Add Server action from ServersCombo.
 * {@link IAddServerListener#serverAdded(String)} is called after Add Server
 * action is finished.
 * 
 * @author Wojciech Galanciak, 2014
 * 
 */
public interface IAddServerListener {

	/**
	 * This method is called after Add Server action is finished. In the case
	 * when new server is not added the method is not called.
	 * 
	 * @param name
	 *            name of new server
	 */
	void serverAdded(String name);

}