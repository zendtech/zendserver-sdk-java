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
package org.zend.php.server.ui.actions;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.php.internal.server.core.Server;
import org.eclipse.php.server.ui.types.IServerType;

/**
 * Interface for providing actions related to specific server type.
 * 
 * @author Wojciech Galanciak, 2014
 * @see IServerType
 */
@SuppressWarnings("restriction")
public interface IActionContribution {

	/**
	 * Return action label. It may depends on a current state of server (if
	 * server was previously set by
	 * {@link IActionContribution#setServer(Server)} method. Behavior depends on
	 * specific implementation.
	 * 
	 * @return action label
	 */
	String getLabel();

	/**
	 * Return {@link ImageDescriptor} instance for action icon.
	 * 
	 * @return action icon
	 */
	ImageDescriptor getIcon();

	/**
	 * Run specific action.
	 */
	void run();

	/**
	 * Set server which will be proceeded by the action.
	 * 
	 * @param server
	 *            {@link Server} instance
	 */
	void setServer(Server server);

	/**
	 * @param server
	 *            {@link Server} instance
	 * @return <code>true</code> if action is available for specified
	 *         {@link Server} instance; otherwise return <code>false</code>
	 */
	boolean isAvailable(Server server);

}
