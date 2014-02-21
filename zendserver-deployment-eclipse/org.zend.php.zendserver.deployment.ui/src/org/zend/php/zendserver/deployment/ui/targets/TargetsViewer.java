package org.zend.php.zendserver.deployment.ui.targets;

import org.eclipse.core.runtime.Platform;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationListener;
import org.eclipse.debug.core.ILaunchListener;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.part.ResourceTransfer;
import org.zend.php.zendserver.deployment.core.database.ConnectionState;
import org.zend.php.zendserver.deployment.core.database.ITargetDatabase;
import org.zend.php.zendserver.deployment.core.database.ITargetDatabaseListener;
import org.zend.php.zendserver.deployment.core.database.TargetsDatabaseManager;
import org.zend.php.zendserver.deployment.core.targets.TargetsManagerService;
import org.zend.php.zendserver.deployment.ui.actions.EditTargetAction;
import org.zend.php.zendserver.deployment.ui.actions.OpenDatabaseConnectionAction;
import org.zend.sdklib.manager.TargetsManager;
import org.zend.sdklib.target.IZendTarget;
import org.zend.webapi.core.progress.IStatusChangeEvent;
import org.zend.webapi.core.progress.IStatusChangeListener;

/**
 * TreeViewer that shows targets configured in ZendSDK and
 * launch configurations assigned to them.
 */
public class TargetsViewer {

	private TreeViewer viewer;
	private IStatusChangeListener listener;
	private MenuManager menuMgr;
	private ILaunchListener launchListener;
	private ILaunchConfigurationListener cfgChangeListener;
	private ITargetDatabaseListener targetDatabaseListener;

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
				refreshViewer();
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
				handleDoubleClick(event);
			}
		});
		
		menuMgr = new MenuManager("#PopupMenu"); //$NON-NLS-1$
		menuMgr.add(new Separator("popupGroup1")); //$NON-NLS-1$
		menuMgr.add(new Separator("popupGroup2")); //$NON-NLS-1$
		menuMgr.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
		
		Menu menu = menuMgr.createContextMenu(viewer.getControl());
		viewer.addPostSelectionChangedListener(new ISelectionChangedListener() {
			
			public void selectionChanged(SelectionChangedEvent event) {
				menuMgr.updateAll(true);
			}
		});
		viewer.getControl().setMenu(menu);
		
		cfgChangeListener = new ILaunchConfigurationListener() {
			
			public void launchConfigurationRemoved(ILaunchConfiguration configuration) {
				refreshViewer();
			}
			
			public void launchConfigurationChanged(ILaunchConfiguration configuration) {
			}
			
			public void launchConfigurationAdded(ILaunchConfiguration configuration) {
			}
		};
		DebugPlugin.getDefault().getLaunchManager().addLaunchConfigurationListener(cfgChangeListener);
		
		launchListener = new ILaunchListener() {
			
			public void launchRemoved(ILaunch launch) {
				refreshViewer();
			}
			
			public void launchChanged(ILaunch launch) {
				refreshViewer();
			}
			
			public void launchAdded(ILaunch launch) {
				refreshViewer();
			}
		};
		DebugPlugin.getDefault().getLaunchManager().addLaunchListener(launchListener);

		targetDatabaseListener = new ITargetDatabaseListener() {

			public void stateChanged(ITargetDatabase targetDatabase,
					ConnectionState state) {
				refreshViewer();
			}
		};
		TargetsDatabaseManager.getManager().addTargetDatabaseListener(
				targetDatabaseListener);
	}
	
	protected void handleDoubleClick(DoubleClickEvent event) {
		ISelection selection = event.getSelection();
		if (selection.isEmpty()) {
			return;
		}

		IStructuredSelection ssel = (IStructuredSelection) selection;
		final Object obj = ssel.getFirstElement();

		if (obj instanceof IZendTarget) {
			new EditTargetAction(viewer).run();
			return;
		}
		if (obj instanceof ITargetDatabase) {
			OpenDatabaseConnectionAction action = new OpenDatabaseConnectionAction(
					(ITargetDatabase) obj);
			action.run();
			return;
		}
		
		IDoubleClickListener adapter = (IDoubleClickListener) Platform.getAdapterManager().getAdapter(obj, IDoubleClickListener.class);
		if (adapter != null) {
			adapter.doubleClick(event);
		}
	}

	protected void refreshViewer() {
		if (viewer == null || viewer.getTree() == null
				|| viewer.getTree().isDisposed()) {
			return;
		}
		viewer.getControl().getDisplay().asyncExec(new Runnable() {
			public void run() {
				viewer.refresh();
			}
		});
	}

	public void dispose() {
		TargetsManager tm = TargetsManagerService.INSTANCE.getTargetManager();
		tm.removeStatusChangeListener(listener);
		DebugPlugin.getDefault().getLaunchManager().removeLaunchConfigurationListener(cfgChangeListener);
		DebugPlugin.getDefault().getLaunchManager().removeLaunchListener(launchListener);
		TargetsDatabaseManager.getManager().removeTargetDatabaseListener(targetDatabaseListener);
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

	public TreeViewer getViewer() {
		return viewer;
	}
	
	public MenuManager getMenuManager() {
		return menuMgr;
	}

}
