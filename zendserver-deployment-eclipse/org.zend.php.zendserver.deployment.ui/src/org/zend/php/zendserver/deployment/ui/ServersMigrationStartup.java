package org.zend.php.zendserver.deployment.ui;

import org.eclipse.php.internal.server.core.Server;
import org.zend.php.server.core.utils.ServerUtils;
import org.zend.php.server.ui.migration.AbstractMigrationService;
import org.zend.php.server.ui.types.LocalZendServerType;
import org.zend.php.zendserver.deployment.core.targets.ZendServerManager;
import org.zend.sdklib.manager.TargetsManager;
import org.zend.sdklib.target.IZendTarget;

/**
 * {@link AbstractMigrationService} implementation which is responsible for
 * migrating old PHP servers which do not have server type defined. It checks
 * following types:
 * <ul>
 * <li>Local Zend Server</li>
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
			if (isLocalZendServer(server)) {
				typeId = LocalZendServerType.ID;
			}
			if (typeId != null) {
				setType(server, typeId);
			}
			return true;
		}
		return false;
	}

	private boolean isLocalZendServer(Server server) {
		IZendTarget target = ServerUtils.getTarget(server);
		if (TargetsManager.isLocalhost(target)) {
			return true;
		} else {
			String enabled = server.getAttribute(
					ZendServerManager.ZENDSERVER_ENABLED_KEY,
					String.valueOf(false));
			if (Boolean.valueOf(enabled)) {
				return true;
			}
		}
		return false;
	}

}
