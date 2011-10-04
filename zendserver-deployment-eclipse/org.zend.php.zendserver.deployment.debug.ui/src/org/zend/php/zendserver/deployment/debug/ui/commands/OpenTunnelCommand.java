package org.zend.php.zendserver.deployment.debug.ui.commands;

import java.io.IOException;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.osgi.util.NLS;
import org.zend.php.zendserver.deployment.core.targets.TargetsManagerService;
import org.zend.php.zendserver.deployment.debug.core.tunnel.ZendDevCloudTunnelManager;
import org.zend.sdklib.target.IZendTarget;

/**
 * Opens SSH tunnel.
 * Expects single String parameter OpenTunnelCommand.CONTAINER with name of container to open tunnel to.
 * Called remotely by Google Chrome extension.
 */
public class OpenTunnelCommand extends AbstractHandler {

	private static final String CONTAINER = "container"; //$NON-NLS-1$

	public Object execute(ExecutionEvent event) throws ExecutionException {
		String containerName = event.getParameter(CONTAINER);
		
		IZendTarget target = TargetsManagerService.INSTANCE.getContainerByName(containerName);
		if (target == null) {
			throw new ExecutionException(NLS.bind("Unknown container '{0}'. Please add this container in Zend Studio to continue.", containerName)); 
		}
		
		try {
			ZendDevCloudTunnelManager.getManager().connect(target);
		} catch (IOException e) {
			throw new ExecutionException(NLS.bind("An error occured while connecting the SSH tunnel to '{0}' container: {1}", containerName, e.getMessage()), e);
		}
		
		return null;
	}

}
