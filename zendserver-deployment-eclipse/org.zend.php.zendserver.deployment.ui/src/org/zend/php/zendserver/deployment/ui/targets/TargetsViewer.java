package org.zend.php.zendserver.deployment.ui.targets;

import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchListener;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.part.ResourceTransfer;
import org.zend.php.zendserver.deployment.core.targets.TargetsManagerService;
import org.zend.php.zendserver.deployment.ui.actions.EditTargetAction;
import org.zend.sdklib.event.IStatusChangeEvent;
import org.zend.sdklib.event.IStatusChangeListener;
import org.zend.sdklib.manager.TargetsManager;

/**
 * TreeViewer that shows targets configured in ZendSDK and
 * launch configurations assigned to them.
 */
public class TargetsViewer {

	private TreeViewer viewer;
	private IStatusChangeListener listener;

	/**
	 * Create viewer control
	 * 
	 * @param parent parent composite
	 */
	public void createControl(Composite parent) {
		viewer = new TreeViewer(parent);
		viewer.setContentProvider(new TargetsContentProvider());
		viewer.setLabelProvider(new TargetsLabelProvider());
		TargetsManager tm = TargetsManagerService.INSTANCE.getTargetManager();
		viewer.setInput(tm);
		listener = new IStatusChangeListener() {
			
			public void statusChanged(IStatusChangeEvent event) {
				viewer.refresh();
			}
		};
		tm.addStatusChangeListener(listener);
		
		// configure drag and drop
		int ops = DND.DROP_COPY | DND.DROP_MOVE;
		Transfer[] transfers = new Transfer[] { ResourceTransfer.getInstance()};
		viewer.addDropSupport(ops, transfers, new DropTransferListener(viewer));
		
		Control control = viewer.getControl();
		control.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		viewer.addDoubleClickListener(new IDoubleClickListener() {
			
			public void doubleClick(DoubleClickEvent event) {
				new EditTargetAction(viewer).run();
			}
		});
		
		DebugPlugin.getDefault().getLaunchManager().addLaunchListener(new ILaunchListener() {
			
			public void launchRemoved(ILaunch launch) {
				viewer.refresh();
			}
			
			public void launchChanged(ILaunch launch) {
				viewer.refresh();
			}
			
			public void launchAdded(ILaunch launch) {
				viewer.refresh();
			}
		});
	}
	
	public void dispose() {
		TargetsManager tm = TargetsManagerService.INSTANCE.getTargetManager();
		tm.removeStatusChangeListener(listener);
	}

	/**
	 * Sets focus to first active control on the targets viewer
	 */
	public void setFocus() {
		// TODO Auto-generated method stub
		
	}

	public ISelectionProvider getSelectionProvider() {
		return viewer;
	}

	public Viewer getViewer() {
		return viewer;
	}

}
