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

import org.eclipse.core.resources.IProject;
import org.eclipse.php.internal.server.core.Server;

/**
 * Contribution to drag and drop function in PHP Servers view.
 * 
 * @author Wojciech Galanciak, 2014
 * 
 */
@SuppressWarnings("restriction")
public interface IDragAndDropContribution {

	public void performAction(Server server, IProject project);

	public boolean isAvailable(Server server);

	public boolean isSupported(Server server, IProject project);

}
