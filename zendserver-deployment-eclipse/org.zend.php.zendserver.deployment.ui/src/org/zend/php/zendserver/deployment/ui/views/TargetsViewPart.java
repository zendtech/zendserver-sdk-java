package org.zend.php.zendserver.deployment.ui.views;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.part.ViewPart;
import org.zend.php.zendserver.deployment.ui.actions.AddTargetAction;
import org.zend.php.zendserver.deployment.ui.actions.DetectTargetAction;
import org.zend.php.zendserver.deployment.ui.actions.EditTargetAction;
import org.zend.php.zendserver.deployment.ui.actions.RefreshViewerAction;
import org.zend.php.zendserver.deployment.ui.actions.RemoveTargetAction;
import org.zend.php.zendserver.deployment.ui.targets.TargetsViewer;

/**
 * Targets view
 */
public class TargetsViewPart extends ViewPart {

	private TargetsViewer tv;

	public TargetsViewPart() {
		tv = new TargetsViewer();
	}

	@Override
	public void createPartControl(Composite parent) {
		tv.createControl(parent);
		
		IActionBars actionBars = getViewSite().getActionBars();
		IToolBarManager toolBarManager = actionBars.getToolBarManager();
		toolBarManager.add(new DetectTargetAction());
		toolBarManager.add(new AddTargetAction());
		toolBarManager.add(new EditTargetAction(tv.getSelectionProvider()));
		toolBarManager.add(new RemoveTargetAction(tv.getSelectionProvider()));
		
		IMenuManager menuManager = actionBars.getMenuManager();
		menuManager.add(new RefreshViewerAction(tv.getViewer()));
		
		getViewSite().registerContextMenu(tv.getMenuManager(), tv.getViewer());
		getViewSite().setSelectionProvider(tv.getViewer());
	}

	@Override
	public void dispose() {
		tv.dispose();
	}
	
	@Override
	public void setFocus() {
		tv.setFocus();
	}

}
