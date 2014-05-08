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
package org.zend.php.server.internal.ui.views;

import java.text.MessageFormat;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.php.internal.server.core.Server;
import org.eclipse.php.server.ui.types.IServerType;
import org.eclipse.php.server.ui.types.ServerTypesManager;
import org.eclipse.swt.graphics.Image;
import org.zend.php.server.internal.ui.Messages;
import org.zend.php.server.internal.ui.ServersUI;

/**
 * @author Wojciech Galanciak, 2014
 * 
 */
@SuppressWarnings("restriction")
class ViewLabelProvider extends LabelProvider {

	@Override
	public String getText(Object obj) {
		if (obj instanceof Server) {
			Server server = (Server) obj;
			return MessageFormat.format(
					Messages.ViewLabelProvider_ServersViewLabel,
					server.getName(), server.getBaseURL());
		}
		return super.getText(obj);
	}

	@Override
	public Image getImage(Object obj) {
		if (obj instanceof Server) {
			Server server = (Server) obj;
			String typeName = server.getAttribute(IServerType.TYPE, null);
			IServerType type = ServerTypesManager.getInstance().getType(
					typeName);
			if (type != null) {
				return type.getViewIcon();
			} else {
				return ServersUI.getDefault().getImage(
						ServersUI.DEFAULT_SERVER_ICON);
			}
		}
		return super.getImage(obj);
	}

}