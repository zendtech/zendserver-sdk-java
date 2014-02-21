package org.zend.php.zendserver.deployment.ui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.Viewer;
import org.zend.php.zendserver.deployment.core.targets.TargetsManagerService;
import org.zend.php.zendserver.deployment.ui.Activator;
import org.zend.php.zendserver.deployment.ui.Messages;

/**
 * Calls refresh() on provided JFace viewer.
 */
public class RefreshViewerAction extends Action {

	private Viewer viewer;

	public RefreshViewerAction(Viewer viewer) {
		super(Messages.RefreshViewerAction_Refresh, Activator.getImageDescriptor(Activator.IMAGE_REFRESH));
		this.viewer = viewer;
	}
	
	@Override
	public void run() {
		// reload targets
		TargetsManagerService.INSTANCE.getTargetManager().reload();
		viewer.refresh();
	}
}
