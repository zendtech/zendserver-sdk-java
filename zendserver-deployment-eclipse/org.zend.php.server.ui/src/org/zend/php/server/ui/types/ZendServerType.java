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
package org.zend.php.server.ui.types;

import org.eclipse.php.server.core.types.IServerType;
import org.zend.php.server.internal.ui.Messages;

/**
 * Representation of Zend Server type.
 * 
 * @author Wojciech Galanciak, 2014
 * 
 */
public class ZendServerType implements IServerType {

	public static final String ID = "org.zend.php.server.ui.types.ZendServerType"; //$NON-NLS-1$

	@Override
	public String getId() {
		return ID;
	}

	@Override
	public String getName() {
		return Messages.ZendServerType_Name;
	}

	@Override
	public String getDescription() {
		return Messages.ZendServerType_Description;
	}

}
