/*******************************************************************************
 * Copyright (c) 2014 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.php.server.internal.ui.startup;

import java.net.MalformedURLException;

import org.eclipse.php.internal.server.core.Server;
import org.eclipse.php.internal.server.core.manager.ServersManager;
import org.zend.php.server.core.utils.ServerUtils;
import org.zend.php.server.internal.ui.ServersUI;
import org.zend.php.server.ui.migration.AbstractMigrationService;
import org.zend.php.server.ui.types.LocalApacheType;
import org.zend.php.server.ui.types.OpenShiftServerType;
import org.zend.php.server.ui.types.ZendServerType;
import org.zend.php.zendserver.deployment.core.targets.ZendServerManager;
import org.zend.php.zendserver.deployment.core.tunnel.SSHTunnelConfiguration;
import org.zend.sdklib.manager.TargetsManager;
import org.zend.sdklib.target.IZendTarget;

/**
 * {@link AbstractMigrationService} implementation which is responsible for
 * migrating old PHP servers which do not have server type defined. It checks
 * following types:
 * <ul>
 * <li>Phpcloud</li>
 * <li>OpenShift</li>
 * <li>Zend Server</li>
 * <li>Local Apache HTTP Server</li>
 * </ul>
 * 
 * @author Wojciech Galanciak, 2014
 * 
 */
@SuppressWarnings("restriction")
public class ServersMigrationStartup extends AbstractMigrationService {

	@Override
	protected boolean migrate(Server server) {
		String typeId = getServerType(server);
		if (typeId == null) {
			if (isOpenShift(server)) {
				SSHTunnelConfiguration config = SSHTunnelConfiguration
						.createOpenShiftConfiguration(ServerUtils
								.getTarget(server));
				config.store(server);
				typeId = OpenShiftServerType.ID;
			} else if (isPhpcloud(server)) {
				SSHTunnelConfiguration config = SSHTunnelConfiguration
						.createPhpcloudConfiguration(server,
								ServerUtils.getTarget(server));
				config.store(server);
				typeId = ZendServerType.ID;
			} else if (isZendServer(server)) {
				typeId = ZendServerType.ID;
				return true;
			} else if (isLocalApache(server)) {
				typeId = LocalApacheType.ID;
			}
			if (typeId != null) {
				setType(server, typeId);
				return true;
			}
		}
		return updateDefaultPHPWebServer(server);
	}

	private boolean updateDefaultPHPWebServer(Server server) {
		if ("Default PHP Web Server".equals(server.getName()) //$NON-NLS-1$
				&& "http://localhost".equals(server.getBaseURL()) //$NON-NLS-1$
				&& server.getDocumentRoot().isEmpty()) {
			if (ServersManager.getServer(ServersManager.DEFAULT_SERVER_NAME) != null) {
				ServersManager.removeServer(ServersManager.DEFAULT_SERVER_NAME);
			}
			server.setName(ServersManager.DEFAULT_SERVER_NAME);
			server.setAttribute(ServersManager.EMPTY_SERVER,
					String.valueOf(true));
			try {
				server.setBaseURL("http://<no_php_server>"); //$NON-NLS-1$
				return true;
			} catch (MalformedURLException e) {
				ServersUI.logError(e);
			}
		}
		return false;
	}

	private boolean isLocalApache(Server server) {
		// TODO how we can check that?
		return false;
	}

	private boolean isOpenShift(Server server) {
		IZendTarget target = ServerUtils.getTarget(server);
		return TargetsManager.isOpenShift(target);
	}

	private boolean isPhpcloud(Server server) {
		IZendTarget target = ServerUtils.getTarget(server);
		return TargetsManager.isPhpcloud(target);
	}

	private boolean isZendServer(Server server) {
		IZendTarget target = ServerUtils.getTarget(server);
		if (target != null) {
			return !TargetsManager.isLocalhost(target);
		}
		String enabled = server
				.getAttribute(ZendServerManager.ZENDSERVER_ENABLED_KEY,
						String.valueOf(false));
		return Boolean.valueOf(enabled);
	}

}
