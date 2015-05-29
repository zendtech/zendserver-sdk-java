/*******************************************************************************
 * Copyright (c) 2014 Zend Technologies.
 * All rights reserved. This program and the accompanying materials
 * are the copyright of Zend Technologies and is protected under
 * copyright laws of the United States.
 * You must not copy, adapt or redistribute this document for 
 * any use. 
 *
 *******************************************************************************/
package org.zend.php.server.ui.types;

import org.eclipse.php.internal.server.core.types.IServerType;
import org.zend.php.server.internal.ui.Messages;

/**
 * Representation of local Zend Server type.
 * 
 * @author Wojciech Galanciak, 2014
 * 
 */
public class LocalZendServerType implements IServerType {

	public static final String ID = "org.zend.php.server.ui.types.LocalZendServerType"; //$NON-NLS-1$ 

	public String getId() {
		return ID;
	}

	public String getName() {
		return Messages.LocalZendServerType_Name;
	}

	public String getDescription() {
		return Messages.LocalZendServerType_Description;
	}

}
