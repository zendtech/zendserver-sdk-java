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

import org.eclipse.php.internal.server.core.Server;

/**
 * Server filter for defining which servers should be visible in
 * {@link ServersCombo}.
 * 
 * @author Wojciech Galanciak, 2014
 * @see ServersCombo
 */
@SuppressWarnings("restriction")
public interface IServerFilter {

	/**
	 * @param servers
	 *            all servers
	 * @return list of servers which match defined filter.
	 */
	Server[] filter(Server[] servers);

}