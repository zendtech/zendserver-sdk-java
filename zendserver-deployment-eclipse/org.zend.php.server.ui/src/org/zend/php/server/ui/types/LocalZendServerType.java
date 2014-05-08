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
import org.eclipse.php.internal.server.core.Server;
import org.eclipse.php.server.ui.types.IServerType;
import org.eclipse.swt.graphics.Image;
import org.zend.php.server.ui.ServersUI;

/**
 * @author Wojciech Galanciak, 2014
 * 
 */
public class LocalZendServerType implements IServerType {

	public static final String ID = "org.zend.php.server.ui.types.ZendServerType"; //$NON-NLS-1$
	public static final String LOCAL_ZEND_SERVER = "Local Zend Server";

	@Override
	public String getId() {
		return ID;
	}

	@Override
	public String getName() {
		return LOCAL_ZEND_SERVER;
	}

	@Override
	public Image getViewIcon() {
		return ServersUI.getDefault().getImage("icons/obj16/zend_server.png");
	}

	@Override
	public ImageDescriptor getWizardImage() {
		return ServersUI.getImageDescriptor("icons/wizban/zend_server_wiz.png");
	}

	@Override
	public boolean isCompatible(Server server) {
		String serverType = server.getAttribute(TYPE, null);
		return getId().equals(serverType);
	}

}
