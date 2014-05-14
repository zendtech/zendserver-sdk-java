/*******************************************************************************
 * Copyright (c) 2014 Zend Technologies.
 * All rights reserved. This program and the accompanying materials
 * are the copyright of Zend Technologies and is protected under
 * copyright laws of the United States.
 * You must not copy, adapt or redistribute this document for 
 * any use. 
 *
 *******************************************************************************/
package org.zend.php.zendserver.deployment.ui.zendserver;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.php.server.ui.types.IServerType;
import org.eclipse.swt.graphics.Image;
import org.zend.php.zendserver.deployment.ui.Activator;

/**
 * Representation of local Zend Server type.
 * 
 * @author Wojciech Galanciak, 2014
 * 
 */
public class LocalZendServerType implements IServerType {

	public static final String ID = "org.zend.php.zendserver.deployment.ui.zendserver.LocalZendServerType"; //$NON-NLS-1$ 

	public String getId() {
		return ID;
	}

	public String getName() {
		return Messages.LocalZendServerType_Name;
	}

	public Image getViewIcon() {
		return Activator.getDefault()
				.getImage(Activator.IMAGE_ZEND_SERVER_ICON);
	}

	public ImageDescriptor getWizardImage() {
		return Activator.getImageDescriptor(Activator.IMAGE_ZEND_SERVER_WIZ);
	}

}
