package org.zend.php.zendserver.deployment.debug.ui.commands;

import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.expressions.EvaluationContext;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.zend.php.zendserver.deployment.core.targets.TargetsManagerService;
import org.zend.php.zendserver.deployment.debug.core.tunnel.ZendDevCloudTunnelManager;
import org.zend.php.zendserver.deployment.debug.ui.Messages;
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
		IZendTarget target = null;
		
		if (containerName != null) {
			target = TargetsManagerService.INSTANCE
					.getContainerByName(containerName);
		} else {
			EvaluationContext ctx = (EvaluationContext) event
					.getApplicationContext();
			Object element = ctx.getDefaultVariable();
			if (element instanceof List) {
				List<?> list = (List<?>) element;
				if (list.size() > 0) {
					element = list.get(0);
				}
			}
			if (element instanceof IZendTarget) {
				target = (IZendTarget) element;
			}
		}
		if (target == null) {
			throw new ExecutionException(NLS.bind("Unknown container '{0}'. Please add this container in Zend Studio to continue.", containerName)); 
		}
		
		try {
			if (!ZendDevCloudTunnelManager.getManager().connect(target)) {
				throw new IllegalStateException(
						"Openning SSH Tunnel is possible only for phpcloud targets.");
			}
		} catch (Exception e) {
			Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow()
					.getShell();
			MessageDialog.openError(shell,
					Messages.DeploymentHandler_sshTunnelErrorTitle,
					e.getMessage());
			throw new ExecutionException(NLS.bind("An error occured while connecting the SSH tunnel to '{0}' container: {1}", containerName, e.getMessage()), e);
		}
		
		return null;
	}

}
