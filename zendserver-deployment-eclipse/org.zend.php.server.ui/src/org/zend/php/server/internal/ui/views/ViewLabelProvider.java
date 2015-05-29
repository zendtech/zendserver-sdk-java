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

import org.eclipse.jface.resource.FontDescriptor;
import org.eclipse.jface.viewers.IFontProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.php.internal.server.core.Server;
import org.eclipse.php.internal.server.core.manager.ServersManager;
import org.eclipse.php.internal.server.core.types.IServerType;
import org.eclipse.php.internal.server.core.types.ServerTypesManager;
import org.eclipse.php.internal.server.ui.types.IServerTypeDescriptor;
import org.eclipse.php.internal.server.ui.types.ServerTypesDescriptorRegistry;
import org.eclipse.php.internal.server.ui.types.IServerTypeDescriptor.ImageType;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.zend.php.server.internal.ui.Messages;
import org.zend.php.server.ui.ServersUI;
import org.zend.php.zendserver.deployment.core.database.ConnectionState;
import org.zend.php.zendserver.deployment.core.database.ITargetDatabase;

/**
 * Label provider for PHP Servers view.
 * 
 * @author Wojciech Galanciak, 2014
 * 
 */
@SuppressWarnings("restriction")
class ViewLabelProvider extends LabelProvider implements IFontProvider {

	private Font defautFont;
	private Font boldFont;

	public ViewLabelProvider(Font defautFont) {
		super();
		this.defautFont = defautFont;
		FontDescriptor boldDescriptor = FontDescriptor.createFrom(defautFont)
				.setStyle(SWT.BOLD);
		this.boldFont = boldDescriptor.createFont(Display.getDefault());
	}

	@Override
	public String getText(Object obj) {
		if (obj instanceof Server) {
			Server server = (Server) obj;
			return MessageFormat.format(
					Messages.ViewLabelProvider_ServersViewLabel,
					server.getName(), server.getBaseURL());
		} else if (obj instanceof ITargetDatabase) {
			ConnectionState state = ((ITargetDatabase) obj).getState();
			return "MySQL Connection (" + state.getLabel() + ")"; //$NON-NLS-1$ //$NON-NLS-2$
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
				IServerTypeDescriptor typeDescriptor = ServerTypesDescriptorRegistry.getDescriptor(type);
				return typeDescriptor.getImage(ImageType.ICON_16);
			} else {
				return ServersUI.getDefault().getImage(
						ServersUI.DEFAULT_SERVER_ICON);
			}
		} else if (obj instanceof ITargetDatabase) {
			Image result = null;
			ConnectionState state = ((ITargetDatabase) obj).getState();
			switch (state) {
			case CONNECTED:
				result = ServersUI.getDefault().getImage(
						ServersUI.IMAGE_DATABASE_ON);
				break;
			case DISCONNECTED:
				result = ServersUI.getDefault().getImage(
						ServersUI.IMAGE_DATABASE_OFF);
				break;
			case UNAVAILABLE:
				result = ServersUI.getDefault().getImage(
						ServersUI.IMAGE_DATABASE_CREATE);
				break;
			default:
				result = ServersUI.getDefault().getImage(
						ServersUI.IMAGE_DATABASE);
				break;
			}
			return result;
		}
		return super.getImage(obj);
	}

	@Override
	public Font getFont(Object obj) {
		if (obj instanceof Server) {
			Server server = (Server) obj;
			if (server.equals(ServersManager.getDefaultServer(null))) {
				return boldFont;
			}
		}
		return defautFont;
	}
}