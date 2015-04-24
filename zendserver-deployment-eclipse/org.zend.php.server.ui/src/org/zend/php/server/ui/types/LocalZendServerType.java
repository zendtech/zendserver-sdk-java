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

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.php.server.ui.types.IServerType;
import org.eclipse.swt.graphics.Image;
import org.zend.php.server.internal.ui.IHelpContextIds;
import org.zend.php.server.internal.ui.Messages;
import org.zend.php.server.internal.ui.ServersUI;

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

	public Image getViewIcon() {
		return ServersUI.getDefault().getImage(ServersUI.ZEND_SERVER_ICON);
	}

	@Override
	public Image getTypeIcon() {
		return ServersUI.getDefault().getImage(ServersUI.ZEND_SERVER_TYPE_ICON);
	}

	public ImageDescriptor getWizardImage() {
		return ServersUI.getImageDescriptor(ServersUI.ZEND_SERVER_WIZ);
	}
	
	@Override
	public String getHelp() {
		return IHelpContextIds.ADDING_A_SERVER_LOCAL_ZEND_SERVER;
	}

}
