package org.zend.php.zendserver.deployment.debug.ui.commands;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.zend.sdklib.target.IZendTarget;

/**
 * Opens SSH tunnel. Expects single String parameter OpenTunnelCommand.CONTAINER
 * with name of container to open tunnel to. Called remotely by Google Chrome
 * extension.
 */
public class OpenTunnelCommand extends AbstractTunnelHandler {

	public Object execute(ExecutionEvent event) throws ExecutionException {
		IZendTarget target = getTarget(event);
		if (target != null) {
			openTunnel(target);
		}
		return null;
	}

}
