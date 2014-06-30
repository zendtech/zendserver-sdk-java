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

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.php.server.ui.types.IServerType;
import org.eclipse.swt.graphics.Image;
import org.zend.php.server.internal.ui.Messages;
import org.zend.php.server.internal.ui.ServersUI;

/**
 * Representation of Zend Developer Cloud server type.
 * 
 * @author Wojciech Galanciak, 2014
 * 
 */
public class PhpcloudServerType implements IServerType {

	public static final String ID = "org.zend.php.server.ui.types.PhpcloudServerType"; //$NON-NLS-1$

	@Override
	public String getId() {
		return ID;
	}

	@Override
	public String getName() {
		return Messages.PhpcloudServerType_Name;
	}
	
	@Override
	public String getDescription() {
		return Messages.PhpcloudServerType_Description;
	}

	@Override
	public Image getViewIcon() {
		return ServersUI.getDefault().getImage(ServersUI.PHPCLOUD_ICON);
	}
	
	@Override
	public Image getTypeIcon() {
		return ServersUI.getDefault().getImage(ServersUI.PHPCLOUD_TYPE_ICON);
	}

	@Override
	public ImageDescriptor getWizardImage() {
		return ServersUI.getImageDescriptor(ServersUI.PHPCLOUD_WIZ);
	}

}
