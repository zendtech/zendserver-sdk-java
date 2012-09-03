package org.zend.php.zendserver.deployment.debug.ui.commands;

import java.text.MessageFormat;
import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.expressions.IEvaluationContext;
import org.eclipse.osgi.util.NLS;
import org.zend.core.notifications.NotificationManager;
import org.zend.php.zendserver.deployment.core.targets.TargetsManagerService;
import org.zend.php.zendserver.deployment.core.tunnel.AbstractSSHTunnel.State;
import org.zend.php.zendserver.deployment.core.tunnel.SSHTunnelManager;
import org.zend.php.zendserver.deployment.debug.ui.Messages;
import org.zend.php.zendserver.monitor.core.Activator;
import org.zend.sdklib.manager.TargetsManager;
import org.zend.sdklib.target.IZendTarget;

/**
 * Opens SSH tunnel. Expects single String parameter OpenTunnelCommand.CONTAINER
 * with name of container to open tunnel to. Called remotely by Google Chrome
 * extension.
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
			IEvaluationContext ctx = (IEvaluationContext) event
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
			throw new ExecutionException(NLS.bind(
					Messages.OpenTunnelCommand_UnknownContainer, containerName));
		}
		openTunnel(target);
		return null;
	}

	private boolean openTunnel(IZendTarget target) {
		try {
			State result = null;
			if (TargetsManager.isPhpcloud(target)
					|| TargetsManager.isOpenShift(target)) {
				result = SSHTunnelManager.getManager().connect(target);
			}
			switch (result) {
			case CONNECTED:
				String message = MessageFormat.format(
						Messages.OpenTunnelCommand_TunnelOpenedMessage,
						target.getId());
				NotificationManager.registerInfo(
						Messages.OpenTunnelCommand_OpenTunnelTitle, message,
						4000);
				break;
			case CONNECTING:
				message = MessageFormat.format(
						Messages.OpenTunnelCommand_SuccessMessage,
						target.getId());
				NotificationManager.registerInfo(
						Messages.OpenTunnelCommand_OpenTunnelTitle, message,
						4000);
				break;
			case NOT_SUPPORTED:
				NotificationManager.registerWarning(
						Messages.OpenTunnelCommand_OpenTunnelTitle,
						Messages.OpenTunnelCommand_NotSupportedMessage, 4000);
				break;
			default:
				break;
			}
			return true;
		} catch (Exception e) {
			Activator.log(e);
			String message = MessageFormat.format(
					Messages.DeploymentHandler_sshTunnelErrorTitle,
					target.getId());
			NotificationManager.registerError(
					Messages.OpenTunnelCommand_OpenTunnelTitle, message, 4000);
		}
		return false;
	}

}
